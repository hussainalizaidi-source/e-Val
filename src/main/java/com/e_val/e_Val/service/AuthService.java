package com.e_val.e_Val.service;

import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.AuthRequest;
import com.e_val.e_Val.model.dto.AuthResponse;
import com.e_val.e_Val.model.dto.RegisterRequest;
import com.e_val.e_Val.model.enums.Role;
import com.e_val.e_Val.repository.UserRepository;
import com.e_val.e_Val.utils.JwtUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService; // Added dependency

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User.UserBuilder userBuilder = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole());

        // Assign roll_no for students
        if (request.getRole() == Role.STUDENT) {
            String rollNo = userService.generateNextRollNo();
            userBuilder.rollNo(rollNo);
        }

        User user = userBuilder.build();
        userRepository.save(user);

        return AuthResponse.builder()
            .token(jwtUtils.generateToken(user))
            .build();
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        // Debug output
        System.out.println("Loaded user role: " + user.getRole());
        System.out.println("Authorities: " + user.getAuthorities());
        return AuthResponse.builder()
            .token(jwtUtils.generateToken(user))
            .role(user.getRole())
            .build();
    }
}