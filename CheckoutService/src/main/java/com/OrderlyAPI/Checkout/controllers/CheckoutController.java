package com.OrderlyAPI.Checkout.controllers;

import com.OrderlyAPI.Checkout.model.Order;
import com.OrderlyAPI.Checkout.service.CheckoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
                                          @RequestBody @Valid Order order,
                                          BindingResult bindingResult) {
        try {
            if (customerId == null || customerId.isEmpty()) {
                return ResponseEntity.badRequest().body("Params are empty or null");
            }

            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body("Invalid order request");
            }

            final Optional<Order> orderResult = checkoutService.insertOrder(customerId, order);

            if (orderResult.isEmpty()) {
                return ResponseEntity.internalServerError().body("Customer account was not found");
            }

            return ResponseEntity.ok().body(orderResult);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
