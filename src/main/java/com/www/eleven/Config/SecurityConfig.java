package com.www.eleven.Config;

import com.www.eleven.Filter.AuthorizationFilter;
import com.www.eleven.Filter.CorsConfig;
import com.www.eleven.Filter.Service.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration//빈등록: 스프링 컨테이너에서 객체에서 관리
@EnableWebSecurity/////필터를 추가해준다
@Slf4j
public class SecurityConfig {
    private CorsConfig corsConfig;
    private AuthenticationConfiguration authenticationConfiguration;
    private AuthorizationService authorizationService;

    public SecurityConfig(CorsConfig corsConfig, AuthenticationConfiguration configuration, AuthorizationService authorizationService) {
        this.authenticationConfiguration = configuration;
        this.authorizationService = authorizationService;
        this.corsConfig = corsConfig;
    }

    @Bean
    public BCryptPasswordEncoder pwdEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .formLogin().disable()
//                .addFilterBefore(authorizationExceptionFilter, AuthorizationFilter.class)
                .addFilter(corsConfig.corsfilter())
                .addFilter(new AuthorizationFilter(authenticationConfiguration.getAuthenticationManager(), authorizationService))
                .authorizeRequests()
                .antMatchers("/login", "/token-expire/**","/api/auth/**").permitAll()
                .anyRequest().permitAll()
        ;

        return http.build();

    }
}
