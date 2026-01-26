package com.testhub.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护
            .csrf().disable()
            // 禁用CORS（网关已处理）
            .cors()
            .and()
            // 会话管理：无状态
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 授权配置
            .authorizeRequests()
            // 放行所有/auth/**路径
            .antMatchers("/auth/**").permitAll()
            // 放行H2控制台
            .antMatchers("/h2-console/**").permitAll()
            // 放行actuator健康检查
            .antMatchers("/actuator/**").permitAll()
            // 其他请求需要认证
            .anyRequest().authenticated()
            .and()
            // 允许H2控制台frame
            .headers().frameOptions().disable();

        return http.build();
    }
}
