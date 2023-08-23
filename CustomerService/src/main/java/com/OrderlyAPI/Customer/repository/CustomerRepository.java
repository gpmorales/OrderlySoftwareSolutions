package com.OrderlyAPI.Customer.repository;

import com.OrderlyAPI.Customer.model.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerModel, Integer> {
    Optional<CustomerModel> findCustomerModelByEmail(String email);
    Optional<CustomerModel> findCustomerModelByPassword(String password);
}
