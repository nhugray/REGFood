package com.finalterm.regfood.features.auth.domain.usecase;

import com.finalterm.regfood.features.auth.domain.repository.AuthRepository;

public class LoginWithGoogleUseCase {
    private final AuthRepository authRepository;

    public LoginWithGoogleUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute(String idToken, AuthRepository.AuthCallback callback) {
        authRepository.loginWithGoogle(idToken, callback);
    }
}
