package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.model.UserStatus;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import com.example.backend.dto.StatusDTO;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")

public class UserController {
    
    private Map<String, String> otpStorage = new HashMap<>(); // email -> otp

    @Autowired  
    private UserRepository userRepository;


    @Autowired
    private JavaMailSender mailSender;


    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable Integer id) {
        return userRepository.findById(id).orElse(null);
    }   


    @GetMapping("/username/id/{id}")
    public ResponseEntity<String> getUserNameById(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            return ResponseEntity.ok(user.getUsername());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


    //Hàm tạo OTP VÀ XÁC THỰC OTP

    private String generateOtp() {
        int otp = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    @PostMapping("/sendOtp")
    public ResponseEntity<?> sendOtp(@RequestParam("email") String email) {
        
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại.");
        }

        String otp = generateOtp();
        otpStorage.put(email, otp);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Mã xác thực OTP");
            message.setText("Mã OTP của bạn là: " + otp + ". Vui lòng không chia sẻ mã này với bất kỳ ai.");
            mailSender.send(message);

            return ResponseEntity.ok("OTP đã được gửi tới email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi gửi email: " + e.getMessage());
        }
    }


   @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(
        @RequestParam("email") String email,
        @RequestParam("otp") String inputOtp
    ) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp == null || !storedOtp.equals(inputOtp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn.");
        }

        otpStorage.remove(email);
        return ResponseEntity.ok("Xác thực OTP thành công!");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(
            @RequestParam("email") String email,
            @RequestParam("newPassword") String newPassword) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email không tồn tại.");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Đặt lại mật khẩu thành công!");
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

        // Kiểm tra trạng thái tài khoản
        System.out.println("User status: " + user.getStatus());
        if (user.getStatus() != UserStatus.ACTIVE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản của bạn đã bị khóa hoặc không hoạt động.");
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
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        user.setUsername(username);

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
    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer userId, @RequestBody StatusDTO statusDTO) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

            user.setStatus(UserStatus.valueOf(statusDTO.getStatus()));
            userRepository.save(user);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi cập nhật trạng thái người dùng: " + e.getMessage());
        }
    }

}   