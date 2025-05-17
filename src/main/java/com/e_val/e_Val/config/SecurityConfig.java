package com.e_val.e_Val.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/error", // Permit /error endpoint
                    "/login.html",
                    "/register.html",
                    "/teacherdash.html",
                    "/studentdash.html",
                    "/notifications.html",
                    "/createQuiz.html",
                    "/numerical.html",
                    "/short.html",
                    "/institution.html",
                    "/assignquiz.html",
                    "/upload.html",
                    "/assignstudent.html",
                    "/assignteacher.html",
                    "/mcq.html",
                    "/my_classes.html",
                    "/attemptQuiz.html",
                    "/markAttempts.html",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/classes").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.GET, "/api/classes/available").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers(HttpMethod.GET, "/api/classes/my-classes").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/quizzes/teacher").hasRole("TEACHER")
                .requestMatchers(HttpMethod.GET, "/api/quizzes/available").hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/quizzes/{quizId}").hasRole("STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/quizzes/{quizId}/submit").hasRole("STUDENT")
                .requestMatchers(HttpMethod.POST, "/api/classes/{classId}/assign-student").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/classes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/quizzes/**").hasRole("TEACHER")
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    System.out.println("Unauthorized access to " + request.getServletPath() + ": " + authException.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized access\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    System.out.println("Access denied to " + request.getServletPath() + ": " + accessDeniedException.getMessage());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Access denied\"}");
                })
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}