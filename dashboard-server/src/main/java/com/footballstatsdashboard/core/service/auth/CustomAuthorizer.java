package com.footballstatsdashboard.core.service.auth;

import com.footballstatsdashboard.api.model.User;
import io.dropwizard.auth.Authorizer;

import java.util.Objects;

public class CustomAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
        return user.getRole() != null && Objects.equals(user.getRole(), role);
    }
}