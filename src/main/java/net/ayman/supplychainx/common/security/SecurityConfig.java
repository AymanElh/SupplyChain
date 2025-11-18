package net.ayman.supplychainx.common.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.
                        requestMatchers("/public/**").permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .httpBasic(httpSecurityHttpBasicConfigurer -> {})
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
