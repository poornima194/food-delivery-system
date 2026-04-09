package com.example.fooddelivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.fooddelivery.model.Menu;
import com.example.fooddelivery.model.User;
import com.example.fooddelivery.repository.MenuRepository;

import jakarta.servlet.http.HttpSession;

    @Controller
    @RequestMapping("/admin")
    public class AdminController {

    private final MenuRepository menuRepo;

    public AdminController(MenuRepository menuRepo) {
        this.menuRepo = menuRepo;
    }

    
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getRole());
    }

    
    @GetMapping
    public String adminPage(Model model, HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/menu";
        }

        model.addAttribute("menuItems", menuRepo.findAll());
        model.addAttribute("item", new Menu()); // for form binding

        return "admin";
    }

    
    @PostMapping("/add")
    public String addItem(@ModelAttribute Menu item, HttpSession session) {

        if (!isAdmin(session)) return "redirect:/menu";

        
        if (item.getItemName() == null || item.getItemName().isEmpty() || item.getPrice() <= 0) {
            return "redirect:/admin?error=true";
        }

        menuRepo.save(item);

        return "redirect:/admin";
    }

    
    @GetMapping("/delete")
    public String deleteItem(@RequestParam("id") int id, HttpSession session) {

        if (!isAdmin(session)) return "redirect:/menu";

        if (menuRepo.existsById(id)) {
            menuRepo.deleteById(id);
        }

        return "redirect:/admin";
    }

    
    @GetMapping("/edit")
    public String editPage(@RequestParam("id") int id, Model model, HttpSession session) {

        if (!isAdmin(session)) return "redirect:/menu";

        Menu item = menuRepo.findById(id).orElse(new Menu()); // ✅ safe fallback
        model.addAttribute("item", item);

        return "edit-item";
    }

    
    @PostMapping("/update")
    public String updateItem(@ModelAttribute Menu item, HttpSession session) {

        if (!isAdmin(session)) return "redirect:/menu";

        Menu existing = menuRepo.findById(item.getMenuId()).orElse(null);

        if (existing != null) {
            existing.setItemName(item.getItemName());
            existing.setPrice(item.getPrice());
            existing.setImageUrl(item.getImageUrl());

            menuRepo.save(existing);
        }

        return "redirect:/admin?updated=true";
    }
}
