package com.finalterm.regfood.features.home.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.finalterm.regfood.R;
import com.finalterm.regfood.features.foodrecognition.view.ui.FoodRecognitionFragment;
import com.finalterm.regfood.shared.session.UserSession;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class HomeFragment extends Fragment {

    private View homeStateRoot;
    private View historyStateRoot;
    private View accountStateRoot;
    private View loginStateRoot;
    private View scanStateRoot;
    private View resultStateRoot;
    private TextView tvHomeGreeting;
    private TextView tvProfileHomeInitial;
    private TextView tvProfileHistoryInitial;
    private TextView tvProfileScanInitial;
    private TextView tvProfileResultInitial;
    private TextView tvAccountDisplayName;
    private TextView btnLoginFromAccount;
    private View historyGuestLoginBar;
    private View historyGuestPreviewRoot;
    private View historyMemberRoot;
    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private View btnGoogleSignIn;
    private FrameLayout foodRecognitionContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeStateRoot = view.findViewById(R.id.homeStateRoot);
        historyStateRoot = view.findViewById(R.id.historyStateRoot);
        accountStateRoot = view.findViewById(R.id.accountStateRoot);
        loginStateRoot = view.findViewById(R.id.loginStateRoot);
        scanStateRoot = view.findViewById(R.id.scanStateRoot);
        resultStateRoot = view.findViewById(R.id.resultStateRoot);
        tvHomeGreeting = view.findViewById(R.id.tvHomeGreeting);
        tvProfileHomeInitial = view.findViewById(R.id.tvProfileHomeInitial);
        tvProfileHistoryInitial = view.findViewById(R.id.tvProfileHistoryInitial);
        tvProfileScanInitial = view.findViewById(R.id.tvProfileScanInitial);
        tvProfileResultInitial = view.findViewById(R.id.tvProfileResultInitial);
        tvAccountDisplayName = view.findViewById(R.id.tvAccountDisplayName);
        btnLoginFromAccount = view.findViewById(R.id.btnLoginFromAccount);
        historyGuestLoginBar = view.findViewById(R.id.historyGuestLoginBar);
        historyGuestPreviewRoot = view.findViewById(R.id.historyGuestPreviewRoot);
        historyMemberRoot = view.findViewById(R.id.historyMemberRoot);
        etLoginEmail = view.findViewById(R.id.etLoginEmail);
        etLoginPassword = view.findViewById(R.id.etLoginPassword);
        btnGoogleSignIn = view.findViewById(R.id.btnGoogleSignIn);
        foodRecognitionContainer = view.findViewById(R.id.foodRecognitionContainer);

        view.findViewById(R.id.btnScanMeal).setOnClickListener(v -> showScanState());
        view.findViewById(R.id.btnManualLog).setOnClickListener(v -> showHistoryState());
        view.findViewById(R.id.btnProfileHome).setOnClickListener(v -> showAccountState());
        view.findViewById(R.id.btnProfileHistory).setOnClickListener(v -> showAccountState());
        view.findViewById(R.id.btnProfileScan).setOnClickListener(v -> showAccountState());
        view.findViewById(R.id.btnProfileResult).setOnClickListener(v -> showAccountState());
        view.findViewById(R.id.btnLoginFromHistory).setOnClickListener(v -> showLoginState());
        btnLoginFromAccount.setOnClickListener(v -> {
            if (UserSession.isGuest()) {
                showLoginState();
                return;
            }
            UserSession.logout();
            Toast.makeText(requireContext(), R.string.auth_logout_success, Toast.LENGTH_SHORT).show();
            showHomeState();
        });
        view.findViewById(R.id.btnLoginSubmit).setOnClickListener(v -> handleLogin());
        view.findViewById(R.id.btnBackToAccount).setOnClickListener(v -> showAccountState());
        view.findViewById(R.id.btnBackFromHistory).setOnClickListener(v -> showHomeState());
        view.findViewById(R.id.btnCapturePhoto).setOnClickListener(v -> openFoodRecognition());
        view.findViewById(R.id.btnUploadPhoto).setOnClickListener(v -> openFoodRecognition());
        view.findViewById(R.id.btnBackHomeFromResultCard).setOnClickListener(v ->
            Toast.makeText(requireContext(), R.string.favorite_added_hint, Toast.LENGTH_SHORT).show()
        );
        view.findViewById(R.id.btnScanAnother).setOnClickListener(v -> openFoodRecognition());
        btnGoogleSignIn.setOnClickListener(v -> handleGoogleSignIn());

        refreshUserUi();
        if (UserSession.consumeLoginPrompt()) {
            showLoginState();
        } else {
            showHomeState();
        }
    }

    public void resetToMainHome() {
        if (homeStateRoot != null) {
            if (foodRecognitionContainer != null) {
                foodRecognitionContainer.setVisibility(View.GONE);
            }
            showHomeState();
        }
    }

    private void showHomeState() {
        refreshUserUi();
        homeStateRoot.setVisibility(View.VISIBLE);
        historyStateRoot.setVisibility(View.GONE);
        accountStateRoot.setVisibility(View.GONE);
        loginStateRoot.setVisibility(View.GONE);
        scanStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.GONE);
        if (foodRecognitionContainer != null) {
            foodRecognitionContainer.setVisibility(View.GONE);
        }
    }

    private void showHistoryState() {
        refreshUserUi();
        homeStateRoot.setVisibility(View.GONE);
        historyStateRoot.setVisibility(View.VISIBLE);
        accountStateRoot.setVisibility(View.GONE);
        loginStateRoot.setVisibility(View.GONE);
        scanStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.GONE);
    }

    private void showAccountState() {
        refreshUserUi();
        homeStateRoot.setVisibility(View.GONE);
        historyStateRoot.setVisibility(View.GONE);
        accountStateRoot.setVisibility(View.VISIBLE);
        loginStateRoot.setVisibility(View.GONE);
        scanStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.GONE);
    }

    private void showLoginState() {
        homeStateRoot.setVisibility(View.GONE);
        historyStateRoot.setVisibility(View.GONE);
        accountStateRoot.setVisibility(View.GONE);
        loginStateRoot.setVisibility(View.VISIBLE);
        scanStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.GONE);
    }

    private void showScanState() {
        refreshUserUi();
        homeStateRoot.setVisibility(View.GONE);
        historyStateRoot.setVisibility(View.GONE);
        accountStateRoot.setVisibility(View.GONE);
        loginStateRoot.setVisibility(View.GONE);
        scanStateRoot.setVisibility(View.VISIBLE);
        resultStateRoot.setVisibility(View.GONE);
        foodRecognitionContainer.setVisibility(View.GONE);
    }

    private void openFoodRecognition() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.foodRecognitionContainer, new FoodRecognitionFragment())
                .addToBackStack(null)
                .commit();
        
        homeStateRoot.setVisibility(View.GONE);
        historyStateRoot.setVisibility(View.GONE);
        accountStateRoot.setVisibility(View.GONE);
        loginStateRoot.setVisibility(View.GONE);
        scanStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.GONE);
        foodRecognitionContainer.setVisibility(View.VISIBLE);
    }

    private void showResultState() {
        refreshUserUi();
        homeStateRoot.setVisibility(View.GONE);
        historyStateRoot.setVisibility(View.GONE);
        accountStateRoot.setVisibility(View.GONE);
        loginStateRoot.setVisibility(View.GONE);
        scanStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.VISIBLE);
    }

    private void handleLogin() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), R.string.auth_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), R.string.auth_login_success, Toast.LENGTH_SHORT).show();
                        etLoginEmail.setText("");
                        etLoginPassword.setText("");
                        showHomeState();
                    } else {
                        String message = task.getException() != null ? task.getException().getMessage() : getString(R.string.auth_invalid_credentials);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleGoogleSignIn() {
        CredentialManager credentialManager = CredentialManager.create(requireContext());

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                requireActivity(),
                request,
                null,
                requireActivity().getMainExecutor(),
                new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignIn(result);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        Toast.makeText(requireContext(), "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void handleSignIn(GetCredentialResponse result) {
        Credential credential = result.getCredential();

        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(customCredential.getData());
                String idToken = googleIdTokenCredential.getIdToken();

                FirebaseAuth.getInstance()
                        .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), R.string.auth_login_success, Toast.LENGTH_SHORT).show();
                                etLoginEmail.setText("");
                                etLoginPassword.setText("");
                                showHomeState();
                            } else {
                                String message = task.getException() != null ? task.getException().getMessage() : "Firebase sign-in failed.";
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                return;
            }
        }

        Toast.makeText(requireContext(), "No Google credential returned.", Toast.LENGTH_SHORT).show();
    }

    private void refreshUserUi() {
        if (tvHomeGreeting == null) {
            return;
        }

        String displayName = UserSession.isGuest()
            ? getString(R.string.user_display_guest)
            : UserSession.getCurrentEmail();
        String profileInitial = UserSession.isGuest() ? "G" : getEmailInitial(UserSession.getCurrentEmail());

        tvHomeGreeting.setText(getString(R.string.home_greeting_format, displayName));
        tvProfileHomeInitial.setText(profileInitial);
        tvProfileHistoryInitial.setText(profileInitial);
        tvProfileScanInitial.setText(profileInitial);
        tvProfileResultInitial.setText(profileInitial);
        tvAccountDisplayName.setText(displayName);
        btnLoginFromAccount.setText(UserSession.isGuest() ? R.string.action_login : R.string.action_logout);

        boolean isGuest = UserSession.isGuest();
        historyGuestLoginBar.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        historyGuestPreviewRoot.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        historyMemberRoot.setVisibility(isGuest ? View.GONE : View.VISIBLE);
    }

    private String getEmailInitial(String email) {
        if (email == null || email.isEmpty()) {
            return "U";
        }
        return String.valueOf(Character.toUpperCase(email.charAt(0)));
    }
}
