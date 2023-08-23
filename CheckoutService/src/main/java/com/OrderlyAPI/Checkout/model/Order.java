package com.OrderlyAPI.Checkout.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Table(name = "orders")
public class Order {
    @Id
    @SequenceGenerator(
            name = "order_id_sequence",
            sequenceName = "order_id_sequence"
    )
    @GeneratedValue(
            strategy =  GenerationType.SEQUENCE,
            generator = "order_id_sequence"
    )
    private Integer ID;

    @NotNull
    @NotEmpty
    private Integer customerId;

    @NotNull
    @NotEmpty
    private String restaurantId;

    @NotNull
    @NotEmpty
    private String customerEmail;

    @NotNull
    @NotEmpty
    private String orderItems;

    private double totalAmount;

    @NotNull
    private LocalDateTime orderTime;
}
