package com.OrderlyAPI.Authorization.controller;

import com.OrderlyAPI.Authorization.model.AuthenticationResponse;
import com.OrderlyAPI.Authorization.model.CustomerLoginRequest;
import com.OrderlyAPI.Authorization.model.CustomerRegistrationRequest;
import com.OrderlyAPI.Authorization.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// Authentication Overview Process :
// The client sends a request to the server with the user's credentials (username and password) over an HTTPS connection
// The server validates the credentials against a user database or an authentication service. If the credentials are valid,
// THE SERVER GENERATES A JWT TOKEN

// The JWT token typically contains information such as the token issuer, expiration time, and other optional claims, but not the user-specific data
// The server signs the JWT token using a secret key or a private key, creating a digitally signed token
// The server sends the JWT token back to the client as part of the authentication response


// This class is responsible for controlling the endpoint for which users can
// access to gain the validated JWT token ()
@RestController
@RequestMapping("OrderlyAPI/auth")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @Autowired
    AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping(value = "/registration", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public AuthenticationResponse registration(@RequestBody CustomerRegistrationRequest registrationRequest) {
        if (registrationRequest == null) {
            return AuthenticationResponse
                    .builder()
                    .JWT_token(null)
                    .DashboardURL("/login_page")
                    .errorMessage("Bad Request -> was empty")
                    .build();
        }

        return authorizationService.register(registrationRequest);
    }

    @PostMapping(value = "/authentication", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public AuthenticationResponse authenticateLogin(@RequestBody CustomerLoginRequest loginRequest) {
        if (loginRequest == null) {
            return AuthenticationResponse
                    .builder()
                    .JWT_token(null)
                    .DashboardURL("/login_page")
                    .errorMessage("Bad Request -> was empty... ")
                    .build();
        }

        return authorizationService.authenticate(loginRequest);
    }
}
