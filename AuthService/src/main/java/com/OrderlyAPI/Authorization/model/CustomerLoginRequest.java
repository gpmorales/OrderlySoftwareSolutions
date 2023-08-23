package com.OrderlyAPI.Authorization.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ComponentScan;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

// Holds the payload of the POST request send by a user or someone trying to sign in
// Will contain fields like username & password , etc.

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ComponentScan
public class CustomerLoginRequest {
    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String password;
}
