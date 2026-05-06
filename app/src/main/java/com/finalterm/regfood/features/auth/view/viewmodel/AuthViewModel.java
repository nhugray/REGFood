package com.finalterm.regfood.features.auth.view.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.finalterm.regfood.features.auth.domain.model.AuthResult;
import com.finalterm.regfood.features.auth.domain.model.User;
import com.finalterm.regfood.features.auth.domain.repository.AuthRepository;
import com.finalterm.regfood.features.auth.domain.usecase.GetCurrentUserUseCase;
import com.finalterm.regfood.features.auth.domain.usecase.LoginWithEmailUseCase;
import com.finalterm.regfood.features.auth.domain.usecase.LoginWithGoogleUseCase;
import com.finalterm.regfood.features.auth.domain.usecase.LogoutUseCase;

public class AuthViewModel extends ViewModel {
    private final LoginWithEmailUseCase loginWithEmailUseCase;
    private final LoginWithGoogleUseCase loginWithGoogleUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final LogoutUseCase logoutUseCase;

    private final MutableLiveData<AuthResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public AuthViewModel(AuthRepository authRepository) {
        this.loginWithEmailUseCase = new LoginWithEmailUseCase(authRepository);
        this.loginWithGoogleUseCase = new LoginWithGoogleUseCase(authRepository);
        this.getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
        this.logoutUseCase = new LogoutUseCase(authRepository);
    }

    public LiveData<AuthResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loginWithEmail(String email, String password) {
        isLoading.setValue(true);
        loginWithEmailUseCase.execute(email, password, result -> {
            isLoading.setValue(false);
            loginResult.setValue(result);
        });
    }

    public void loginWithGoogle(String idToken) {
        isLoading.setValue(true);
        loginWithGoogleUseCase.execute(idToken, result -> {
            isLoading.setValue(false);
            loginResult.setValue(result);
        });
    }

    public User getCurrentUser() {
        return getCurrentUserUseCase.execute();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public void logout() {
        logoutUseCase.execute();
    }
}
