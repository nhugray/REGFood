package com.finalterm.regfood.features.auth.domain.repository;

import com.finalterm.regfood.features.auth.domain.model.AuthResult;
import com.finalterm.regfood.features.auth.domain.model.User;

public interface AuthRepository {
    void loginWithEmail(String email, String password, AuthCallback callback);
    void loginWithGoogle(String idToken, AuthCallback callback);
    void registerWithEmail(String email, String password, AuthCallback callback);
    void logout();
    User getCurrentUser();
    boolean isUserLoggedIn();

    interface AuthCallback {
        void onResult(AuthResult result);
    }
}
