package com.example.backend.dto;

import java.time.LocalDate;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String username;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
}
