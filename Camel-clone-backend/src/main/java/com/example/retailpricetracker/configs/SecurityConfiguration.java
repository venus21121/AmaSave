package com.example.retailpricetracker.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Defining what criteria an incoming request must match before being forwarded to application middleware. We want the following criteria:
//  There is no need to provide the CSRF token because we will use it.
//  The request URL path matching /auth/signup and /auth/login doesn't require authentication.
//  Any other request URL path must be authenticated.
//  The request is stateless, meaning every request must be treated as a new one, even if it comes from the same client or has been received earlier.
//  Must use the custom authentication provider, and they must be executed before the authentication middleware.
//  The CORS configuration must allow only POST and GET requests.
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF protection
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**", "/product/**", "/users/**", "/api/pricewatch/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000"));//"http://localhost:3000" ,
                configuration.setAllowedMethods(List.of("GET","POST", "OPTIONS", "DELETE", "PATCH", "PUT"));  // Include OPTIONS for preflight, "OPTIONS"
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));

        configuration.setAllowCredentials(true); // Allow credentials (cookies)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}
