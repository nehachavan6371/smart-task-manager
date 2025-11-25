package com.smarttaskmanager.controller;

import com.smarttaskmanager.model.User;
import com.smarttaskmanager.repository.UserRepository;
import com.smarttaskmanager.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid_credentials"));
        }
        var user = userRepository.findByUsername(username).orElseThrow();
        java.util.Set<String> roles = user.getRoles() == null ? java.util.Collections.emptySet() : user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
        String token = jwtUtil.generateToken(username, roles);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // Simple user creation for demo (DO NOT use in production without validation)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (userRepository.findByUsername(username).isPresent()) return ResponseEntity.badRequest().body(Map.of("error", "user exists"));
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("username", username));
    }
}
