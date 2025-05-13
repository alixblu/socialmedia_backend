package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find a user by their username
    User findByUsername(String username);
    // User findById(String id);
    // Find a user by their email
    User findByEmail(String email);
    boolean existsByEmailAndIsAdmin(String email, boolean isAdmin);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    // Find all non-admin users
    List<User> findByIsAdminFalse();
}