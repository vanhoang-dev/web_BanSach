package com.example.web_bansach.security.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.web_bansach.security.jwt.JwtAuthenticationFilter;
import com.example.web_bansach.security.jwt.JwtTokenProvider;
import com.example.web_bansach.security.handler.JwtAccessDeniedHandler;
import com.example.web_bansach.security.handler.JwtAuthenticationEntryPoint;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
// Cấu hình xác thực, phân quyền, CORS và chuỗi bộ lọc bảo mật cho toàn bộ API.
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final String allowedOrigins;

    // Khởi tạo cấu hình với bộ xử lý JWT, bộ xử lý lỗi bảo mật và danh sách nguồn được phép gọi API.
    public SecurityConfig(JwtTokenProvider jwtTokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler,
            @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    // Cung cấp bộ mã hóa BCrypt dùng để băm và đối chiếu mật khẩu người dùng.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // Cung cấp trình quản lý xác thực để luồng đăng nhập kiểm tra thông tin tài khoản.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    // Khai báo chính sách CORS, cho phép các giao diện trong cấu hình gọi API kèm thông tin xác thực.
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Đọc và loại bỏ khoảng trắng trong danh sách nguồn được phép từ cấu hình ứng dụng.
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    // Xây dựng chuỗi bảo mật: tắt phiên máy chủ, xử lý lỗi, phân quyền đường dẫn và kiểm tra JWT.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Tạo bộ lọc đọc JWT từ mỗi yêu cầu trước bộ lọc đăng nhập mặc định của Spring Security.
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider);

        http
                // Áp dụng chính sách CORS đã khai báo ở trên cho toàn bộ API.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Tắt CSRF vì backend sử dụng JWT và không lưu phiên đăng nhập trên máy chủ.
                .csrf(csrf -> csrf.disable())
                // Không tạo HTTP session; mỗi yêu cầu phải tự gửi JWT để xác thực.
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Trả lỗi 401 khi chưa xác thực và lỗi 403 khi không đủ quyền.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Cho phép yêu cầu OPTIONS để trình duyệt thực hiện kiểm tra CORS trước khi gọi API.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Cho phép các đường dẫn WebSocket để thiết lập kết nối nhận thông báo thời gian thực.
                        .requestMatchers("/ws/**", "/app/**", "/topic/**", "/queue/**").permitAll()
                        .requestMatchers("/actuator/health/**", "/actuator/prometheus").permitAll()

                        // Các API công khai không yêu cầu đăng nhập.
                        .requestMatchers(HttpMethod.GET, Endpoints.PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, Endpoints.PUBLIC_POST_ENDPOINTS).permitAll()

                        // Các API người dùng cho phép cả quyền người dùng và quyền quản trị viên.
                        .requestMatchers(HttpMethod.GET, Endpoints.USER_GET_ENDPOINTS).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, Endpoints.USER_POST_ENDPOINTS).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, Endpoints.USER_PUT_ENDPOINTS).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.USER_DELETE_ENDPOINTS).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")

                        // Các API quản trị chỉ cho phép tài khoản có quyền quản trị viên.
                        .requestMatchers(HttpMethod.GET, Endpoints.ADMIN_GET_ENDPOINTS).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, Endpoints.ADMIN_POST_ENDPOINTS).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, Endpoints.ADMIN_PUT_ENDPOINTS).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, Endpoints.ADMIN_DELETE_ENDPOINTS).hasAuthority("ROLE_ADMIN")

                        // Mọi yêu cầu chưa khớp các nhóm trên đều bắt buộc phải đăng nhập.
                        .anyRequest().authenticated())
                // Chạy bộ lọc JWT trước bộ lọc xác thực tên đăng nhập và mật khẩu mặc định.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Hoàn tất và trả chuỗi bộ lọc bảo mật cho Spring quản lý.
        return http.build();
    }
}
