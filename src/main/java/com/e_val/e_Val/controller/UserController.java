package com.e_val.e_Val.controller;

import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.enums.Role;
import com.e_val.e_Val.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role) {
        Role roleEnum = Role.valueOf(role.toUpperCase());
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getRole() == roleEnum)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/students/rollno/{rollNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<User> getStudentByRollNumber(@PathVariable String rollNumber) {
        User student = userRepository.findByRollNo(rollNumber)
                .filter(user -> user.getRole() == Role.STUDENT)
                .orElse(null);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(student);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("message", ex.getMessage()));
    }
}