package com.e_val.e_Val.model;

import com.e_val.e_Val.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(unique = true)
    private String name; // Full name of the user

    @Column(unique = true)
    @Email
    private String email;

    private String password;

    private String resetToken;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true, nullable = true)
    private String rollNo; // New field for student roll number

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getAuthority()));
    }

    public String getName() {
        return name; // Use name as the display name
    }
    
    @Override
    public String getUsername() {
        return email; // Use email as the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}