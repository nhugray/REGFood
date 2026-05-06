package com.finalterm.regfood.features.insights.view.ui;

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
import com.finalterm.regfood.shared.session.UserSession;

public class InsightsFragment extends Fragment {

    private View guestGateBar;
    private View guestPreviewRoot;
    private View memberInteractiveRoot;
    private CheckBox cbCalories;
    private CheckBox cbProtein;
    private CheckBox cbWater;
    private TextView tvHabitResult;

    private String selectedPeriod;

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
        cbCalories = view.findViewById(R.id.cbInsightsCalories);
        cbProtein = view.findViewById(R.id.cbInsightsProtein);
        cbWater = view.findViewById(R.id.cbInsightsWater);
        tvHabitResult = view.findViewById(R.id.tvInsightsHabitResult);

        selectedPeriod = getString(R.string.insights_period_week);

        view.findViewById(R.id.btnInsightsLogin).setOnClickListener(v -> navigateToLogin());
        view.findViewById(R.id.btnInsightsWeek).setOnClickListener(v -> selectPeriod(true));
        view.findViewById(R.id.btnInsightsMonth).setOnClickListener(v -> selectPeriod(false));
        view.findViewById(R.id.btnGenerateInsights).setOnClickListener(v -> generateInsights());

        renderAccessState();
    }

    private void renderAccessState() {
        boolean isGuest = UserSession.isGuest();
        guestGateBar.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        guestPreviewRoot.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        memberInteractiveRoot.setVisibility(isGuest ? View.GONE : View.VISIBLE);
    }

    private void selectPeriod(boolean isWeek) {
        selectedPeriod = getString(isWeek ? R.string.insights_period_week : R.string.insights_period_month);
        Toast.makeText(requireContext(), getString(R.string.insights_period_selected, selectedPeriod), Toast.LENGTH_SHORT).show();
    }

    private void generateInsights() {
        String metrics = buildSelectedMetrics();
        if (metrics.isEmpty()) {
            Toast.makeText(requireContext(), R.string.insights_select_metric_warning, Toast.LENGTH_SHORT).show();
            return;
        }

        tvHabitResult.setText(getString(R.string.insights_generated_summary, selectedPeriod, metrics));
    }

    private String buildSelectedMetrics() {
        StringBuilder builder = new StringBuilder();
        if (cbCalories.isChecked()) {
            builder.append(getString(R.string.insights_filter_calories));
        }
        if (cbProtein.isChecked()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(getString(R.string.insights_filter_protein));
        }
        if (cbWater.isChecked()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(getString(R.string.insights_filter_water));
        }
        return builder.toString();
    }

    private void navigateToLogin() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openHomeLoginState();
        }
    }
}
