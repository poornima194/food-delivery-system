package com.example.fooddelivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.model.Orders;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OrderRepository orderRepo;

    public AdminController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("orders", orderRepo.findAll());
        model.addAttribute("totalOrders", orderRepo.count());
        return "admin";
    }

    @GetMapping("/updateStatus")
    public String updateStatus(@RequestParam("id") int id) {
        Orders order = orderRepo.findById(id).get();
        order.setDeliveryStatus("Out for Delivery");
        orderRepo.save(order);
        return "redirect:/admin";
    }
}