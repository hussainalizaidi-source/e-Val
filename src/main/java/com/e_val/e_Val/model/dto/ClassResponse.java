package com.e_val.e_Val.model.dto;

import com.e_val.e_Val.model.User;
import lombok.Data;
import java.util.List;

@Data
public class ClassResponse {
    private Long id;
    private String name;
    private String code;
    private String section;
    private Integer maxCapacity;
    private User teacher;
    private List<User> students;
}