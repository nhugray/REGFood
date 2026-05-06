package com.finalterm.regfood.features.auth.data.datasource;

import com.finalterm.regfood.features.auth.domain.model.AuthResult;
import com.finalterm.regfood.features.auth.domain.model.User;
import com.finalterm.regfood.features.auth.domain.repository.AuthRepository;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthDataSource {
    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthDataSource() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void loginWithEmail(String email, String password, AuthRepository.AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        User user = mapFirebaseUserToUser(firebaseUser);
                        callback.onResult(AuthResult.success(user));
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        callback.onResult(AuthResult.failure(errorMessage));
                    }
                });
    }

    public void loginWithGoogle(String idToken, AuthRepository.AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        User user = mapFirebaseUserToUser(firebaseUser);
                        callback.onResult(AuthResult.success(user));
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Google login failed";
                        callback.onResult(AuthResult.failure(errorMessage));
                    }
                });
    }

    public void registerWithEmail(String email, String password, AuthRepository.AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        User user = mapFirebaseUserToUser(firebaseUser);
                        callback.onResult(AuthResult.success(user));
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                        callback.onResult(AuthResult.failure(errorMessage));
                    }
                });
    }

    public void logout() {
        firebaseAuth.signOut();
    }

    public User getCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        return firebaseUser != null ? mapFirebaseUserToUser(firebaseUser) : null;
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    private User mapFirebaseUserToUser(FirebaseUser firebaseUser) {
        if (firebaseUser == null) return null;
        return new User(
                firebaseUser.getUid(),
                firebaseUser.getEmail(),
                firebaseUser.getDisplayName(),
                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null
        );
    }
}
