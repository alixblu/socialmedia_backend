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

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is incorrect");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password is incorrect");
        }

        // Check if the user is an admin
        boolean isAdmin = userRepository.existsByEmailAndIsAdmin(email, true);

        Map<String, Object> response = new HashMap<>();
        String token = jwtUtil.generateToken(user.getEmail());
        response.put("accessToken", token);
        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã được sử dụng!");
        }
    
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên người dùng đã tồn tại!");
        }

        // Secure password hashing
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(request.getPassword());

        // Create and save the user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword); // Store hashed password
        user.setDateOfBirth(request.getDateOfBirth());
        user.setCreatedAt(LocalDate.now());
        user.setIsAdmin(false);
        user.setAvatarUrl("default-avatar.png");
        user.setUpdatedAt(LocalDate.now());

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công!");
    }

    //API phần profile
    @PutMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer id,
            @RequestParam("username") String username,
            @RequestParam(value = "password" , required = false) String password,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile
    ) {
        // Cập nhật thông tin người dùng
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    
        // Set username
        user.setUsername(username);
    
        // Cập nhật password nếu có
        if (password != null && !password.isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(password));
        }
    
        // Nếu có file ảnh thì lưu lại
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // Tạo thư mục static/images trong resources (có thể cần điều chỉnh đường dẫn nếu chạy trên production)
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
    
        // Lưu cập nhật người dùng vào database
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
    

    @GetMapping("/getUserByToken")
    public ResponseEntity<?> getUserByToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                JwtUtil jwtUtil = new JwtUtil();
                
                // Validate token and extract email
                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.extractUsername(token);
                    
                    // Find user by email
                    User user = userRepository.findByEmail(email);
                    
                    if (user != null) {
                        return ResponseEntity.ok(user);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Không tìm thấy người dùng với email này");
                    }
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Token không hợp lệ");
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/non-admin")
    public ResponseEntity<?> getAllNonAdminUsers() {
        try {
            List<User> nonAdminUsers = userRepository.findByIsAdminFalse();
            return ResponseEntity.ok(nonAdminUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi lấy danh sách người dùng: " + e.getMessage());
        }
    }

}   