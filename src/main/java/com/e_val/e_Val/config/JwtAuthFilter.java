package com.e_val.e_Val.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.e_val.e_Val.utils.JwtUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        logger.info("Processing request: {} {}, Authorization header: {}", 
            request.getMethod(), request.getServletPath(), authHeader != null ? authHeader.substring(0, Math.min(authHeader.length(), 20)) + "..." : null);

        // Skip filter for permitted endpoints
        if (isPermittedEndpoint(request)) {
            logger.debug("Permitted endpoint: {}", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for: {}", request.getServletPath());
            sendErrorResponse(response, "Missing or invalid Authorization header");
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userEmail;
        try {
            userEmail = jwtUtils.extractUsername(jwt);
            logger.info("Extracted userEmail: {}", userEmail);
        } catch (Exception e) {
            logger.error("JWT parsing error: {}", e.getMessage());
            sendErrorResponse(response, "Invalid or malformed JWT token");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            logger.info("Loaded userDetails: {}", userDetails.getUsername());
            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Authentication set for user: {}", userEmail);
            } else {
                logger.warn("Invalid JWT token for user: {}", userEmail);
                sendErrorResponse(response, "Invalid or expired JWT token");
                return;
            }
        } else if (userEmail == null) {
            logger.warn("No userEmail extracted from token");
            sendErrorResponse(response, "Malformed JWT token");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPermittedEndpoint(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();
        
        // Permit all static resources
        if (path.startsWith("/css/") || 
            path.startsWith("/js/") || 
            path.startsWith("/images/") ||
            path.endsWith(".html")) {
            return true;
        }
        
        // Permit auth endpoints
        if (path.startsWith("/auth/") && ("POST".equals(method) || "OPTIONS".equals(method))) {
            return true;
        }
        
        // Permit preflight requests
        if ("OPTIONS".equals(method)) {
            return true;
        }
        
        // Permit specific pages
        return "/login.html".equals(path) || 
               "/register.html".equals(path) || 
               "/".equals(path);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}