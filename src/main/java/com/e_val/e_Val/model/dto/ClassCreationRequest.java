package com.e_val.e_Val.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data
public class ClassCreationRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String code;

    @NotBlank
    private String section;

    @NotNull
    @Min(1)
    private Integer maxCapacity;

    private String teacherEmail; // Optional
}