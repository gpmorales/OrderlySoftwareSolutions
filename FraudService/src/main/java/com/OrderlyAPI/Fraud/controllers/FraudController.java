package com.OrderlyAPI.Fraud.controllers;

import com.OrderlyAPI.Fraud.service.FraudCheckService;
import com.OrderlyAPI.Fraud.model.FraudCheckResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("OrderlyAPI/fraud_check")
public class FraudController {

    private final FraudCheckService fraudCheckService;

    @Autowired
    FraudController(FraudCheckService fraudCheckService) {
        this.fraudCheckService = fraudCheckService;
    }

    // MICROSERVICES WILL COMMUNICATE via HTTP REQUESTS
    @GetMapping(path = "/{customerEmail}/{customerPhoneNumber}")
    public FraudCheckResponse isFraudster(@PathVariable("customerEmail") String customerEmail,
                                          @PathVariable("customerPhoneNumber") String customerPhoneNumber) {
        boolean isFraudulentCustomer = fraudCheckService.isFraudulentCustomer(customerEmail, customerPhoneNumber);
        return new FraudCheckResponse(isFraudulentCustomer); // Record obj w/ one field is instantiated and return
    }

}
