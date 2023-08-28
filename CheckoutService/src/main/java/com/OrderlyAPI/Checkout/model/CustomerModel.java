package com.OrderlyAPI.Checkout.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_model")
public class CustomerModel {
    @Id
    @SequenceGenerator(
            name = "customer_id_sequence",
            sequenceName = "customer_id_sequence"
    )
    @GeneratedValue(
            strategy =  GenerationType.SEQUENCE,
            generator = "customer_id_sequence"
    )
    private Integer ID;

    /**
     * Since we cannot store complex items in relational databases, such as
     * a list or json objects, we employ multiple tables and relationships
     *
     * The OneToMany annotations establishes a bidirectional relationship
     * which allows a customer record to be referenced multiple Order records
     *
     * Parameters:
     * mappedBy - establishes the reference within the 'customer' field of the OrderModel
     *
     * When you create a OrderModel instance and add it to a CustomerModel instance's orders list
     * using the getOrders() method, the new Order instance will automatically have the
     * proper customer reference set within the field we map it to using the mappedBy.
     * */

    @NotNull
    @OneToMany(mappedBy = "customer")
    List<OrderModel> orders;

    @NotNull
    @NotEmpty
    private String firstName;

    private String lastName;

    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String password;
    private String phoneNumber;
}
