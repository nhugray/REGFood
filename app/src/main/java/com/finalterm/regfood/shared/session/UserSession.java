package com.finalterm.regfood.shared.session;

import com.finalterm.regfood.features.auth.domain.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public final class UserSession {

    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static boolean loginPromptRequested = false;

    private UserSession() {
        // Utility class
    }

    public static boolean isGuest() {
        return firebaseAuth.getCurrentUser() == null;
    }

    public static String getCurrentEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getEmail() : "";
    }

    public static String getDisplayName() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getDisplayName() : "";
    }

    public static User getCurrentUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) return null;
        return new User(
                firebaseUser.getUid(),
                firebaseUser.getEmail(),
                firebaseUser.getDisplayName(),
                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null
        );
    }

    // For backward compatibility, but now async
    public static boolean login(String email, String password) {
        // This is synchronous call, but Firebase is async. For demo, keep old behavior
        // In real app, should use callbacks
        // For now, assume login is handled elsewhere
        return false;
    }

    public static void logout() {
        firebaseAuth.signOut();
        loginPromptRequested = false;
    }

    public static void requestLoginPrompt() {
        loginPromptRequested = true;
    }

    public static boolean consumeLoginPrompt() {
        boolean shouldPrompt = loginPromptRequested;
        loginPromptRequested = false;
        return shouldPrompt;
    }
}
