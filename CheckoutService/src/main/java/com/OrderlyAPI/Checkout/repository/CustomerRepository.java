package com.OrderlyAPI.Checkout.repository;

import com.OrderlyAPI.Checkout.model.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerModel, Integer> {

}

