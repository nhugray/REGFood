package com.finalterm.regfood.features.journal.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.MainActivity;
import com.finalterm.regfood.R;
import com.finalterm.regfood.features.journal.data.JournalRepository;
import com.finalterm.regfood.features.journal.domain.JournalSummary;
import com.finalterm.regfood.shared.session.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalFragment extends Fragment {

    private View guestGateBar;
    private View guestPreviewRoot;
    private View memberInteractiveRoot;
    private TextView tvBreakfastValue;
    private TextView tvLunchValue;
    private TextView tvDinnerValue;
    private TextView tvNightValue;
    private MaterialButton btnOpenHistory;
    private LinearProgressIndicator progressMeal;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private JournalRepository journalRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        journalRepository = new JournalRepository(requireContext().getApplicationContext());
        guestGateBar = view.findViewById(R.id.journalGuestGateBar);
        guestPreviewRoot = view.findViewById(R.id.journalGuestPreviewRoot);
        memberInteractiveRoot = view.findViewById(R.id.journalMemberInteractiveRoot);
        tvBreakfastValue = view.findViewById(R.id.tvBreakfastValue);
        tvLunchValue = view.findViewById(R.id.tvLunchValue);
        tvDinnerValue = view.findViewById(R.id.tvDinnerValue);
        tvNightValue = view.findViewById(R.id.tvNightValue);
        btnOpenHistory = view.findViewById(R.id.btnOpenMealHistory);
        progressMeal = view.findViewById(R.id.progressMeal);

        view.findViewById(R.id.btnJournalLogin).setOnClickListener(v -> navigateToLogin());
        btnOpenHistory.setOnClickListener(v -> openMealHistory());

        renderAccessState();
        loadTodaySummary();
    }

    private void renderAccessState() {
        boolean isGuest = UserSession.isGuest();
        guestGateBar.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        guestPreviewRoot.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        memberInteractiveRoot.setVisibility(isGuest ? View.GONE : View.VISIBLE);
    }

    private void loadTodaySummary() {
        executor.execute(() -> {
            JournalSummary summary = journalRepository.loadTodaySummary();
            if (!isAdded()) {
                return;
            }
            requireActivity().runOnUiThread(() -> applySummary(summary));
        });
    }

    private void applySummary(JournalSummary summary) {
        progressMeal.setProgress((int) Math.round(summary.mealCompletionRatio * 100));

        tvBreakfastValue.setText(buildMealCopy("Bữa sáng", summary.breakfastCalories));
        tvLunchValue.setText(buildMealCopy("Bữa trưa", summary.lunchCalories));
        tvDinnerValue.setText(buildMealCopy("Bữa tối", summary.dinnerCalories));
        if (tvNightValue != null) {
            tvNightValue.setText(buildMealCopy("Bữa khuya", summary.nightCalories));
        }
    }

    private String buildMealCopy(String label, double calories) {
        if (calories <= 0) {
            return label + " • Đang chờ dữ liệu";
        }
        return String.format(Locale.getDefault(), "%s • %.0f kcal hôm nay", label, calories);
    }

    private void openMealHistory() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openMealHistory();
        }
    }

    private void navigateToLogin() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openHomeLoginState();
        }
    }

    private void saveJournal() {
        Toast.makeText(requireContext(), R.string.journal_saved_success, Toast.LENGTH_SHORT).show();
    }
}
