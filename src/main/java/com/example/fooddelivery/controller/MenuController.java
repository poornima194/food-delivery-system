package com.example.fooddelivery.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.fooddelivery.repository.MenuRepository;
import com.example.fooddelivery.repository.OrderRepository;
import com.example.fooddelivery.model.Orders;
import com.example.fooddelivery.model.OrderItem;
import com.example.fooddelivery.model.User;

import jakarta.servlet.http.HttpSession;
import java.util.*;

import org.springframework.scheduling.annotation.Scheduled;
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

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("items", menuRepo.findAll());
        return "menu";
    }

    
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
    public String removeItem(@RequestParam("index") int index, HttpSession session) {

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

        if (cart == null || prices == null) {
            return "redirect:/menu";
        }

        Orders order = new Orders();
        order.setStatus("Placed");
        order.setPaymentStatus("PAID");
        order.setDeliveryStatus("Preparing");

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (int i = 0; i < cart.size(); i++) {

            String itemStr = cart.get(i);
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

    
    @GetMapping("/orders")
    public String orderHistory(Model model) {
        model.addAttribute("orders", orderRepo.findAll());
        return "orders";
    }

    
    @GetMapping("/track")
    public String track(Model model) {

        List<Orders> orders = orderRepo.findAll();

        for (Orders order : orders) {

            String status = order.getDeliveryStatus();

            if ("Preparing".equals(status)) {
                order.setProgress(25);

            } else if ("Out for Delivery".equals(status)) {
                order.setProgress(75);

            } else if ("Delivered".equals(status)) {
                order.setProgress(100);

            } else {
                order.setProgress(0);
            }
        }

        model.addAttribute("orders", orders);
        return "track";
    }
    
    @Scheduled(fixedRate = 20000) // every 20 seconds
    public void autoUpdateStatus() {

        List<Orders> orders = orderRepo.findAll();

        for (Orders order : orders) {

            if ("Delivered".equals(order.getDeliveryStatus())) continue;

            if ("Preparing".equals(order.getDeliveryStatus())) {
                order.setDeliveryStatus("Out for Delivery");

            } else if ("Out for Delivery".equals(order.getDeliveryStatus())) {
                order.setDeliveryStatus("Delivered");
            }
        }

        orderRepo.saveAll(orders);
    }
    @PostMapping("/createOrder")
    @ResponseBody
    public Map<String, Object> createOrder(HttpSession session) {

        try {
            List<Double> prices = (List<Double>) session.getAttribute("prices");

            double total = 0;
            if (prices != null) {
                total = prices.stream().mapToDouble(Double::doubleValue).sum();
            }

            int amount = (int) (total * 100); // in paise

            
            com.razorpay.RazorpayClient client =
                    new com.razorpay.RazorpayClient("rzp_test_SVXcXvwVu4o7PM", "AzXeWwaAgMC1kg7NoXAGQW73");

            org.json.JSONObject options = new org.json.JSONObject();
            options.put("amount", amount);
            options.put("currency", "INR");
            options.put("receipt", "order_rcptid_" + System.currentTimeMillis());

            com.razorpay.Order order = client.orders.create(options);

            
            Map<String, Object> response = new HashMap<>();
            response.put("amount", amount);
            response.put("currency", "INR");
            response.put("id", order.get("id"));   

            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    @PostMapping("/verifyPayment")
    @ResponseBody
    public String verifyPayment(
        @RequestParam("razorpay_payment_id") String razorpay_payment_id,
        @RequestParam("razorpay_order_id") String razorpay_order_id,
        @RequestParam("razorpay_signature") String razorpay_signature,
        HttpSession session
    ) {

        try {
            String secret = "AzXeWwaAgMC1kg7NoXAGQW73"; 

            String data = razorpay_order_id + "|" + razorpay_payment_id;

            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256"));

            byte[] hash = mac.doFinal(data.getBytes());
            String generatedSignature = new String(org.apache.commons.codec.binary.Hex.encodeHex(hash));

            if (generatedSignature.equals(razorpay_signature)) {

                List<String> cart = (List<String>) session.getAttribute("cart");
                List<Double> prices = (List<Double>) session.getAttribute("prices");
                System.out.println("razorpay signature = generated signature ");
                Orders order = new Orders();
                order.setStatus("Placed");
                order.setPaymentStatus("PAID");
                order.setDeliveryStatus("Preparing");

                List<OrderItem> orderItems = new ArrayList<>();
                double total = 0;

                for (int i = 0; i < cart.size(); i++) {

                    String[] parts = cart.get(i).split(" x ");
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

                
                order.setPaymentId(razorpay_payment_id);
                order.setRazorpayOrderId(razorpay_order_id);
                order.setSignature(razorpay_signature);

                orderRepo.save(order);

                session.removeAttribute("cart");
                session.removeAttribute("prices");

                return "success";
            }

        } catch (Exception e) {
        	System.out.println("Error: " + e.getMessage());
        	System.out.println("razorpay signature != generated signature ");
            e.printStackTrace();
        }

        return "failure";
    }
}
