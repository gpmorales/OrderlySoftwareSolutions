package com.OrderlyAPI.Customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
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

    // @Data creates the getters and setters for this class's defined attributes
    // and a default no-arg constructor

    // @Builder creates a build pattern class that allows you
    // to create complex objects by chaining the fields where you define the attributes
    // ex : Person me = Person.builder().age(12).name("bob").phone(1234) ... etc
}
