package com.finalterm.regfood.features.foodrecognition.view.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.finalterm.regfood.R;
import com.finalterm.regfood.features.foodrecognition.data.model.FoodPredictionResponse;
import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.repository.MealRepository;
import com.finalterm.regfood.shared.session.UserSession;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfirmFoodBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_RESPONSE = "arg_response";
    private static final String ARG_IMAGE_PATH = "arg_image_path";

    private TextView tvFoodName;
    private TextView tvConfidence;
    private TextView tvSelectedFood;
    private TextView tvBaseCalories;
    private Spinner spinnerPortion;
    private LinearLayout toppingsContainer;
    private RadioGroup rgMealType;
    private TextView tvFinalCalories;
    private ImageView ivPreview;

    private FoodPredictionResponse response;
    private String imagePath;
    private final List<ToppingOption> toppingOptions = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private MealRepository mealRepository;

    public static ConfirmFoodBottomSheet newInstance(FoodPredictionResponse response, String imagePath) {
        ConfirmFoodBottomSheet sheet = new ConfirmFoodBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESPONSE, response);
        args.putString(ARG_IMAGE_PATH, imagePath);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mealRepository = new MealRepository(requireContext().getApplicationContext());
        if (getArguments() != null) {
            Serializable serializable = getArguments().getSerializable(ARG_RESPONSE);
            if (serializable instanceof FoodPredictionResponse) {
                response = (FoodPredictionResponse) serializable;
            }
            imagePath = getArguments().getString(ARG_IMAGE_PATH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_confirm_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvFoodName = view.findViewById(R.id.tvFoodName);
        tvConfidence = view.findViewById(R.id.tvConfidence);
        tvSelectedFood = view.findViewById(R.id.tvSelectedFood);
        tvBaseCalories = view.findViewById(R.id.tvBaseCalories);
        spinnerPortion = view.findViewById(R.id.spinnerPortion);
        toppingsContainer = view.findViewById(R.id.toppingsContainer);
        rgMealType = view.findViewById(R.id.rgMealType);
        tvFinalCalories = view.findViewById(R.id.tvFinalCalories);
        ivPreview = view.findViewById(R.id.ivPreview);
        if (rgMealType != null && rgMealType.getCheckedRadioButtonId() == View.NO_ID) {
            rgMealType.check(R.id.rbMealBreakfast);
        }

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        setupData();
        setupPortionSpinner();
        setupToppingOptions();
        updateFinalCalories();

        spinnerPortion.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                updateFinalCalories();
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveSelection());
    }

    private void setupData() {
        if (response == null || response.data == null) {
            return;
        }

        String foodName = !TextUtils.isEmpty(response.data.name) ? response.data.name : response.data.food;
        tvFoodName.setText(foodName);
        tvConfidence.setText(String.format(Locale.US, "Độ tin cậy: %.1f%%", response.data.confidence * 100));
        tvSelectedFood.setText(foodName);

        double baseCalories = FoodNutritionCalculator.getBaseCalories(foodName);
        tvBaseCalories.setText(String.format(Locale.US, "Calories gốc: %.0f kcal", baseCalories));

        if (imagePath != null) {
            ivPreview.setImageURI(android.net.Uri.parse("file://" + imagePath));
        }
    }

    private void setupPortionSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Nhỏ (0.8x)");
        items.add("Vừa (1.0x)");
        items.add("Lớn (1.3x)");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinnerPortion.setAdapter(adapter);
    }

    private void setupToppingOptions() {
        toppingOptions.clear();
        toppingOptions.add(new ToppingOption("Trứng", 90d));
        toppingOptions.add(new ToppingOption("Thịt", 120d));
        toppingOptions.add(new ToppingOption("Quẩy", 80d));
        toppingOptions.add(new ToppingOption("Chả", 60d));
        toppingOptions.add(new ToppingOption("Xúc xích", 110d));
        toppingOptions.add(new ToppingOption("Pate", 95d));
        toppingOptions.add(new ToppingOption("Phô mai", 75d));

        toppingsContainer.removeAllViews();
        for (ToppingOption option : toppingOptions) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(String.format(Locale.US, "%s (+%.0f kcal)", option.label, option.calories));
            checkBox.setTextSize(15f);
            checkBox.setPadding(0, 12, 0, 12);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateFinalCalories());
            toppingsContainer.addView(checkBox);
            option.checkBox = checkBox;
        }
    }

    private void updateFinalCalories() {
        if (response == null || response.data == null) {
            return;
        }

        String foodName = !TextUtils.isEmpty(response.data.name) ? response.data.name : response.data.food;
        double baseCalories = FoodNutritionCalculator.getBaseCalories(foodName);
        double portionMultiplier = FoodNutritionCalculator.getSelectedPortionMultiplier(spinnerPortion.getSelectedItemPosition());
        double toppingsCalories = getSelectedToppingsCalories();
        double finalCalories = (baseCalories * portionMultiplier) + toppingsCalories;
        tvFinalCalories.setText(String.format(Locale.US, "Tổng ước tính: %.0f kcal", finalCalories));
    }

    private String getSelectedPortionLabel() {
        Object selected = spinnerPortion.getSelectedItem();
        return selected != null ? selected.toString() : "Vừa (1.0x)";
    }

    private void saveSelection() {
        if (response == null || response.data == null) {
            Toast.makeText(requireContext(), "Không có dữ liệu để lưu", Toast.LENGTH_SHORT).show();
            return;
        }

        FragmentActivity activity = getActivity();
        if (!(activity instanceof FoodConfirmationListener) && !(getParentFragment() instanceof FoodConfirmationListener)) {
            Toast.makeText(requireContext(), "Không tìm thấy nơi nhận dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }

        String rawFoodName = !TextUtils.isEmpty(response.data.name) ? response.data.name : response.data.food;
        String foodName = FoodNutritionCalculator.getDisplayFoodName(rawFoodName);
        double portionMultiplier = FoodNutritionCalculator.getSelectedPortionMultiplier(spinnerPortion.getSelectedItemPosition());
        String portionName = getSelectedPortionLabel();
        String toppings = getSelectedToppingsLabel();
        String mealType = getSelectedMealType();
        double baseCalories = FoodNutritionCalculator.getBaseCalories(foodName);
        double toppingsCalories = getSelectedToppingsCalories();
        double finalCalories = (baseCalories * portionMultiplier) + toppingsCalories;

        if (rgMealType.getCheckedRadioButtonId() == View.NO_ID) {
            Toast.makeText(requireContext(), "Vui lòng chọn bữa ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = UserSession.isGuest() ? 0L : Math.abs(UserSession.getCurrentEmail().hashCode());
        String aiLabel = rawFoodName;
        double confidence = response.data.confidence;
        long now = System.currentTimeMillis();

        File storedImageFile = copyImageToAppStorage();
        String localImagePath = storedImageFile != null ? storedImageFile.getAbsolutePath() : imagePath;

        MealLogEntity mealLog = new MealLogEntity(
                userId,
                0L,
                foodName,
                aiLabel,
                confidence,
                portionName,
                portionMultiplier,
                toppings,
                finalCalories,
                0d,
                0d,
                0d,
                0d,
                mealType,
                now,
                localImagePath,
                null,
                false,
                now
        );

        executorService.execute(() -> {
            try {
                mealRepository.saveMeal(mealLog);
                requireActivity().runOnUiThread(() -> {
                    if (activity instanceof FoodConfirmationListener) {
                        ((FoodConfirmationListener) activity).onFoodConfirmed(foodName, portionMultiplier, toppings, finalCalories, response);
                    } else if (getParentFragment() instanceof FoodConfirmationListener) {
                        ((FoodConfirmationListener) getParentFragment()).onFoodConfirmed(foodName, portionMultiplier, toppings, finalCalories, response);
                    }
                    Toast.makeText(requireContext(), "Đã lưu món và ảnh trong máy", Toast.LENGTH_SHORT).show();
                    dismiss();
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Lưu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private File copyImageToAppStorage() {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        File source = new File(imagePath);
        if (!source.exists()) {
            return null;
        }

        try {
            File dir = new File(requireContext().getFilesDir(), "meal_images");
            if (!dir.exists() && !dir.mkdirs()) {
                return null;
            }

            File target = new File(dir, "meal_" + System.currentTimeMillis() + "_" + source.getName());
            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(android.net.Uri.fromFile(source));
                 FileOutputStream outputStream = new FileOutputStream(target)) {
                if (inputStream == null) {
                    return null;
                }

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            return target;
        } catch (Exception e) {
            return null;
        }
    }

    private String getSelectedMealType() {
        int checkedId = rgMealType.getCheckedRadioButtonId();
        if (checkedId == R.id.rbMealLunch) {
            return "lunch";
        }
        if (checkedId == R.id.rbMealDinner) {
            return "dinner";
        }
        if (checkedId == R.id.rbMealNight) {
            return "night";
        }
        return "breakfast";
    }

    private String getSelectedToppingsLabel() {
        List<String> selected = new ArrayList<>();
        for (ToppingOption option : toppingOptions) {
            if (option.checkBox != null && option.checkBox.isChecked()) {
                selected.add(option.label);
            }
        }
        return TextUtils.join(", ", selected);
    }

    private double getSelectedToppingsCalories() {
        double total = 0d;
        for (ToppingOption option : toppingOptions) {
            if (option.checkBox != null && option.checkBox.isChecked()) {
                total += option.calories;
            }
        }
        return total;
    }

    public interface FoodConfirmationListener {
        void onFoodConfirmed(String foodName, double portionMultiplier, String toppings, double finalCalories, FoodPredictionResponse response);
    }

    private static class ToppingOption {
        final String label;
        final double calories;
        CheckBox checkBox;

        ToppingOption(String label, double calories) {
            this.label = label;
            this.calories = calories;
        }
    }

    private abstract static class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
            onItemSelected(position);
        }

        @Override
        public void onNothingSelected(android.widget.AdapterView<?> parent) {
        }

        public abstract void onItemSelected(int position);
    }

}
