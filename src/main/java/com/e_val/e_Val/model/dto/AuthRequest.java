package com.e_val.e_Val.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
     @NotBlank
    private String name; 
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String password;
}