package com.finalterm.regfood.features.goals.view.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.MainActivity;
import com.finalterm.regfood.R;
import com.finalterm.regfood.local.entity.UserProfileEntity;
import com.finalterm.regfood.local.repository.UserProfileRepository;
import com.finalterm.regfood.shared.session.UserSession;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class GoalsFragment extends Fragment {

    private View guestGateBar;
    private View guestPreviewRoot;
    private View memberInteractiveRoot;
    private TextInputEditText etAge;
    private TextInputEditText etHeight;
    private TextInputEditText etWeight;
    private MaterialAutoCompleteTextView spGender;
    private MaterialAutoCompleteTextView spActivityLevel;
    private RadioGroup rgGoalType;
    private android.widget.TextView tvGoalCalories;
    private android.widget.TextView tvMacroPreview;
    private UserProfileRepository userProfileRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userProfileRepository = new UserProfileRepository(requireContext().getApplicationContext());

        guestGateBar = view.findViewById(R.id.goalsGuestGateBar);
        guestPreviewRoot = view.findViewById(R.id.goalsGuestPreviewRoot);
        memberInteractiveRoot = view.findViewById(R.id.goalsMemberInteractiveRoot);
        etAge = view.findViewById(R.id.etAge);
        etHeight = view.findViewById(R.id.etHeight);
        etWeight = view.findViewById(R.id.etWeight);
        spGender = view.findViewById(R.id.spGender);
        spActivityLevel = view.findViewById(R.id.spActivityLevel);
        rgGoalType = view.findViewById(R.id.rgGoalType);
        tvGoalCalories = view.findViewById(R.id.tvGoalCalories);
        tvMacroPreview = view.findViewById(R.id.tvMacroPreview);

        view.findViewById(R.id.btnGoalsLogin).setOnClickListener(v -> navigateToLogin());
        view.findViewById(R.id.btnSaveGoals).setOnClickListener(v -> saveProfile());

        setupDropdowns();
        ((RadioButton) view.findViewById(R.id.rbGoalMaintain)).setChecked(true);
        renderAccessState();
        loadExistingProfile();
        setupAutoPreviewListeners();
        updatePreview();
    }

    private void setupDropdowns() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                new String[]{"Nam", "Nữ", "Khác"});
        spGender.setAdapter(genderAdapter);
        spGender.setText("Nam", false);

        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                new String[]{"Ít vận động", "Vận động nhẹ", "Vận động vừa", "Vận động nhiều", "Rất năng động"});
        spActivityLevel.setAdapter(activityAdapter);
        spActivityLevel.setText("Ít vận động", false);
    }

    private void setupAutoPreviewListeners() {
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> updatePreview();
        etAge.setOnFocusChangeListener(focusChangeListener);
        etHeight.setOnFocusChangeListener(focusChangeListener);
        etWeight.setOnFocusChangeListener(focusChangeListener);
        rgGoalType.setOnCheckedChangeListener((group, checkedId) -> updatePreview());
    }

    private void renderAccessState() {
        boolean isGuest = UserSession.isGuest();
        guestGateBar.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        guestPreviewRoot.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        memberInteractiveRoot.setVisibility(isGuest ? View.GONE : View.VISIBLE);
    }

    private void loadExistingProfile() {
        if (UserSession.getCurrentUser() == null) return;
        userProfileRepository.getProfileByUid(UserSession.getCurrentUser().getUid(), profile -> {
            if (!isAdded() || profile == null) return;
            requireActivity().runOnUiThread(() -> {
                etAge.setText(String.valueOf(profile.age));
                etHeight.setText(String.valueOf(profile.heightCm));
                etWeight.setText(String.valueOf(profile.weightKg));
                spGender.setText(profile.gender, false);
                spActivityLevel.setText(profile.activityLevel, false);
                if ("Giảm cân".equals(profile.goalType)) {
                    ((RadioButton) requireView().findViewById(R.id.rbGoalLose)).setChecked(true);
                } else if ("Tăng cân".equals(profile.goalType)) {
                    ((RadioButton) requireView().findViewById(R.id.rbGoalGain)).setChecked(true);
                } else {
                    ((RadioButton) requireView().findViewById(R.id.rbGoalMaintain)).setChecked(true);
                }
                updatePreview();
            });
        });
    }

    private void updatePreview() {
        double calories = calculateCalories();
        double protein = Math.max(0, calories * 0.25 / 4.0);
        double carbs = Math.max(0, calories * 0.45 / 4.0);
        double fat = Math.max(0, calories * 0.30 / 9.0);
        tvGoalCalories.setText(calories > 0 ? String.format("Calo/ngày: %.0f kcal", calories) : "Calo/ngày: --");
        tvMacroPreview.setText(calories > 0
                ? String.format("Protein %.0fg • Carb %.0fg • Fat %.0fg", protein, carbs, fat)
                : "Protein --g • Carb --g • Fat --g");
    }

    private double calculateCalories() {
        int age = parseInt(textOf(etAge), 0);
        double height = parseDouble(textOf(etHeight), 0);
        double weight = parseDouble(textOf(etWeight), 0);
        String gender = textOf(spGender);
        String activity = textOf(spActivityLevel);
        String goalType = getGoalType();

        if (age <= 0 || height <= 0 || weight <= 0) {
            return 0;
        }

        double bmr = "Nữ".equalsIgnoreCase(gender)
                ? 10 * weight + 6.25 * height - 5 * age - 161
                : 10 * weight + 6.25 * height - 5 * age + 5;

        double tdee = bmr * getActivityMultiplier(activity);
        if ("Giảm cân".equals(goalType)) return Math.max(0, tdee - 350);
        if ("Tăng cân".equals(goalType)) return tdee + 300;
        return tdee;
    }

    private double getActivityMultiplier(String activity) {
        if ("Vận động nhẹ".equals(activity)) return 1.375;
        if ("Vận động vừa".equals(activity)) return 1.55;
        if ("Vận động nhiều".equals(activity)) return 1.725;
        if ("Rất năng động".equals(activity)) return 1.9;
        return 1.2;
    }

    private String getGoalType() {
        int selectedGoalId = rgGoalType.getCheckedRadioButtonId();
        if (selectedGoalId == R.id.rbGoalLose) return "Giảm cân";
        if (selectedGoalId == R.id.rbGoalGain) return "Tăng cân";
        return "Giữ cân";
    }

    private void saveProfile() {
        if (UserSession.getCurrentUser() == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String ageText = textOf(etAge);
        String heightText = textOf(etHeight);
        String weightText = textOf(etWeight);
        String gender = textOf(spGender);
        String activity = textOf(spActivityLevel);
        String goalType = getGoalType();

        if (TextUtils.isEmpty(ageText) || TextUtils.isEmpty(heightText) || TextUtils.isEmpty(weightText)
                || TextUtils.isEmpty(gender) || TextUtils.isEmpty(activity)) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin hồ sơ", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = parseInt(ageText, -1);
        float height = (float) parseDouble(heightText, -1);
        float weight = (float) parseDouble(weightText, -1);
        if (age <= 0 || height <= 0 || weight <= 0) {
            Toast.makeText(requireContext(), "Giá trị nhập không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        double calories = calculateCalories();
        if (calories <= 0) {
            Toast.makeText(requireContext(), "Không thể tính calo từ dữ liệu hiện tại", Toast.LENGTH_SHORT).show();
            return;
        }

        double protein = calories * 0.25 / 4.0;
        double carbs = calories * 0.45 / 4.0;
        double fat = calories * 0.30 / 9.0;
        long now = System.currentTimeMillis();

        UserProfileEntity profile = new UserProfileEntity(
                0,
                UserSession.getCurrentUser().getUid(),
                UserSession.getDisplayName(),
                UserSession.getCurrentEmail(),
                gender,
                age,
                height,
                weight,
                activity,
                goalType,
                calories,
                protein,
                carbs,
                fat,
                now,
                now
        );

        userProfileRepository.saveProfile(profile, new UserProfileRepository.ResultCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Đã lưu hồ sơ sức khỏe", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(Exception error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Lưu thất bại", Toast.LENGTH_SHORT).show());
            }
        });
        updatePreview();
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private String textOf(MaterialAutoCompleteTextView view) {
        return view.getText() != null ? view.getText().toString().trim() : "";
    }

    private int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    private double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return fallback;
        }
    }

    private void navigateToLogin() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openHomeLoginState();
        }
    }
}