package com.finalterm.regfood.features.auth.data.repository;

import com.finalterm.regfood.features.auth.data.datasource.FirebaseAuthDataSource;
import com.finalterm.regfood.features.auth.domain.model.AuthResult;
import com.finalterm.regfood.features.auth.domain.model.User;
import com.finalterm.regfood.features.auth.domain.repository.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuthDataSource firebaseAuthDataSource;

    public AuthRepositoryImpl(FirebaseAuthDataSource firebaseAuthDataSource) {
        this.firebaseAuthDataSource = firebaseAuthDataSource;
    }

    @Override
    public void loginWithEmail(String email, String password, AuthCallback callback) {
        firebaseAuthDataSource.loginWithEmail(email, password, callback);
    }

    @Override
    public void loginWithGoogle(String idToken, AuthCallback callback) {
        firebaseAuthDataSource.loginWithGoogle(idToken, callback);
    }

    @Override
    public void registerWithEmail(String email, String password, AuthCallback callback) {
        firebaseAuthDataSource.registerWithEmail(email, password, callback);
    }

    @Override
    public void logout() {
        firebaseAuthDataSource.logout();
    }

    @Override
    public User getCurrentUser() {
        return firebaseAuthDataSource.getCurrentUser();
    }

    @Override
    public boolean isUserLoggedIn() {
        return firebaseAuthDataSource.isUserLoggedIn();
    }
}
