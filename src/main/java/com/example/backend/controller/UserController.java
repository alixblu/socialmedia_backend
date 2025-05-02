package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.backend.dto.RegisterDTO;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setAvatarUrl(userDetails.getAvatarUrl());
            user.setBio(userDetails.getBio());
            user.setDateOfBirth(userDetails.getDateOfBirth());
            user.setIsAdmin(userDetails.getIsAdmin());
            return userRepository.save(user);
        }
        return null;
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("email", user.getEmail());
            response.put("role", user.getIsAdmin() ? "ADMIN" : "USER");  // Assign role

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409: dữ liệu bị trùng
                    .body("Email đã được sử dụng!");
        }
    
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Tên người dùng đã tồn tại!");
        }
    
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setCreatedAt(LocalDate.now());
        user.setIsAdmin(false);
        user.setAvatarUrl("default-avatar.png");
    
        userRepository.save(user);
    
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201: tạo mới thành công
                .body("Đăng ký thành công!" + user);
    }
    


}   