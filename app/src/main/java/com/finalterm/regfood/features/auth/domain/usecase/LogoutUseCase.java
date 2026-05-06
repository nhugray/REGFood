package com.finalterm.regfood.features.auth.domain.usecase;

import com.finalterm.regfood.features.auth.domain.repository.AuthRepository;

public class LogoutUseCase {
    private final AuthRepository authRepository;

    public LogoutUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void execute() {
        authRepository.logout();
    }
}
