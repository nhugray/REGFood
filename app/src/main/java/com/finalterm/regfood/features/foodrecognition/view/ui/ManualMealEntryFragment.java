package com.finalterm.regfood.features.foodrecognition.view.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.MainActivity;
import com.finalterm.regfood.R;
import com.finalterm.regfood.local.entity.FoodItemEntity;
import com.finalterm.regfood.local.entity.MealLogEntity;
import com.finalterm.regfood.local.repository.FoodCatalogSeeder;
import com.finalterm.regfood.local.repository.MealRepository;
import com.finalterm.regfood.shared.session.UserSession;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManualMealEntryFragment extends Fragment {

    private MealRepository mealRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MaterialAutoCompleteTextView actFoodPicker;
    private MaterialAutoCompleteTextView actPortion;
    private TextView tvSelectedFoodName;
    private TextView tvBaseCalories;
    private TextView tvFinalCalories;
    private RadioGroup rgMealType;
    private View toppingsContainer;
    private ImageView ivMealPreview;
    private final List<ToppingOption> toppingOptions = new ArrayList<>();
    private final List<FoodItemEntity> foods = new ArrayList<>();
    private FoodItemEntity selectedFood;
    private double finalCalories;
    private String currentImagePath;

    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private File pendingCaptureFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manual_meal_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mealRepository = new MealRepository(requireContext().getApplicationContext());
        new FoodCatalogSeeder(requireContext().getApplicationContext()).seedIfNeeded(this::loadFoods);
        actFoodPicker = view.findViewById(R.id.actFoodPicker);
        actPortion = view.findViewById(R.id.actPortion);
        tvSelectedFoodName = view.findViewById(R.id.tvSelectedFoodName);
        tvBaseCalories = view.findViewById(R.id.tvBaseCalories);
        tvFinalCalories = view.findViewById(R.id.tvFinalCalories);
        rgMealType = view.findViewById(R.id.rgMealType);
        toppingsContainer = view.findViewById(R.id.toppingsContainer);
        ivMealPreview = view.findViewById(R.id.ivMealPreview);

        registerLaunchers();

        view.findViewById(R.id.btnTakePhoto).setOnClickListener(v -> capturePhoto());
        view.findViewById(R.id.btnPickPhoto).setOnClickListener(v -> pickPhoto());
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        view.findViewById(R.id.btnSave).setOnClickListener(v -> saveMeal());

        setupPortionPicker();
        setupPortionPickerInteractions();
        setupToppings();
        setupMealType();
        setupFoodPickerInteractions();
        loadFoods();
        updateFinalCalories();
    }

    private void registerLaunchers() {
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (Boolean.TRUE.equals(success) && pendingCaptureFile != null && pendingCaptureFile.exists()) {
                        currentImagePath = pendingCaptureFile.getAbsolutePath();
                        showPreview(currentImagePath);
                    } else {
                        cleanupPendingCapture();
                    }
                }
        );

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        File copied = copyUriToCache(uri);
                        if (copied != null) {
                            currentImagePath = copied.getAbsolutePath();
                            showPreview(currentImagePath);
                        } else {
                            Toast.makeText(requireContext(), "Không đọc được ảnh từ thư viện", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void capturePhoto() {
        File imageFile = createImageFile();
        if (imageFile == null) {
            Toast.makeText(requireContext(), "Không tạo được file ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        pendingCaptureFile = imageFile;
        Uri pendingCaptureUri = androidx.core.content.FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                imageFile
        );
        takePictureLauncher.launch(pendingCaptureUri);
    }

    private void pickPhoto() {
        pickImageLauncher.launch("image/*");
    }

    private void showPreview(String path) {
        if (TextUtils.isEmpty(path)) return;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
            ivMealPreview.setImageBitmap(bitmap);
            ivMealPreview.setVisibility(View.VISIBLE);
        }
    }

    private File createImageFile() {
        try {
            File dir = new File(requireContext().getCacheDir(), "images");
            if (!dir.exists() && !dir.mkdirs()) {
                return null;
            }
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            return new File(dir, "meal_" + timestamp + ".jpg");
        } catch (Exception e) {
            return null;
        }
    }

    private File copyUriToCache(Uri sourceUri) {
        File target = createImageFile();
        if (target == null) return null;

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(sourceUri);
             FileOutputStream outputStream = new FileOutputStream(target)) {
            if (inputStream == null) return null;
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            return target;
        } catch (IOException e) {
            return null;
        }
    }

    private void cleanupPendingCapture() {
        if (pendingCaptureFile != null && pendingCaptureFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            pendingCaptureFile.delete();
        }
        pendingCaptureFile = null;
    }

    private void setupPortionPicker() {
        String[] portionLabels = new String[]{"Nhỏ (0.8x)", "Vừa (1.0x)", "Lớn (1.3x)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, portionLabels);
        actPortion.setAdapter(adapter);
        actPortion.setText(portionLabels[1], false);
        actPortion.setOnItemClickListener((parent, view, position, id) -> updateFinalCalories());
    }

    private void setupPortionPickerInteractions() {
        actPortion.setOnClickListener(v -> actPortion.showDropDown());
        actPortion.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actPortion.showDropDown();
            }
        });
    }

    private void setupMealType() {
        if (rgMealType.getCheckedRadioButtonId() == View.NO_ID) {
            rgMealType.check(R.id.rbMealBreakfast);
        }
        rgMealType.setOnCheckedChangeListener((group, checkedId) -> updateFinalCalories());
    }

    private void setupFoodPickerInteractions() {
        actFoodPicker.setOnClickListener(v -> actFoodPicker.showDropDown());
        actFoodPicker.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actFoodPicker.showDropDown();
            }
        });
    }

    private void setupToppings() {
        toppingOptions.clear();
        toppingOptions.add(new ToppingOption("Trứng", 90d));
        toppingOptions.add(new ToppingOption("Thịt", 120d));
        toppingOptions.add(new ToppingOption("Quẩy", 80d));
        toppingOptions.add(new ToppingOption("Chả", 60d));
        toppingOptions.add(new ToppingOption("Xúc xích", 110d));
        toppingOptions.add(new ToppingOption("Pate", 95d));
        toppingOptions.add(new ToppingOption("Phô mai", 75d));

        ((android.widget.LinearLayout) toppingsContainer).removeAllViews();
        for (ToppingOption option : toppingOptions) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(String.format(Locale.getDefault(), "%s (+%.0f kcal)", option.label, option.calories));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateFinalCalories());
            ((android.widget.LinearLayout) toppingsContainer).addView(checkBox);
            option.checkBox = checkBox;
        }
    }

    private void loadFoods() {
        executor.execute(() -> {
            List<FoodItemEntity> activeFoods = mealRepository.getActiveFoods();
            foods.clear();
            if (activeFoods != null) foods.addAll(activeFoods);

            List<String> names = new ArrayList<>();
            for (FoodItemEntity food : foods) {
                names.add(food.foodName);
            }

            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, names);
                actFoodPicker.setAdapter(adapter);
                actFoodPicker.setOnItemClickListener((parent, view, position, id) -> {
                    if (position >= 0 && position < foods.size()) {
                        selectedFood = foods.get(position);
                        tvSelectedFoodName.setText(selectedFood.foodName);
                        tvBaseCalories.setText(String.format(Locale.getDefault(), "Calories gốc: %.0f kcal", selectedFood.baseCalories));
                        updateFinalCalories();
                    }
                });
            });
        });
    }

    private void updateFinalCalories() {
        double base = selectedFood != null ? selectedFood.baseCalories : 0d;
        double portion = getSelectedPortionMultiplier();
        double toppingCalories = getSelectedToppingsCalories();
        finalCalories = (base * portion) + toppingCalories;
        tvFinalCalories.setText(String.format(Locale.getDefault(), "Tổng ước tính: %.0f kcal", finalCalories));
    }

    private double getSelectedPortionMultiplier() {
        String value = actPortion.getText() != null ? actPortion.getText().toString() : "";
        if (value.contains("0.8x")) return 0.8d;
        if (value.contains("1.3x")) return 1.3d;
        return 1.0d;
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

    private void saveMeal() {
        if (selectedFood == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn món ăn", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentImagePath == null || currentImagePath.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chụp ảnh hoặc chọn ảnh từ thư viện", Toast.LENGTH_SHORT).show();
            return;
        }
        if (UserSession.isGuest()) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rgMealType.getCheckedRadioButtonId() == View.NO_ID) {
            Toast.makeText(requireContext(), "Vui lòng chọn bữa ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        final long userId = Math.abs(UserSession.getCurrentEmail().hashCode());
        final long now = System.currentTimeMillis();
        final String mealType = getSelectedMealType();
        final String portionName = actPortion.getText() != null ? actPortion.getText().toString() : "Vừa (1.0x)";
        final double portionMultiplier = getSelectedPortionMultiplier();
        final String toppings = getSelectedToppingsLabel();
        final double toppingsCalories = getSelectedToppingsCalories();
        final double totalCalories = (selectedFood.baseCalories * portionMultiplier) + toppingsCalories;

        MealLogEntity mealLog = new MealLogEntity(
                userId,
                selectedFood.id,
                selectedFood.foodName,
                selectedFood.aiLabel,
                1.0d,
                portionName,
                portionMultiplier,
                toppings,
                totalCalories,
                0d,
                0d,
                0d,
                0d,
                mealType,
                now,
                currentImagePath,
                null,
                false,
                now
        );

        executor.execute(() -> {
            mealRepository.saveMeal(mealLog);
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Đã lưu bữa ăn", Toast.LENGTH_SHORT).show();
                if (requireActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) requireActivity();
                    mainActivity.refreshHomeTodayMetrics();
                    mainActivity.openJournalTab();
                }
            });
        });
    }

    private String getSelectedMealType() {
        int checkedId = rgMealType.getCheckedRadioButtonId();
        if (checkedId == R.id.rbMealLunch) return "lunch";
        if (checkedId == R.id.rbMealDinner) return "dinner";
        if (checkedId == R.id.rbMealNight) return "night";
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

    private static class ToppingOption {
        final String label;
        final double calories;
        CheckBox checkBox;

        ToppingOption(String label, double calories) {
            this.label = label;
            this.calories = calories;
        }
    }
}
