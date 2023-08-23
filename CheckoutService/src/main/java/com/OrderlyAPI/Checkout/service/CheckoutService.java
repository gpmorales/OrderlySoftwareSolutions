package com.OrderlyAPI.Checkout.service;

import com.OrderlyAPI.Checkout.model.CustomerModel;
import com.OrderlyAPI.Checkout.model.Order;
import com.OrderlyAPI.Checkout.repository.CustomerRepository;
import com.OrderlyAPI.Checkout.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckoutService {

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    @Autowired
    CheckoutService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public Optional<Order> insertOrder(String customerId, Order order) {
        final Integer id = Integer.parseInt(customerId);

        final Optional<CustomerModel> customerQueryResult = customerRepository.findById(id);

        if (customerQueryResult.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(orderRepository.save(order));
    }

}
