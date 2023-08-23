package com.OrderlyAPI.Authorization.repository;

import com.OrderlyAPI.Authorization.model.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerModel, Integer> {
    Optional<CustomerModel> findCustomerModelByEmail(String email);

}
