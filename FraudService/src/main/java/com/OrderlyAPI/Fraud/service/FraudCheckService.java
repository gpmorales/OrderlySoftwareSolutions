package com.OrderlyAPI.Fraud.service;

import com.OrderlyAPI.Fraud.model.FraudCheckHistory;
import com.OrderlyAPI.Fraud.repository.FraudCheckHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FraudCheckService {
    private final FraudCheckHistoryRepository FCHrepository;

    @Autowired
    FraudCheckService(FraudCheckHistoryRepository FCHrepository){
        this.FCHrepository = FCHrepository;
    }

    // Perform Business logic here
    public boolean isFraudulentCustomer(String customerEmail, String customerPhoneNumber) {
        // USE THIRD PARTY HERE TO CHECK IF CORRESPONDING EMAIL IS NOT A SPAM ONE
        Optional<FraudCheckHistory> customer_history = FCHrepository.
                findFraudCheckHistoryByEmailOrPhoneNumber(customerEmail, customerPhoneNumber);

        // If we have checked this email/customer before
        if (customer_history.isPresent()) {
            FraudCheckHistory Customer_history = customer_history.get();
            if ((Customer_history.getEmail().equals(customerEmail) || Customer_history.getPhoneNumber().equals(customerPhoneNumber)) && Customer_history.isFraudster())
                return true;

            if (Customer_history.getEmail().equals(customerEmail) || Customer_history.getPhoneNumber().equals(customerPhoneNumber))
                return false;
        }

        // Otherwise we have not checked this customerId before
        // So we will add it to our fraud-check-history repository
        boolean External_Fraud_Check = THIRD_PARTY_CHECK(customerEmail, customerPhoneNumber);

        FCHrepository.save(
                FraudCheckHistory.builder()
                        .isFraudster(External_Fraud_Check)
                        .phoneNumber(customerPhoneNumber)
                        .email(customerEmail)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return External_Fraud_Check;
    }

    //TODO
    public boolean THIRD_PARTY_CHECK(String customerEmail, String customerPhoneNumber){
        return false;
    }

}
