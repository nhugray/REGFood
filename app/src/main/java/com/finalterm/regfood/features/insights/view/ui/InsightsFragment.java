package com.finalterm.regfood.features.insights.view.ui;

import android.os.Bundle;
import android.view.Gravity;
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
import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.repository.MealRepository;
import com.finalterm.regfood.shared.session.UserSession;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsightsFragment extends Fragment {

    private View guestGateBar;
    private View guestPreviewRoot;
    private View memberInteractiveRoot;
    private TextView tvSummary;
    private TextView tvTotalCalories;
    private TextView tvPeakDay;
    private TextView tvGoalDays;
    private TextView tvChartNote;
    private TextView tvHabitResult;
    private LinearLayout chartContainer;
    private TextView btnGenerateInsights;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MealRepository mealRepo;
    private List<MealLogEntity> cachedMeals = new ArrayList<>();
    private String selectedPeriod = "week";
    private long selectedUserId = 0L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        guestGateBar = view.findViewById(R.id.insightsGuestGateBar);
        guestPreviewRoot = view.findViewById(R.id.insightsGuestPreviewRoot);
        memberInteractiveRoot = view.findViewById(R.id.insightsMemberInteractiveRoot);
        tvSummary = view.findViewById(R.id.tvInsightsSummary);
        tvTotalCalories = view.findViewById(R.id.tvTotalCalories);
        tvPeakDay = view.findViewById(R.id.tvPeakDay);
        tvGoalDays = view.findViewById(R.id.tvGoalDays);
        tvChartNote = view.findViewById(R.id.tvChartNote);
        tvHabitResult = view.findViewById(R.id.tvInsightsHabitResult);
        chartContainer = view.findViewById(R.id.chartContainer);
//        btnGenerateInsights = view.findViewById(R.id.btnGenerateInsights);

        mealRepo = new MealRepository(requireContext().getApplicationContext());
        selectedUserId = UserSession.isGuest() ? 0L : Math.abs(UserSession.getCurrentEmail().hashCode());

        view.findViewById(R.id.btnInsightsLogin).setOnClickListener(v -> navigateToLogin());
//        btnGenerateInsights.setOnClickListener(v -> refreshInsights());

        renderAccessState();
        if (!UserSession.isGuest()) {
            refreshInsights();
        }
    }

    private void renderAccessState() {
        boolean isGuest = UserSession.isGuest();
        guestGateBar.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        guestPreviewRoot.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        memberInteractiveRoot.setVisibility(isGuest ? View.GONE : View.VISIBLE);
    }

    private void refreshInsights() {
        executor.execute(() -> {
            List<MealLogEntity> meals = mealRepo.getMealsByUser(selectedUserId);
            cachedMeals = meals != null ? meals : new ArrayList<>();
            final InsightData data = buildInsightData(cachedMeals);
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> renderInsights(data));
        });
    }

    private InsightData buildInsightData(List<MealLogEntity> meals) {
        if (meals == null) meals = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_YEAR, -6);
        long start = startOfDay(now.getTimeInMillis());
        Calendar next = Calendar.getInstance();
        next.setTimeInMillis(start);
        next.add(Calendar.DAY_OF_YEAR, 7);
        long end = startOfDay(next.getTimeInMillis());

        Map<Long, Double> caloriesByDay = new HashMap<>();
        for (MealLogEntity meal : meals) {
            if (meal.eatenAt >= start && meal.eatenAt < end) {
                long dayKey = startOfDay(meal.eatenAt);
                caloriesByDay.put(dayKey, caloriesByDay.getOrDefault(dayKey, 0d) + meal.totalCalories);
            }
        }

        List<DayBucket> buckets = new ArrayList<>();
        Calendar cursor = Calendar.getInstance();
        cursor.setTimeInMillis(start);
        for (int i = 0; i < 7; i++) {
            long dayKey = startOfDay(cursor.getTimeInMillis());
            double calories = caloriesByDay.getOrDefault(dayKey, 0d);
            buckets.add(new DayBucket(dayKey, calories));
            cursor.add(Calendar.DAY_OF_YEAR, 1);
        }

        double totalCalories = 0d;
        double peakCalories = 0d;
        String peakLabel = "--";
        int goalDays = 0;
        double goalCalories = 1800d;

        for (DayBucket bucket : buckets) {
            totalCalories += bucket.calories;
            if (bucket.calories > peakCalories) {
                peakCalories = bucket.calories;
                peakLabel = bucket.label;
            }
            if (bucket.calories > 0 && bucket.calories <= goalCalories) {
                goalDays++;
            }
        }

        return new InsightData(buckets, totalCalories, peakLabel, goalDays, goalCalories);
    }

    private void renderInsights(InsightData data) {
        tvSummary.setText("7 ngày gần nhất với dữ liệu thật từ nhật ký bữa ăn.");
        tvTotalCalories.setText(String.format(Locale.getDefault(), "%.0f", data.totalCalories));
        tvPeakDay.setText(data.peakLabel);
        tvGoalDays.setText(String.valueOf(data.goalDays));
        tvChartNote.setText(String.format(Locale.getDefault(), "Mục tiêu tham chiếu: %.0f kcal/ngày", data.goalCalories));
        renderChart(data.buckets, data.goalCalories);
        tvHabitResult.setText(buildInsightCopy(data));
    }

    private void renderChart(List<DayBucket> buckets, double goalCalories) {
        chartContainer.removeAllViews();
        double max = 0d;
        for (DayBucket bucket : buckets) {
            max = Math.max(max, bucket.calories);
        }
        if (max <= 0d) max = goalCalories;

        for (DayBucket bucket : buckets) {
            LinearLayout column = new LinearLayout(requireContext());
            column.setOrientation(LinearLayout.VERTICAL);
            column.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams columnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            columnParams.setMargins(4, 0, 4, 0);
            column.setLayoutParams(columnParams);

            TextView value = new TextView(requireContext());
            value.setText(bucket.calories > 0 ? String.format(Locale.getDefault(), "%.0f", bucket.calories) : "");
            value.setTextColor(requireContext().getColor(R.color.neutral_500));
            value.setTextSize(11f);
            value.setPadding(0, 0, 0, 6);

            View bar = new View(requireContext());
            int height = (int) Math.max(12d, (bucket.calories / max) * 170d);
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            bar.setLayoutParams(barParams);
            bar.setBackgroundColor(requireContext().getColor(bucket.calories > goalCalories ? R.color.orange_500 : R.color.green_500));

            TextView label = new TextView(requireContext());
            label.setText(bucket.label);
            label.setTextColor(requireContext().getColor(R.color.neutral_500));
            label.setTextSize(11f);
            label.setPadding(0, 8, 0, 0);

            column.addView(value);
            column.addView(bar);
            column.addView(label);
            chartContainer.addView(column);
        }
    }

    private String buildInsightCopy(InsightData data) {
        if (data.buckets.isEmpty()) return "Chưa có bữa ăn nào trong khoảng thời gian này.";

        DayBucket topDay = Collections.max(data.buckets, Comparator.comparingDouble(b -> b.calories));
        DayBucket lowDay = null;
        for (DayBucket bucket : data.buckets) {
            if (bucket.calories > 0 && (lowDay == null || bucket.calories < lowDay.calories)) {
                lowDay = bucket;
            }
        }

        String peakText = topDay.calories > 0
                ? String.format(Locale.getDefault(), "%s là ngày cao nhất với %.0f kcal.", topDay.label, topDay.calories)
                : "Chưa có ngày nào có dữ liệu.";
        String lowText = lowDay != null
                ? String.format(Locale.getDefault(), "Ngày nhẹ nhất của bạn là %s.", lowDay.label)
                : "Chưa đủ dữ liệu để nhận diện ngày nhẹ nhất.";
        String trendText = data.goalDays >= 4
                ? "Nhịp ăn khá ổn, bạn đang giữ nhiều ngày nằm trong vùng mục tiêu."
                : "Bạn nên xem lại các ngày vượt mục tiêu để cân bằng hơn.";
        return peakText + " " + lowText + " " + trendText;
    }

    private long startOfDay(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void navigateToLogin() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openHomeLoginState();
        }
    }

    private static final class InsightData {
        final List<DayBucket> buckets;
        final double totalCalories;
        final String peakLabel;
        final int goalDays;
        final double goalCalories;

        InsightData(List<DayBucket> buckets, double totalCalories, String peakLabel, int goalDays, double goalCalories) {
            this.buckets = buckets;
            this.totalCalories = totalCalories;
            this.peakLabel = peakLabel;
            this.goalDays = goalDays;
            this.goalCalories = goalCalories;
        }
    }

    private static final class DayBucket {
        final long dayKey;
        final double calories;
        final String label;

        DayBucket(long dayKey, double calories) {
            this.dayKey = dayKey;
            this.calories = calories;
            this.label = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date(dayKey));
        }
    }
}
