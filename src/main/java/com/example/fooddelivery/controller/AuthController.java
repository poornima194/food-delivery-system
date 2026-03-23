package com.example.fooddelivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.fooddelivery.model.User;
import com.example.fooddelivery.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UserRepository userRepo;

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

    // ✅ SESSION LOGIN
    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        jakarta.servlet.http.HttpSession session) {

        User user = userRepo.findByEmailAndPassword(email, password);

        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/menu";
        }

        return "redirect:/?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}