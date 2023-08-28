package com.OrderlyAPI.Checkout.controllers;

import com.OrderlyAPI.Checkout.model.OrderModel;
import com.OrderlyAPI.Checkout.service.CheckoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("OrderlyAPI/checkout")
public class CheckoutController {
    private final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    private final CheckoutService checkoutService;

    CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping(value = "/confirmCheckout", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> addItem(@RequestParam String customerId,
                                          @RequestBody Map<String, String> orderData) {
        try {
            if (customerId == null || customerId.isEmpty()) {
                return ResponseEntity.badRequest().body("Params are empty or null");
            }

            if (orderData.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid order request");
            }

            final Optional<OrderModel> orderResult = checkoutService.insertOrder(customerId, orderData);

            if (orderResult.isEmpty()) {
                return ResponseEntity.internalServerError().body("Customer account was not found");
            }

            return ResponseEntity.ok().body(orderResult);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
