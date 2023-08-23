package com.OrderlyAPI.Checkout.repository;

import com.OrderlyAPI.Checkout.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}

