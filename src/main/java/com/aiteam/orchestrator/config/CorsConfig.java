package com.aiteam.orchestrator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS配置类 - 跨域请求支持
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有来源
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        
        // 允许所有HTTP方法
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允许所有请求头
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // 暴露的响应头
        config.setExposedHeaders(Arrays.asList("X-Requested-With", "Content-Type", "Accept", "Authorization"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}