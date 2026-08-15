package com.example.web_bansach.security.principal;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Thông tin định danh người dùng dành cho Spring Security.
 * Biểu diễn người dùng đã xác thực cùng các quyền được cấp.
 */
@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private Set<String> roles;

    @Override
    // Trả danh sách quyền để Spring Security kiểm tra endpoint.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> role != null && role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    // Trả mật khẩu đã mã hóa phục vụ xác thực.
    public String getPassword() {
        return password;
    }

    @Override
    // Trả email được dùng làm định danh đăng nhập.
    public String getUsername() {
        return username;
    }

    @Override
    // Cho biết tài khoản chưa hết hạn.
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    // Cho biết tài khoản chưa bị khóa.
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    // Cho biết thông tin xác thực chưa hết hạn.
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // Trả trạng thái kích hoạt của tài khoản.
    public boolean isEnabled() {
        return enabled;
    }
}
