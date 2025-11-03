package com.example.techzone.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setDefaultTargetUrl("/product/home");
        successHandler.setAlwaysUseDefaultTargetUrl(false);

        http
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        .requestMatchers("/auth/account").authenticated()
                        .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                        .requestMatchers("/api/user/get-all-users", "/api/user/delete/**", "/api/user/get-by-id/").hasRole("ADMIN")
                        .requestMatchers("/api/user/update", "/api/user/get").authenticated()
                        .requestMatchers(
                                "/api/address/create",
                                "/api/address/update",
                                "/api/address/get-all-for-current-user",
                                "/api/address/delete/**",
                                "/api/address/get-by-id/"
                        ).authenticated()
                        .requestMatchers("/api/address/get-all").hasRole("ADMIN")
                        .requestMatchers("/api/category/get-all", "/api/category/get-by-id/").permitAll()
                        .requestMatchers("/api/category/create", "/api/category/delete/**", "/api/category/update").hasRole("ADMIN")
                        .requestMatchers("/api/product/create", "/api/product/delete/**", "/api/product/update/").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/api/product/get-all", "/api/product/get-by-id/", "/api/product/get-by-category-id/").permitAll()
                        .requestMatchers("/product/create").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/product/*/edit").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/api/products/*/feedback").authenticated()
                        .requestMatchers("/api/feedback/**").authenticated()
                        .anyRequest().permitAll()
                )

//               JWT-filter for API
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .csrf(Customizer.withDefaults());

        return http.build();
    }
}

