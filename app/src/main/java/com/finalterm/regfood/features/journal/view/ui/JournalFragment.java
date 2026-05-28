package com.finalterm.regfood.features.journal.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.MainActivity;
import com.finalterm.regfood.R;
import com.finalterm.regfood.features.journal.data.JournalRepository;
import com.finalterm.regfood.features.journal.domain.JournalSummary;
import com.finalterm.regfood.local.entity.FavoriteFoodEntity;
import com.finalterm.regfood.local.repository.FavoriteFoodRepository;
import com.finalterm.regfood.shared.session.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;
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
    private TextView tvFavoritesCount;
    private LinearLayout favoritesContainer;
    private MaterialButton btnOpenHistory;
    private LinearProgressIndicator progressMeal;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private JournalRepository journalRepository;
    private FavoriteFoodRepository favoriteFoodRepository;

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
        favoriteFoodRepository = new FavoriteFoodRepository(requireContext().getApplicationContext());
        guestGateBar = view.findViewById(R.id.journalGuestGateBar);
        guestPreviewRoot = view.findViewById(R.id.journalGuestPreviewRoot);
        memberInteractiveRoot = view.findViewById(R.id.journalMemberInteractiveRoot);
        tvBreakfastValue = view.findViewById(R.id.tvBreakfastValue);
        tvLunchValue = view.findViewById(R.id.tvLunchValue);
        tvDinnerValue = view.findViewById(R.id.tvDinnerValue);
        tvNightValue = view.findViewById(R.id.tvNightValue);
        tvFavoritesCount = view.findViewById(R.id.tvFavoritesCount);
        favoritesContainer = view.findViewById(R.id.favoritesContainer);
        btnOpenHistory = view.findViewById(R.id.btnOpenMealHistory);
        progressMeal = view.findViewById(R.id.progressMeal);

        view.findViewById(R.id.btnJournalLogin).setOnClickListener(v -> navigateToLogin());
        btnOpenHistory.setOnClickListener(v -> openMealHistory());

        renderAccessState();
        loadTodaySummary();
        loadFavorites();
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
        progressMeal.setMax(summary.targetMeals);
        progressMeal.setProgress(summary.completedMeals);

        tvBreakfastValue.setText(buildMealCopy("Bữa sáng", summary.breakfastCalories));
        tvLunchValue.setText(buildMealCopy("Bữa trưa", summary.lunchCalories));
        tvDinnerValue.setText(buildMealCopy("Bữa tối", summary.dinnerCalories));
        if (tvNightValue != null) {
            tvNightValue.setText(buildMealCopy("Bữa khuya", summary.nightCalories));
        }

        TextView completion = requireView().findViewById(R.id.tvJournalCompletion);
        if (completion != null) {
            completion.setText(String.format(Locale.getDefault(), "%d/%d buổi đã có dữ liệu", summary.completedMeals, summary.targetMeals));
        }
    }

    private void loadFavorites() {
        executor.execute(() -> {
            long userId = UserSession.isGuest() ? 0L : Math.abs(UserSession.getCurrentEmail().hashCode());
            List<FavoriteFoodEntity> favorites = favoriteFoodRepository.getFavoritesByUser(userId);
            if (!isAdded()) {
                return;
            }
            requireActivity().runOnUiThread(() -> renderFavorites(favorites));
        });
    }

    private void renderFavorites(List<FavoriteFoodEntity> favorites) {
        favoritesContainer.removeAllViews();
        if (favorites == null || favorites.isEmpty()) {
            tvFavoritesCount.setText("0 món yêu thích");
            return;
        }

        tvFavoritesCount.setText(String.format(Locale.getDefault(), "%d món yêu thích", favorites.size()));
        for (FavoriteFoodEntity favorite : favorites) {
            favoritesContainer.addView(createFavoriteCard(favorite));
        }
    }

    private View createFavoriteCard(FavoriteFoodEntity favorite) {
        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = 12;
        card.setLayoutParams(params);
        card.setCardBackgroundColor(getResources().getColor(R.color.white, null));
        card.setStrokeColor(getResources().getColor(R.color.neutral_100, null));
        card.setStrokeWidth(1);
        card.setRadius(24f);

        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(16, 16, 16, 16);

        TextView title = new TextView(requireContext());
        title.setText(favorite.foodNameSnapshot);
        title.setTextColor(getResources().getColor(R.color.neutral_900, null));
        title.setTextSize(16f);
        title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);

        TextView subtitle = new TextView(requireContext());
        subtitle.setText(favorite.subtitleSnapshot);
        subtitle.setTextColor(getResources().getColor(R.color.neutral_500, null));
        subtitle.setTextSize(13f);
        subtitle.setPadding(0, 6, 0, 0);

        TextView kcal = new TextView(requireContext());
        kcal.setText(String.format(Locale.getDefault(), "%.0f kcal", favorite.caloriesSnapshot));
        kcal.setTextColor(getResources().getColor(R.color.green_700, null));
        kcal.setTextSize(14f);
        kcal.setPadding(0, 10, 0, 0);

        content.addView(title);
        content.addView(subtitle);
        content.addView(kcal);
        card.addView(content);
        return card;
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
}
