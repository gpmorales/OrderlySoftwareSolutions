package com.OrderlyAPI.Checkout.service;

import com.OrderlyAPI.Checkout.model.CustomerModel;
import com.OrderlyAPI.Checkout.model.OrderModel;
import com.OrderlyAPI.Checkout.repository.CustomerRepository;
import com.OrderlyAPI.Checkout.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.InvalidPropertiesFormatException;
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

    // TODO -> finish impl
    public Optional<OrderModel> insertOrder(String customerId, OrderModel order) throws InvalidPropertiesFormatException {
        try {
            final Integer id = Integer.parseInt(customerId);

            final Optional<CustomerModel> customerQueryResult = customerRepository.findById(id);

            if (customerQueryResult.isEmpty()) {
                return Optional.empty();
            }

            /** Create bidirectional relation between the order entity and the customer entity */
            CustomerModel customer = customerQueryResult.get();
            customer.getOrders().add(order);
            order.setCustomer(customer);

            customerRepository.saveAndFlush(customer);

            return Optional.of(orderRepository.saveAndFlush(order));

        } catch (NumberFormatException e) {
            throw new InvalidPropertiesFormatException(e.getMessage());
        }
    }

}
