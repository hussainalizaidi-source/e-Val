package com.e_val.e_Val.model;

import com.e_val.e_Val.model.enums.Role;
import lombok.Data;
import javax.persistence.*;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
    private String resetToken;

    @Enumerated(EnumType.STRING)
    private Role role;
}
