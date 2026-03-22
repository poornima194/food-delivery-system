package com.example.fooddelivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.fooddelivery.model.User;
import com.example.fooddelivery.repository.UserRepository;

@Controller
public class AuthController {

    private final UserRepository userRepo;

    public AuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ✅ Show login page
    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    // ✅ Show signup page
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // ✅ REGISTER (FIXED)
    @PostMapping("/register")
    public String register(@RequestParam("name") String name,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        userRepo.save(user);

        return "redirect:/";
    }

    // ✅ LOGIN (FIXED)
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password) {

        User user = userRepo.findByEmailAndPassword(email, password);

        if (user != null) {
            return "redirect:/menu";
        } else {
            return "login";
        }
    }
}