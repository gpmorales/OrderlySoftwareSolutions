package com.OrderlyAPI.Authorization.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ComponentScan;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ComponentScan
public class CustomerRegistrationRequest {
        @NotNull
        @Pattern(regexp = "[A-Za-z]{2,20}")
        private String firstName;

        @NotNull
        @Pattern(regexp = "[A-Za-z]{2,20}")
        private String lastName;

        @NotNull
        @Pattern(regexp = "[A-Za-z0-9]{2,20}@[A-Za-z0-9]{2,20}\\.[A-Za-z]{3,10}")
        private String email;

        @NotNull
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$")
        private String password;

        @NotNull
        @Pattern(regexp = "^\\+1\\d{10}$")
        private String phoneNumber;
}
