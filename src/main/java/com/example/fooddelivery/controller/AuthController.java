package com.example.fooddelivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.fooddelivery.model.User;
import com.example.fooddelivery.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // ✅ REGISTER USER (FIXED)
    @PostMapping("/register")
    public String signup(@RequestParam("name") String name,
                         @RequestParam("email") String email,
                         @RequestParam("password") String password) {

        // 🔒 Basic validation
        if (name.isEmpty() || email.isEmpty() || password.length() < 6) {
            return "redirect:/signup?error=true";
        }

        // ❌ Prevent duplicate email
        if (userRepo.findByEmail(email) != null) {
            return "redirect:/signup?exists=true";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);

        // 🔐 Hash password
        user.setPassword(passwordEncoder.encode(password));

        // ✅ DEFAULT ROLE
        user.setRole("USER");

        userRepo.save(user);

        return "redirect:/";
    }

    // ✅ LOGIN WITH ROLE-BASED REDIRECT
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session) {

        User user = userRepo.findByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {

            session.setAttribute("user", user);

            // 🔥 ROLE BASED REDIRECT
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin";
            } else {
                return "redirect:/menu";
            }
        }

        return "redirect:/?error=true";
    }

    // ✅ LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}