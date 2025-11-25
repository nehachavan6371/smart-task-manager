package com.smarttaskmanager.config;

import com.smarttaskmanager.model.Role;
import com.smarttaskmanager.model.User;
import com.smarttaskmanager.repository.RoleRepository;
import com.smarttaskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Allow overriding admin seeding via environment variables for easy dev setup
            String adminUsername = System.getenv().getOrDefault("ADMIN_USERNAME", "admin");
            String adminPassword = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin");
            String adminRolesEnv = System.getenv().getOrDefault("ADMIN_ROLES", "ROLE_USER,ROLE_ADMIN");

            // Ensure roles exist
            Set<Role> roles = new java.util.HashSet<>();
            for (String rn : adminRolesEnv.split(",")) {
                String roleName = rn.trim();
                if (roleName.isEmpty()) continue;
                Role r = roleRepository.findByName(roleName).orElseGet(() -> {
                    Role newR = new Role(); newR.setName(roleName); return roleRepository.save(newR);
                });
                roles.add(r);
            }

            // Seed admin user if not present
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setDisplayName("Administrator");
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRoles(roles);
                userRepository.save(admin);
            }
        };
    }
}
