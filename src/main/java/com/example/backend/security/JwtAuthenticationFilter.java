package com.example.backend.security;

import com.example.backend.model.User;
import com.example.backend.model.UserStatus;
import com.example.backend.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            
            if (token != null && jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractUsername(token);
                System.out.println("Extracted email from token: " + email); // Debug log
                
                User user = userRepository.findByEmail(email);
                if (user != null) {
                    // Check if user is active
                    if (user.getStatus() != UserStatus.ACTIVE) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("Tài khoản của bạn đã bị khóa hoặc không hoạt động.");
                        return;
                    }
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Authentication set for user: " + email); // Debug log
                }
            }
        } catch (Exception e) {
            System.out.println("Error processing JWT token: " + e.getMessage()); // Debug log
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}