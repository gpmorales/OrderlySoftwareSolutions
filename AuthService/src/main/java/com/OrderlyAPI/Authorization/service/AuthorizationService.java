package com.OrderlyAPI.Authorization.service;

import com.OrderlyAPI.Authorization.model.AuthenticationResponse;
import com.OrderlyAPI.Authorization.model.CustomerLoginRequest;
import com.OrderlyAPI.Authorization.model.CustomerModel;
import com.OrderlyAPI.Authorization.model.CustomerRegistrationRequest;
import com.OrderlyAPI.Authorization.model.Role;
import com.OrderlyAPI.Authorization.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    // Dependency injections done by constructor (all private and final fields)

    private final AuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse register(CustomerRegistrationRequest registrationRequest) {
        // Build customer to insert into db
        CustomerModel customer = CustomerModel.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(Role.USER)
                .build();

        // Insert customer
        customerRepository.save(customer);

        // Return response
        final String JWTtoken = jwtService.generateToken(customer);

        return AuthenticationResponse.builder()
                .JWT_token(JWTtoken)
                .DashboardURL("https://OrderlyAPI.com/menu")
                .errorMessage(null)
                .build();
    }

    public AuthenticationResponse authenticate(CustomerLoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        Optional<CustomerModel> customer = customerRepository.findCustomerModelByEmail(loginRequest.getEmail());

        // If user exists in our registered database
        if (customer.isPresent()) {
            String JWTtoken = jwtService.generateToken(customer.get());
            return AuthenticationResponse.builder()
                    .JWT_token(JWTtoken)
                    .DashboardURL("https://OrderlyAPI.com/menu")
                    .errorMessage(null)
                    .build();
        }

        return AuthenticationResponse.builder().build();
    }


}
