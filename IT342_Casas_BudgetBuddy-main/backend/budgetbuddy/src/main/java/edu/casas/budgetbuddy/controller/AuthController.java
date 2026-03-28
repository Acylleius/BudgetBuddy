package edu.casas.budgetbuddy.controller;

import edu.casas.budgetbuddy.dto.LoginRequest;
import edu.casas.budgetbuddy.dto.RegisterRequest;
import edu.casas.budgetbuddy.entity.User;
import edu.casas.budgetbuddy.repository.UserRepository;
import edu.casas.budgetbuddy.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        if (userService.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists";
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        userService.register(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userService.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return "User not found";
        }

        if (!user.getPassword().equals(request.getPassword())) {
            return "Invalid password";
        }

        return "Login successful";
    }
}