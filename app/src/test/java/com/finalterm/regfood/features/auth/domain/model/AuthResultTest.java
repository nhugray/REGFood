package com.finalterm.regfood.features.auth.domain.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AuthResultTest {

    @Test
    public void success_wrapsAuthenticatedUser() {
        User user = new User("uid-1", "user@example.com", "User", "https://example.com/avatar.png");

        AuthResult result = AuthResult.success(user);

        assertTrue(result.isSuccess());
        assertSame(user, result.getUser());
        assertNull(result.getErrorMessage());
    }

    @Test
    public void failure_wrapsErrorMessageWithoutUser() {
        AuthResult result = AuthResult.failure("Invalid credentials");

        assertFalse(result.isSuccess());
        assertEquals("Invalid credentials", result.getErrorMessage());
        assertNull(result.getUser());
    }

    @Test
    public void userSetters_updateUserFields() {
        User user = new User();

        user.setUid("uid-2");
        user.setEmail("updated@example.com");
        user.setDisplayName("Updated User");
        user.setPhotoUrl("https://example.com/updated.png");

        assertEquals("uid-2", user.getUid());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("Updated User", user.getDisplayName());
        assertEquals("https://example.com/updated.png", user.getPhotoUrl());
    }
}
