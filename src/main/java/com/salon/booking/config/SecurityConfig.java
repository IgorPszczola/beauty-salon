package com.salon.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails owner = User.builder()
            .username("owner")
            .password(passwordEncoder().encode("owner123"))
            .roles("OWNER")
            .build();

        UserDetails staff = User.builder()
                .username("staff")
                .password(passwordEncoder().encode("staff123"))
                .roles("STAFF")
                .build();

        UserDetails customer = User.builder()
            .username("customer")
            .password(passwordEncoder().encode("customer123"))
            .roles("CUSTOMER")
            .build();

        return new InMemoryUserDetailsManager(admin, owner, staff, customer);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.GET, "/api/services", "/api/appointments").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/services", "/api/appointments").hasAnyRole("ADMIN", "OWNER", "STAFF")
                    .requestMatchers(HttpMethod.PATCH, "/api/services/**", "/api/appointments/**").hasAnyRole("ADMIN", "OWNER", "STAFF")
                    .requestMatchers(HttpMethod.DELETE, "/api/appointments/**").hasAnyRole("ADMIN", "OWNER", "STAFF")
                    .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
