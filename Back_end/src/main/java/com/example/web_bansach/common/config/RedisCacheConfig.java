package com.example.web_bansach.common.config;

import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.example.web_bansach.common.cache.CacheNames;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
// Bật Spring Cache, lưu dữ liệu trong Redis và quy định thời gian sống cho từng nhóm dữ liệu.
public class RedisCacheConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Bean
    // Tạo bộ quản lý cache Redis với TTL riêng cho từng loại dữ liệu.
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                CacheNames.AUTHORS, createConfiguration(Duration.ofMinutes(30)),
                CacheNames.CATEGORIES, createConfiguration(Duration.ofHours(1)),
                CacheNames.BOOKS, createConfiguration(Duration.ofMinutes(10)),
                CacheNames.BOOK_REVIEWS, createConfiguration(Duration.ofMinutes(5)),
                CacheNames.DASHBOARD, createConfiguration(Duration.ofSeconds(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(createConfiguration(Duration.ofMinutes(10)))
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    // Tạo cấu hình chung: có TTL, không lưu giá trị null và thêm tiền tố để khóa dễ nhận biết.
    private RedisCacheConfiguration createConfiguration(Duration timeToLive) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(timeToLive)
                .disableCachingNullValues()
                .prefixCacheNameWith("bookstore::");
    }

    @Override
    // Nếu Redis tạm thời bị lỗi, bỏ qua cache để API tiếp tục đọc và ghi database bình thường.
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                logCacheError("đọc", exception, cache, key);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                logCacheError("ghi", exception, cache, key);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                logCacheError("xóa", exception, cache, key);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                logCacheError("xóa toàn bộ", exception, cache, null);
            }
        };
    }

    // Ghi cảnh báo ngắn gọn để quản trị viên biết Redis đang lỗi mà không làm hỏng request.
    private void logCacheError(String action, RuntimeException exception, Cache cache, Object key) {
        log.warn("Không thể {} cache '{}' với khóa '{}': {}",
                action, cache.getName(), key, exception.getMessage());
    }
}
