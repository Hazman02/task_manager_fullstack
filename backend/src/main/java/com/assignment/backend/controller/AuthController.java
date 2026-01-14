package com.assignment.backend.controller;

import com.assignment.backend.dto.LoginRequest;
import com.assignment.backend.dto.RegisterRequest;
import com.assignment.backend.dto.UserResponse;
import com.assignment.backend.model.User;
import com.assignment.backend.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.assignment.backend.dto.AuthResponse;
import com.assignment.backend.security.JwtUtil;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000"})
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public AuthController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
}


    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest req) {

        if (userRepository.existsByUsername(req.username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(req.email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(req.username);
        user.setEmail(req.email);
        user.setPassword(passwordEncoder.encode(req.password));

        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail());
    }
    
    @PostMapping("/login")
    
    public AuthResponse login(@RequestBody LoginRequest req) {
        User user = userRepository.findByUsername(req.username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!passwordEncoder.matches(req.password, user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            
            }
            
            String token = jwtUtil.generateToken(user.getUsername());
            
            return new AuthResponse(token);
        }

}
