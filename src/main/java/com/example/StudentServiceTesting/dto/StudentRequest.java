package com.example.StudentServiceTesting.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudentRequest(

        @NotBlank(message = "Student name is required")
        @Size(min = 2, max = 100,
              message = "Student name must contain between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotNull(message = "Age is required")
        @Min(value = 16, message = "Age must be at least 16")
        @Max(value = 100, message = "Age cannot be greater than 100")
        Integer age,

        @NotBlank(message = "Course is required")
        String course

) {
}