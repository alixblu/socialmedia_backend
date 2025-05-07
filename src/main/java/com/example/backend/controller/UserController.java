package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.backend.dto.RegisterDTO;
import java.io.File;
import java.io.IOException;

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
        JwtUtil jwtUtil = new JwtUtil();
        User user = userRepository.findByEmail(email);

        // if (user == null) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is incorrect");
        // }

        // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // if (!encoder.matches(password, user.getPassword())) {
        //     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password is incorrect");
        // }

        // // Check if the user is an admin
        // boolean isAdmin = userRepository.existsByEmailAndIsAdmin(email, true);

        // Map<String, Object> response = new HashMap<>();
        // response.put("email", user.getEmail());
        // response.put("role", isAdmin ? "ADMIN" : "USER");
        // String token = jwtUtil.generateToken(user.getEmail());
        // response.put("token", token);
        return ResponseEntity.ok(user);
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
    
    //API phần profile
    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer id,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        // Cập nhật thông tin người dùng
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        // Nếu có file ảnh thì lưu lại
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // Tạo thư mục static/images trong resources
                String uploadDir = "src/main/resources/static/images/";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Xóa file ảnh cũ nếu không phải ảnh mặc định
                String oldAvatarUrl = user.getAvatarUrl();
                if (oldAvatarUrl != null && !oldAvatarUrl.equals("default-avatar.png")) {
                    File oldFile = new File(directory.getAbsolutePath() + File.separator + oldAvatarUrl);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }

                // Tạo tên file duy nhất bằng cách thêm timestamp
                String originalFilename = avatarFile.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String fileName = System.currentTimeMillis() + fileExtension;

                // Lưu file
                File destFile = new File(directory.getAbsolutePath() + File.separator + fileName);
                avatarFile.transferTo(destFile);

                // Cập nhật URL avatar trong database
                user.setAvatarUrl(fileName);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lưu file ảnh: " + e.getMessage());
            }
        }

        userRepository.save(user); // cập nhật thông tin
        return ResponseEntity.ok(user);
    }

}   