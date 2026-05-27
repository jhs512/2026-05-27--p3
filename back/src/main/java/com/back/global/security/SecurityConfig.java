package com.back.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/", "/member/join", "/member/login").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        // 글쓰기/수정 폼은 로그인 필요 (아래 GET 허용보다 먼저 매칭)
                        .requestMatchers("/posts/write", "/posts/*/edit").authenticated()
                        // 목록·상세는 공개
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/*").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/")
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
