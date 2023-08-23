package com.OrderlyAPI.Checkout.model;

import com.OrderlyAPI.Checkout.model.MenuItem;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
@NoArgsConstructor
public class Order {

    @Id
    private Integer customerId;

    private String customerEmail;

    private List<MenuItem> orderItems;

    private double totalAmount;

    private LocalDateTime orderTime;

    private String restaurantId;

}
