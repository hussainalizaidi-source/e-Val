package com.e_val.e_Val.model.dto;

import com.e_val.e_Val.model.enums.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Role role;
}
