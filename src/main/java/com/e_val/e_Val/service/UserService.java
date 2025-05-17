package com.e_val.e_Val.service;

import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.enums.Role;
import com.e_val.e_Val.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
    public Optional<User> findByRollNo(String rollNo) {
        return userRepository.findByRollNo(rollNo);
    }
    public String generateNextRollNo() {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        int nextNumber = 1;

        if (!students.isEmpty()) {
            // Find the highest roll_no (e.g., STD-0005)
            String maxRollNo = students.stream()
                .filter(user -> user.getRollNo() != null)
                .map(User::getRollNo)
                .max(String::compareTo)
                .orElse("STD-0000");

            // Extract the number part and increment
            String numberPart = maxRollNo.substring(4); // e.g., "0005"
            nextNumber = Integer.parseInt(numberPart) + 1;
        }

        // Format as STD-XXXX (e.g., STD-0001)
        return String.format("STD-%04d", nextNumber);
    }
}