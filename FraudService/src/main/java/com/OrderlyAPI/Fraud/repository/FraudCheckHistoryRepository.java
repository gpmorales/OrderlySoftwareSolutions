package com.OrderlyAPI.Fraud.repository;


import com.OrderlyAPI.Fraud.model.FraudCheckHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface FraudCheckHistoryRepository extends JpaRepository<FraudCheckHistory, Integer> {
    Optional<FraudCheckHistory> findFraudCheckHistoryByEmailOrPhoneNumber(String email, String phoneNumber);
}
