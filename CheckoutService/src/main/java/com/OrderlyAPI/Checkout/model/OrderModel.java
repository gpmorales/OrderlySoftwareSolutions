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
@Table(name = "order_model")
public class OrderModel {
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

    /**
     *  ManyToOne creates a relationship between the Order_model table and the Customer_model
     *  where there can be many Order entities related to a singular Customer entity
     *
     *  Parameters:
     *  name - This attribute specifies the name of the column in the owning entity's table
     *  (in this case the order_model table) that will hold the relational data, acting as a foreign key
     *
     *  referencedColumnName - This attribute specifies the name of the column in the related
     *  entity's table (in this case the customer_model table) that will be referenced by the name column
     *  in the owning entity's table (order_model)
     */
    @NotNull
    @ManyToOne
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    private CustomerModel customer;

    @NotNull
    @NotEmpty
    private String restaurantId;

    @NotNull
    @NotEmpty
    private String orderItems;

    private double totalAmount;

    @NotNull
    private LocalDateTime orderTime;
}
