package com.OrderlyAPI.Authorization.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String JWT_token;
    private String DashboardURL;
    private String errorMessage;
}
