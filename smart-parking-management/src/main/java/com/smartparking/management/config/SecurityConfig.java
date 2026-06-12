package com.smartparking.management.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(14);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://127.0.0.1:5500",
                        "http://localhost:5500"
                         )
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/verify",
                                "/api/auth/login",
                                "/api/auth/forgot",
                                "/api/auth/reset"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // users
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/profile")
                        .hasRole("USER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users",
                                "/api/users/*",
                                "/api/users/all",
                                "/api/users/search/email",
                                "/api/users/search/phone"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/*")
                        .hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/*/restore")
                        .hasRole("ADMIN")
                        // Vehicles
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/vehicles"
                        ).hasRole("USER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/vehicles/my-vehicles"
                        ).hasRole("USER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/vehicles/*"
                        ).hasRole("USER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/vehicles/*/restore"
                        ).hasRole("USER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/vehicles",
                                "/api/vehicles/search",
                                "/api/vehicles/type",
                                "/api/vehicles/*"
                        ).hasRole("ADMIN")

                        // Parking area
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/parking-areas"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/parking-areas/my"
                        ).hasRole("PARKING_OWNER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/parking-areas/all"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER", "USER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/parking-areas"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER", "USER")
                        .requestMatchers(
                                "/api/parking-areas/search/**"
                        ).hasAnyRole("USER", "ADMIN", "SECURITY","PARKING_OWNER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/parking-areas/**"
                        ).hasAnyRole("USER", "ADMIN", "SECURITY", "PARKING_OWNER")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/parking-areas/**"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/parking-areas/**"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER")
                        // Parking Rates
                        .requestMatchers("/api/parking-rates/**")
                        .hasAnyRole("ADMIN", "PARKING_OWNER")
                        // Parking Slots
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/parking-slots/generate"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/parking-slots"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER")

// ADD THIS NEW BLOCK ↓
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/parking-slots/available",
                                "/api/parking-slots/available/**",
                                "/api/parking-slots/area/**",
                                "/api/parking-slots/search/**"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER", "USER", "SECURITY")
// ADD THIS NEW BLOCK ↑

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/parking-slots/**"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER", "USER", "SECURITY")
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/parking-slots/**"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/parking-slots/**"
                        ).hasAnyRole("ADMIN", "PARKING_OWNER")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/parking-slots/*/status")
                        .hasAnyRole("ADMIN", "PARKING_OWNER")
                        // Bookings
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/bookings"
                        ).hasRole("USER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/my",
                                "/api/bookings/my/**"
                        ).hasRole("USER")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/bookings/*/cancel"
                        ).hasRole("USER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/search"
                        ).hasAnyRole("ADMIN", "SECURITY")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/bookings/*/start"
                        ).hasRole("SECURITY")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/bookings/*/complete"
                        ).hasRole("SECURITY")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings",
                                "/api/bookings/status",
                                "/api/bookings/type",
                                "/api/bookings/date-range"
                        ).hasAnyRole("ADMIN", "SECURITY", "PARKING_OWNER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/*"
                        ).hasAnyRole("ADMIN", "SECURITY", "PARKING_OWNER")
                        // payments
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/payments/extra/**"
                        ).hasRole("USER")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/payments/failed/**"
                        ).hasRole("USER")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/payments"
                        ).hasRole("USER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/payments/my",
                                "/api/payments/my/date-range"
                        ).hasRole("USER")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/payments",
                                "/api/payments/status",
                                "/api/payments/date-range",
                                "/api/payments/search",
                                "/api/payments/booking/**",
                                "/api/payments/*"
                        ).hasRole("ADMIN")
                        // Dashboard
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/dashboard/admin"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/dashboard/user"
                        ).hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/dashboard/owner")
                        .hasRole("PARKING_OWNER")
                        // QR Code
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/qr/booking/**"
                        ).hasRole("USER")
                        // THIS JOB ONLY FOR SECURITY DO BUT NOW I ACCESS IT USER ALSO
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/qr/verify"
                        ).hasRole("SECURITY")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/qr/check-in/**",
                                "/api/qr/check-out/**"
                        ).hasAnyRole("SECURITY","USER")
                        // Notifications
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/notifications/my",
                                "/api/notifications/my/unread-count"
                        ).hasRole("USER")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/notifications/*/read"
                        ).hasRole("USER")
                        // Reports
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reports/**"
                        ).hasRole("ADMIN")
                        // Analytics
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/analytics"
                        ).hasRole("ADMIN")


                        .anyRequest().authenticated())
                  .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
               .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
