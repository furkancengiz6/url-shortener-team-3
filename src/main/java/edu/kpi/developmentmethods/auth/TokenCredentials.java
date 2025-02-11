package edu.kpi.developmentmethods.auth;

import io.micronaut.security.authentication.AuthenticationRequest;
import java.io.Serializable;

public class TokenCredentials implements Serializable, AuthenticationRequest<String, String> {
    private final String token;

    TokenCredentials(String token) {
        this.token = token;
    }

    @Override
    public String getIdentity() {
        return "Token User"; 
    }

    @Override
    public String getSecret() {
        return this.token;
    }
}
