package com.example.fooddelivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fooddelivery.model.Orders;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
}