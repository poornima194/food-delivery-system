package com.example.fooddelivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.fooddelivery.repository.MenuRepository;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.model.Orders;
import com.example.fooddelivery.model.OrderItem;

import jakarta.servlet.http.HttpSession;
import java.util.*;



@Controller
public class MenuController {

    private final MenuRepository menuRepo;
    private final OrderRepository orderRepo;

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
        return "menu";
    }

    // ✅ SESSION BASED CART
    @PostMapping("/addToCart")
    public String addToCart(@RequestParam("menuIds") int[] menuIds,
                           @RequestParam Map<String, String> params,
                           HttpSession session) {

        List<String> cart = (List<String>) session.getAttribute("cart");
        List<Double> prices = (List<Double>) session.getAttribute("prices");

        if (cart == null) {
            cart = new ArrayList<>();
            prices = new ArrayList<>();
        }

        for (int id : menuIds) {

            String key = "quantities[" + id + "]";
            int qty = Integer.parseInt(params.get(key));

            var item = menuRepo.findById(id).orElse(null);
            if (item == null) continue;

            cart.add(item.getItemName() + " x " + qty);
            prices.add(item.getPrice() * qty);
        }

        session.setAttribute("cart", cart);
        session.setAttribute("prices", prices);

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {

        List<String> cart = (List<String>) session.getAttribute("cart");
        List<Double> prices = (List<Double>) session.getAttribute("prices");

        if (cart == null) {
            cart = new ArrayList<>();
            prices = new ArrayList<>();
        }

        double total = prices.stream().mapToDouble(Double::doubleValue).sum();

        model.addAttribute("cartItems", cart);
        model.addAttribute("total", total);

        return "cart";
    }

    @GetMapping("/remove")
    public String removeItem(@RequestParam int index, HttpSession session) {

        List<String> cart = (List<String>) session.getAttribute("cart");
        List<Double> prices = (List<Double>) session.getAttribute("prices");

        if (cart != null && index >= 0 && index < cart.size()) {
            cart.remove(index);
            prices.remove(index);
        }

        session.setAttribute("cart", cart);
        session.setAttribute("prices", prices);

        return "redirect:/cart";
    }

    @GetMapping("/payment")
    public String paymentPage(Model model, HttpSession session) {

        List<Double> prices = (List<Double>) session.getAttribute("prices");

        double total = 0;
        if (prices != null) {
            total = prices.stream().mapToDouble(Double::doubleValue).sum();
        }

        model.addAttribute("total", total);
        return "payment";
    }

    @PostMapping("/order")
    public String placeOrder(HttpSession session) {

        List<String> cart = (List<String>) session.getAttribute("cart");
        List<Double> prices = (List<Double>) session.getAttribute("prices");

        if (cart == null) return "redirect:/menu";

        Orders order = new Orders();
        order.setStatus("Placed");
        order.setPaymentStatus("PAID");
        order.setDeliveryStatus("Preparing");

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (int i = 0; i < cart.size(); i++) {

            String itemStr = cart.get(i); // "Pizza x 2"
            String[] parts = itemStr.split(" x ");

            String name = parts[0];
            int qty = Integer.parseInt(parts[1]);

            double price = prices.get(i);

            OrderItem item = new OrderItem();
            item.setItemName(name);
            item.setQuantity(qty);
            item.setPrice(price);
            item.setOrder(order);

            orderItems.add(item);
            total += price;
        }

        order.setItems(orderItems);
        order.setTotal(total);

        orderRepo.save(order);

        session.removeAttribute("cart");
        session.removeAttribute("prices");

        return "redirect:/orders";
    }

    @GetMapping("/track")
    public String track(Model model) {
        model.addAttribute("orders", orderRepo.findAll());
        return "track";
    }
}