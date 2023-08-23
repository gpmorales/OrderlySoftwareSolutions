package com.OrderlyAPI.Checkout.service;

import com.OrderlyAPI.Checkout.model.CustomerModel;
import com.OrderlyAPI.Checkout.model.Order;
import com.OrderlyAPI.Checkout.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckoutService {

    private final MongoTemplate mongoOrderDAO;

    private final CustomerRepository customerRepository;

    @Autowired
    CheckoutService(MongoTemplate mongoOrderDAO, CustomerRepository customerRepository) {
        this.mongoOrderDAO = mongoOrderDAO;
        this.customerRepository = customerRepository;
    }

    public Optional<Order> insertOrder(String restaurantId, String customerId, Order order) {
        final Integer id = Integer.parseInt(customerId);

        final Optional<CustomerModel> customerQueryResult = customerRepository.findById(id);

        if (customerQueryResult.isEmpty()) {
            return Optional.empty();
        }

        if (!mongoOrderDAO.collectionExists(restaurantId)) {
            return Optional.empty();
        }

        return Optional.of(mongoOrderDAO.insert(order, restaurantId));
    }

}
