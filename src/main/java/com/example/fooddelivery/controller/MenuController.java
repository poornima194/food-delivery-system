package com.example.fooddelivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.fooddelivery.repository.MenuRepository;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.model.Orders;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
public class MenuController {

    private final MenuRepository menuRepo;
    private final OrderRepository orderRepo;

    private List<String> cart = new ArrayList<>();
    private List<Double> prices = new ArrayList<>();

    public MenuController(MenuRepository menuRepo, OrderRepository orderRepo) {
        this.menuRepo = menuRepo;
        this.orderRepo = orderRepo;
    }

    @GetMapping("/menu")
    public String showMenu(Model model, HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        model.addAttribute("items", menuRepo.findAll());
        model.addAttribute("cartItems", cart);

        return "menu";
    }

    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("menuIds") int[] menuIds,
                           @RequestParam Map<String, String> params) {

        cart.clear();
        prices.clear();

        for (int id : menuIds) {

            String key = "quantities[" + id + "]";
            int qty = Integer.parseInt(params.get(key));

            var item = menuRepo.findById(id).get();

            cart.add(item.getItemName() + " x " + qty);
            prices.add(item.getPrice() * qty);
        }

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {

        double total = 0;
        for (double p : prices) total += p;

        model.addAttribute("cartItems", cart);
        model.addAttribute("total", total);

        return "cart";
    }

    @GetMapping("/remove")
    public String removeItem(@RequestParam("index") int index) {

        if (index >= 0 && index < cart.size()) {
            cart.remove(index);
            prices.remove(index);
        }

        return "redirect:/cart";
    }

    @GetMapping("/payment")
    public String paymentPage(Model model) {

        double total = 0;
        for (double p : prices) total += p;

        model.addAttribute("total", total);
        return "payment";
    }

    @PostMapping("/order")
    public String placeOrder() {

        double total = 0;
        StringBuilder itemsList = new StringBuilder();

        for (int i = 0; i < cart.size(); i++) {
            itemsList.append(cart.get(i)).append(", ");
            total += prices.get(i);
        }

        Orders order = new Orders();
        order.setItems(itemsList.toString());
        order.setTotal(total);
        order.setStatus("Placed");
        order.setPaymentStatus("PAID");
        order.setDeliveryStatus("Preparing");

        orderRepo.save(order);

        cart.clear();
        prices.clear();

        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderHistory(Model model) {
        model.addAttribute("orders", orderRepo.findAll());
        return "orders";
    }

    @GetMapping("/track")
    public String track(Model model) {
        model.addAttribute("orders", orderRepo.findAll());
        return "track";
    }
    
}