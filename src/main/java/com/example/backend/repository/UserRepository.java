package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find a user by their username
    User findByUsername(String username);

    // Find a user by their email
    User findByEmail(String email);
}