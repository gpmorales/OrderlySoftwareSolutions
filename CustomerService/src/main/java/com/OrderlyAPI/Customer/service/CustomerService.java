package com.OrderlyAPI.Customer.service;

import com.OrderlyAPI.Customer.model.AuthenticationResponse;
import com.OrderlyAPI.Customer.model.CustomerLoginRequest;
import com.OrderlyAPI.Customer.model.CustomerRegistrationRequest;
import com.OrderlyAPI.Customer.model.FraudCheckResponse;
import com.OrderlyAPI.Customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Service
public class CustomerService {
    // Service Layer -> This class handles all the business logic
    // Will also have a Database Access Object injected
    private final CustomerRepository customerRepository;
    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    CustomerService(CustomerRepository customerRepository, WebClient webClient) {
        this.customerRepository = customerRepository;
        this.webClient = webClient;
    }

    public ResponseEntity<AuthenticationResponse> register(CustomerRegistrationRequest registrationRequest) {
        try {
            // Check if email is NOT taken || password is NOT taken
            if (customerRepository.findCustomerModelByEmail(registrationRequest.getEmail()).isPresent()) {
                return new ResponseEntity<>(AuthenticationResponse.builder()
                        .errorMessage("Email already exists")
                        .build(), HttpStatus.CONFLICT);
            }

            if (customerRepository.findCustomerModelByPassword(registrationRequest.getPassword()).isPresent()) {
                return new ResponseEntity<>(AuthenticationResponse.builder()
                        .errorMessage("Password already taken")
                        .build(), HttpStatus.CONFLICT);
            }


            // TODO -> replace with FeignClient
            // CONNECT to our Fraud Micro Service & check if this is user is a fraudster
            FraudCheckResponse fraudCheckResponse = webClient.get()
                    .uri("http://FRAUD-SERVICE/OrderlyAPI/fraud_check/{customerEmail}/{customerPhoneNumber}"
                            , registrationRequest.getEmail(), registrationRequest.getPhoneNumber())
                    .retrieve()
                    .bodyToMono(FraudCheckResponse.class)
                    .block(); /** NOTE Force code to be synchronous (?? this should be blocking) */

            // Check for fraudulent account activity during registration
            if (fraudCheckResponse != null && fraudCheckResponse.isFraudster()) {
                return new ResponseEntity<>(AuthenticationResponse
                        .builder()
                        .errorMessage("Fraudster detected")
                        .build()
                        ,HttpStatus.FORBIDDEN);
                //throw new IllegalStateException("Fraudster detected"));
            }


            // TODO -> replace with FeignClient
            // CONNECT to our Authentication Micro Service
            AuthenticationResponse authenticationResponse = webClient.post()
                    .uri("http://AUTHENTICATION-SERVICE/OrderlyAPI/auth/registration")
                    .body(BodyInserters.fromValue(registrationRequest))
                    .retrieve()
                    .bodyToMono(AuthenticationResponse.class)
                    .block();

            // If user has a JWT token (which means the user was validated)
            if (authenticationResponse != null && authenticationResponse.getJWT_token() != null) {
                return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
            }

            // Otherwise, user was not fraudulent but something went wrong during registration process
            return new ResponseEntity<>(AuthenticationResponse.builder()
                    .errorMessage("SOMETHING WENT WRONG IN REGISTRATION, TRY AGAIN LATER")
                    .build(), HttpStatus.BAD_REQUEST);
        }

        catch (WebClientResponseException e) {
            return new ResponseEntity<>(AuthenticationResponse.builder()
                    .errorMessage("Something went wrong when communicating with the other microservices\n" + e)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<AuthenticationResponse> authenticate(CustomerLoginRequest loginRequest) {
        try {
            // TODO -> replace with FeignClient
            // CONNECT to our Authentication Micro Service
            AuthenticationResponse authenticationResponse = webClient.post()
                    .uri("http://AUTHENTICATION-SERVICE/OrderlyAPI/auth/authentication")
                    .body(BodyInserters.fromValue(loginRequest))
                    .retrieve()
                    .bodyToMono(AuthenticationResponse.class)
                    .block();

            // If user has a JWT token (which means he was validated)
            if (authenticationResponse != null && authenticationResponse.getJWT_token() != null) {
                return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
            }

            // Otherwise, user could not be authenticated ...
            return new ResponseEntity<>(authenticationResponse, HttpStatus.EXPECTATION_FAILED);
        }

        catch (WebClientResponseException e) {
            // Otherwise, user can not be authenticated, indicated with null JWT token
            // This is the result of the AuthManager returning a 403 HttpResponse
            return new ResponseEntity<>(AuthenticationResponse.builder()
                    .errorMessage("Username or Password is INCORRECT!")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

}
