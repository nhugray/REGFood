package com.finalterm.regfood.features.auth.domain.usecase;

import com.finalterm.regfood.features.auth.domain.model.User;
import com.finalterm.regfood.features.auth.domain.repository.AuthRepository;

public class GetCurrentUserUseCase {
    private final AuthRepository authRepository;

    public GetCurrentUserUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public User execute() {
        return authRepository.getCurrentUser();
    }
}
