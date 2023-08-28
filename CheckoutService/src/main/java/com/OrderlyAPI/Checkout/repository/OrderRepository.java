package com.OrderlyAPI.Checkout.repository;

import com.OrderlyAPI.Checkout.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderModel, Integer> {

}

