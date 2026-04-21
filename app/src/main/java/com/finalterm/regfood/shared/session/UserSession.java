package com.finalterm.regfood.shared.session;

public final class UserSession {

    private static final String DEMO_EMAIL = "vlvn@gmail.com";
    private static final String DEMO_PASSWORD = "abcd1234";

    private static boolean loggedIn = false;
    private static String currentEmail = "";
    private static boolean loginPromptRequested = false;

    private UserSession() {
        // Utility class
    }

    public static boolean isGuest() {
        return !loggedIn;
    }

    public static String getCurrentEmail() {
        return currentEmail;
    }

    public static boolean login(String email, String password) {
        if (DEMO_EMAIL.equals(email) && DEMO_PASSWORD.equals(password)) {
            loggedIn = true;
            currentEmail = DEMO_EMAIL;
            loginPromptRequested = false;
            return true;
        }
        return false;
    }

    public static void logout() {
        loggedIn = false;
        currentEmail = "";
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
