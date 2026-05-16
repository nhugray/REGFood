package com.finalterm.regfood.features.foodrecognition.view.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.R;
import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.repository.MealRepository;
import com.finalterm.regfood.shared.session.UserSession;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealHistoryFragment extends Fragment {

    private LinearLayout historyContainer;
    private View emptyState;
    private TextView tvSummary;
    private MealRepository mealRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyContainer = view.findViewById(R.id.historyContainer);
        emptyState = view.findViewById(R.id.emptyState);
        tvSummary = view.findViewById(R.id.tvHistorySummary);
        mealRepository = new MealRepository(requireContext().getApplicationContext());

        loadHistory();
    }

    private void loadHistory() {
        final long userId = UserSession.isGuest() ? 0L : Math.abs(UserSession.getCurrentEmail().hashCode());
        executor.execute(() -> {
            List<MealLogEntity> meals = mealRepository.getMealsByUser(userId);
            requireActivity().runOnUiThread(() -> renderHistory(meals));
        });
    }

    private void renderHistory(List<MealLogEntity> meals) {
        historyContainer.removeAllViews();
        if (meals == null) {
            meals = new ArrayList<>();
        }

        tvSummary.setText(String.format(Locale.US, "%d món đã lưu", meals.size()));
        emptyState.setVisibility(meals.isEmpty() ? View.VISIBLE : View.GONE);
        historyContainer.setVisibility(meals.isEmpty() ? View.GONE : View.VISIBLE);

        for (MealLogEntity meal : meals) {
            historyContainer.addView(createMealCard(meal));
        }
    }

    private View createMealCard(MealLogEntity meal) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View card = inflater.inflate(R.layout.item_meal_history, historyContainer, false);

        ImageView image = card.findViewById(R.id.ivMealImage);
        TextView title = card.findViewById(R.id.tvMealName);
        TextView subtitle = card.findViewById(R.id.tvMealMeta);
        TextView calories = card.findViewById(R.id.tvMealCalories);
        TextView date = card.findViewById(R.id.tvMealDate);
        View fallback = card.findViewById(R.id.imageFallback);

        title.setText(!TextUtils.isEmpty(meal.foodNameSnapshot) ? meal.foodNameSnapshot : "Món ăn");
        subtitle.setText(buildMetaText(meal));
        calories.setText(String.format(Locale.US, "%.0f kcal", meal.totalCalories));
        date.setText(formatDate(meal.eatenAt));

        bindImage(image, fallback, meal.imageLocalPath);
        return card;
    }

    private String buildMetaText(MealLogEntity meal) {
        String portion = !TextUtils.isEmpty(meal.portionName) ? meal.portionName : "";
        String toppings = !TextUtils.isEmpty(meal.addonSummary) ? meal.addonSummary : "Không có topping";
        if (TextUtils.isEmpty(portion)) {
            return toppings;
        }
        return portion + " • " + toppings;
    }

    private String formatDate(long time) {
        return new SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault()).format(new Date(time));
    }

    private void bindImage(ImageView imageView, View fallback, String imageLocalPath) {
        File file = !TextUtils.isEmpty(imageLocalPath) ? new File(imageLocalPath) : null;
        if (file != null && file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                fallback.setVisibility(View.GONE);
                return;
            }
        }

        imageView.setVisibility(View.GONE);
        fallback.setVisibility(View.VISIBLE);
    }
}
