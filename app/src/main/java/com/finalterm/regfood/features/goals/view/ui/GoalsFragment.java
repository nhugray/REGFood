package com.finalterm.regfood.features.goals.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.MainActivity;
import com.finalterm.regfood.R;
import com.finalterm.regfood.shared.session.UserSession;

public class GoalsFragment extends Fragment {

    private View guestGateBar;
    private View guestPreviewRoot;
    private View memberInteractiveRoot;
    private TextView tvGoalCalories;
    private RadioGroup rgGoalType;
    private CheckBox cbGoalProtein;
    private CheckBox cbGoalWorkout;

    private int goalCalories = 1650;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        guestGateBar = view.findViewById(R.id.goalsGuestGateBar);
        guestPreviewRoot = view.findViewById(R.id.goalsGuestPreviewRoot);
        memberInteractiveRoot = view.findViewById(R.id.goalsMemberInteractiveRoot);
        tvGoalCalories = view.findViewById(R.id.tvGoalCalories);
        rgGoalType = view.findViewById(R.id.rgGoalType);
        cbGoalProtein = view.findViewById(R.id.cbGoalProtein);
        cbGoalWorkout = view.findViewById(R.id.cbGoalWorkout);

        view.findViewById(R.id.btnGoalsLogin).setOnClickListener(v -> navigateToLogin());
        view.findViewById(R.id.btnGoalCaloriesPlus).setOnClickListener(v -> adjustCalories(50));
        view.findViewById(R.id.btnGoalCaloriesMinus).setOnClickListener(v -> adjustCalories(-50));
        view.findViewById(R.id.btnSaveGoals).setOnClickListener(v -> saveGoals());

        RadioButton rbMaintain = view.findViewById(R.id.rbGoalMaintain);
        rbMaintain.setChecked(true);

        updateCaloriesUi();
        renderAccessState();
    }

    private void renderAccessState() {
        boolean isGuest = UserSession.isGuest();
        guestGateBar.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        guestPreviewRoot.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        memberInteractiveRoot.setVisibility(isGuest ? View.GONE : View.VISIBLE);
    }

    private void adjustCalories(int delta) {
        goalCalories = Math.max(1200, Math.min(3500, goalCalories + delta));
        updateCaloriesUi();
    }

    private void updateCaloriesUi() {
        tvGoalCalories.setText(getString(R.string.goals_target_calories_format, goalCalories));
    }

    private void saveGoals() {
        int selectedGoalId = rgGoalType.getCheckedRadioButtonId();
        String goalType = getString(R.string.goals_type_maintain);
        if (selectedGoalId == R.id.rbGoalLose) {
            goalType = getString(R.string.goals_type_lose);
        } else if (selectedGoalId == R.id.rbGoalGain) {
            goalType = getString(R.string.goals_type_gain);
        }

        String summary = getString(
                R.string.goals_saved_summary,
                goalType,
                goalCalories,
                cbGoalProtein.isChecked() ? getString(R.string.common_on) : getString(R.string.common_off),
                cbGoalWorkout.isChecked() ? getString(R.string.common_on) : getString(R.string.common_off)
        );
        Toast.makeText(requireContext(), summary, Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openHomeLoginState();
        }
    }
}
