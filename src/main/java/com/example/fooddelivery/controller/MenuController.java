package com.example.fooddelivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.fooddelivery.repository.MenuRepository;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.model.Orders;

@Controller
public class MenuController {

    private final MenuRepository menuRepo;
    private final OrderRepository orderRepo;

    public MenuController(MenuRepository menuRepo, OrderRepository orderRepo) {
        this.menuRepo = menuRepo;
        this.orderRepo = orderRepo;
    }

    @GetMapping("/menu")
    public String showMenu(Model model) {
        model.addAttribute("items", menuRepo.findAll());
        return "menu";
    }

    @PostMapping("/order")
    @ResponseBody
    public String placeOrder() {
        Orders order = new Orders();
        order.setStatus("Placed");
        order.setPaymentStatus("PAID");
        orderRepo.save(order);
        return "Order Placed!";
    }
}