package com.OrderlyAPI.Customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class CustomerRegistrationRequest {
        @NotNull
        @Pattern(regexp = "[A-Za-z]{2,20}")
        private final String firstName;

        @NotNull
        @Pattern(regexp = "[A-Za-z]{2,20}")
        private final String lastName;

        @NotNull
        @Pattern(regexp = "[A-Za-z0-9]{2,20}@[A-Za-z0-9]{2,20}\\.[A-Za-z]{3,10}")
        private final String email;

        @NotNull
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$")
        private final String password;

        @NotNull
        @Pattern(regexp = "^\\+1\\d{10}$")
        private final String phoneNumber;
}
