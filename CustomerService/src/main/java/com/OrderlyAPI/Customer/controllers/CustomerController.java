package com.OrderlyAPI.Customer.controllers;

import com.OrderlyAPI.Customer.model.AuthenticationResponse;
import com.OrderlyAPI.Customer.model.CustomerLoginRequest;
import com.OrderlyAPI.Customer.model.CustomerRegistrationRequest;
import com.OrderlyAPI.Customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("OrderlyAPI/signIn")
public class CustomerController {

    // Service layer object -> constructor dependency injection
    private final CustomerService customerService;

    @Autowired
    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    // ALLOWS USER TO CREATE / REGISTER an ACCOUNT WITH US
    @PostMapping(value = "/create_account", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationResponse> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest customerRegistrationRequest, BindingResult bindingResult) {
        // Checks if fields are correctly validated
        if (bindingResult.hasErrors()) {
            String listErr = bindingResult.getAllErrors().toString();
            // TODO -> FRONTEND DISPLAY OF SPECIFIC INCORRECT FIELDS
            return new ResponseEntity<>(AuthenticationResponse.builder().errorMessage("Content Not Valid ... \n " + listErr).build(), HttpStatus.BAD_REQUEST);
        }

        // MAKE THE CALL TO FRAUD MICROSERVICE
        // Checks if the request data is valid (email not taken, password is not taken, is fraudulent)
        return customerService.register(customerRegistrationRequest);
    }


    // When using POST, the request payload is sent in the body of the HTTP request, which is not visible in the URL or server logs
    @PostMapping(value = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AuthenticationResponse> loginCustomer(@Valid @RequestBody CustomerLoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(AuthenticationResponse.builder()
                    .errorMessage("Username/Password Field CANNOT be blank!")
                    .build(), HttpStatus.BAD_REQUEST);
        }

        // MAKE THE CALL TO AUTHENTICATION MICROSERVICE
        // Checks if the login request data is valid (user email exists + password is correct)
        return customerService.authenticate(loginRequest);
    }

}
