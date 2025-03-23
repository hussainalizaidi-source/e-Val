package com.e_val.e_Val.service;

import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.*;
import com.e_val.e_Val.model.enums.Role;
import com.e_val.e_Val.repository.UserRepository;
import com.e_val.e_Val.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.STUDENT) // Default role (adjust as needed)
            .build();

        userRepository.save(user);
        return AuthResponse.builder()
            .token(jwtUtils.generateToken(user))
            .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        return AuthResponse.builder()
            .token(jwtUtils.generateToken(user))
            .build();
    }
}
