package com.salon.booking.config;

import com.salon.booking.model.AppUser;
import com.salon.booking.model.UserRole;
import com.salon.booking.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AppUserRepository appUserRepository) {
        return username -> {
            AppUser appUser = appUserRepository.findByUsername(username)
                    .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

            UserDetails user = User.builder()
                    .username(appUser.getUsername())
                    .password(appUser.getPassword())
                    .roles(appUser.getRole().name())
                    .disabled(Boolean.FALSE.equals(appUser.getEnabled()))
                    .build();

            return user;
        };
    }

    @Bean
    public CommandLineRunner seedUsers(AppUserRepository appUserRepository,
                                       PasswordEncoder passwordEncoder,
                                       JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("ALTER TABLE app_users ADD COLUMN IF NOT EXISTS enabled BOOLEAN DEFAULT TRUE");
            jdbcTemplate.execute("UPDATE app_users SET enabled = TRUE WHERE enabled IS NULL");

            createUserIfMissing(appUserRepository, passwordEncoder, "admin", "admin123", UserRole.ADMIN);
            createUserIfMissing(appUserRepository, passwordEncoder, "owner", "owner123", UserRole.OWNER);
            createUserIfMissing(appUserRepository, passwordEncoder, "staff", "staff123", UserRole.STAFF);
            createUserIfMissing(appUserRepository, passwordEncoder, "customer", "customer123", UserRole.CUSTOMER);
        };
    }

    private void createUserIfMissing(AppUserRepository appUserRepository,
                                     PasswordEncoder passwordEncoder,
                                     String username,
                                     String rawPassword,
                                     UserRole role) {
        AppUser existingUser = appUserRepository.findByUsername(username).orElse(null);
        if (existingUser != null) {
            if (!Boolean.TRUE.equals(existingUser.getEnabled())) {
                existingUser.setEnabled(true);
                appUserRepository.save(existingUser);
            }
            return;
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);
        appUserRepository.save(user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.GET, "/api/services", "/api/appointments").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/users").hasAnyRole("ADMIN", "OWNER")
                    .requestMatchers(HttpMethod.POST, "/api/users").hasAnyRole("ADMIN", "OWNER")
                    .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasAnyRole("ADMIN", "OWNER")
                    .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("ADMIN", "OWNER")
                    .requestMatchers(HttpMethod.POST, "/api/services").hasAnyRole("ADMIN", "OWNER", "STAFF")
                    .requestMatchers(HttpMethod.POST, "/api/appointments").hasAnyRole("ADMIN", "OWNER", "STAFF", "CUSTOMER")
                    .requestMatchers(HttpMethod.PATCH, "/api/services/**", "/api/appointments/**").hasAnyRole("ADMIN", "OWNER", "STAFF")
                    .requestMatchers(HttpMethod.DELETE, "/api/services/**").hasAnyRole("ADMIN", "OWNER")
                    .requestMatchers(HttpMethod.DELETE, "/api/appointments/**").hasAnyRole("ADMIN", "OWNER", "STAFF")
                    .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
