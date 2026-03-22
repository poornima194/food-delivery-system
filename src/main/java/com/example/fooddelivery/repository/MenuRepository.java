package com.example.fooddelivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fooddelivery.model.Menu;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
}