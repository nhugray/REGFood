package com.finalterm.regfood.features.auth.domain.usecase;

import com.finalterm.regfood.features.auth.domain.model.AuthResult;
import com.finalterm.regfood.features.auth.domain.model.User;
import com.finalterm.regfood.features.auth.domain.repository.AuthRepository;

public class LoginWithEmailUseCase {
    private final AuthRepository authRepository;

    public LoginWithEmailUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String email, String password, AuthRepository.AuthCallback callback) {
        authRepository.loginWithEmail(email, password, callback);
    }
}
