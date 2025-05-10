package com.example.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String bio;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(nullable = false, unique = true)
    private String username;
}

enum UserStatus {
    ACTIVE,
    BANNED
}