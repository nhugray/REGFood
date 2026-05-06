package com.finalterm.regfood.features.auth.domain.model;

public class AuthResult {
    private boolean success;
    private String errorMessage;
    private User user;

    public AuthResult(boolean success, String errorMessage, User user) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public User getUser() {
        return user;
    }

    public static AuthResult success(User user) {
        return new AuthResult(true, null, user);
    }

    public static AuthResult failure(String errorMessage) {
        return new AuthResult(false, errorMessage, null);
    }
}
