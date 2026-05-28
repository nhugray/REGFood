package com.finalterm.regfood.features.foods.view.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.R;
import com.finalterm.regfood.local.entity.FoodItemEntity;
import com.finalterm.regfood.local.repository.FoodCatalogSeeder;
import com.finalterm.regfood.local.repository.MealRepository;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodsFragment extends Fragment {

    private View foodsListRoot;
    private View foodDetailRoot;
    private FrameLayout foodDetailHeader;
    private ImageView foodDetailImage;
    private TextView foodDetailTitle;
    private TextView foodDetailSubtitle;
    private TextView foodDetailDescription;
    private TextView foodDetailKcal;
    private TextView foodDetailEnergy;
    private LinearLayout foodsDynamicContainer;
    private MaterialAutoCompleteTextView actFoodSearch;
    private MealRepository mealRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final List<FoodItemEntity> foods = new ArrayList<>();
    private final List<FoodItemEntity> filteredFoods = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_foods, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        foodsListRoot = view.findViewById(R.id.foodsListRoot);
        foodDetailRoot = view.findViewById(R.id.foodDetailRoot);
        foodDetailHeader = view.findViewById(R.id.foodDetailHeader);
        foodDetailImage = view.findViewById(R.id.ivFoodDetailImage);
        foodDetailTitle = view.findViewById(R.id.tvFoodDetailTitle);
        foodDetailSubtitle = view.findViewById(R.id.tvFoodDetailSubtitle);
        foodDetailDescription = view.findViewById(R.id.tvFoodDetailDescription);
        foodDetailKcal = view.findViewById(R.id.tvFoodDetailKcal);
        foodDetailEnergy = view.findViewById(R.id.tvFoodDetailEnergy);
        foodsDynamicContainer = view.findViewById(R.id.foodsDynamicContainer);
        actFoodSearch = view.findViewById(R.id.actFoodSearch);
        mealRepository = new MealRepository(requireContext().getApplicationContext());

        view.findViewById(R.id.btnFoodsBack).setOnClickListener(v -> showFoodsList());
        setupSearch();
        loadFoodsFromDatabase();
    }

    public void resetToFoodsList() {
        if (foodsListRoot != null) {
            showFoodsList();
        }
    }

    private void setupSearch() {
        actFoodSearch.setOnClickListener(v -> actFoodSearch.showDropDown());
        actFoodSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actFoodSearch.showDropDown();
            }
        });
        actFoodSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                filterFoods(s != null ? s.toString() : "");
            }
        });
    }

    private void showFoodsList() {
        foodsListRoot.setVisibility(View.VISIBLE);
        foodDetailRoot.setVisibility(View.GONE);
    }

    private void loadFoodsFromDatabase() {
        new FoodCatalogSeeder(requireContext().getApplicationContext()).seedIfNeeded(() -> executorService.execute(() -> {
            List<FoodItemEntity> activeFoods = mealRepository.getActiveFoods();
            foods.clear();
            if (activeFoods != null) foods.addAll(activeFoods);
            filteredFoods.clear();
            filteredFoods.addAll(foods);
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                renderFoodsList();
                setupSearchSuggestions();
            });
        }));
    }

    private void setupSearchSuggestions() {
        List<String> suggestions = new ArrayList<>();
        for (FoodItemEntity food : foods) {
            suggestions.add(food.foodName);
        }
        actFoodSearch.setAdapter(new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, suggestions));
    }

    private void filterFoods(String query) {
        filteredFoods.clear();
        if (TextUtils.isEmpty(query)) {
            filteredFoods.addAll(foods);
        } else {
            String normalized = query.trim().toLowerCase(Locale.ROOT);
            boolean numericQuery = normalized.matches("\\d+");
            Integer maxCalories = numericQuery ? Integer.parseInt(normalized) : null;

            for (FoodItemEntity food : foods) {
                boolean matchName = food.foodName != null && food.foodName.toLowerCase(Locale.ROOT).contains(normalized);
                boolean matchCalories = maxCalories != null && food.baseCalories <= maxCalories;
                if (matchName || matchCalories) {
                    filteredFoods.add(food);
                }
            }
        }
        renderFoodsList();
    }

    private void renderFoodsList() {
        foodsDynamicContainer.removeAllViews();
        if (filteredFoods.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("Không tìm thấy món phù hợp.");
            empty.setTextColor(requireContext().getColor(R.color.neutral_500));
            foodsDynamicContainer.addView(empty);
            return;
        }

        for (FoodItemEntity food : filteredFoods) {
            foodsDynamicContainer.addView(createFoodCard(food));
        }
    }

    private View createFoodCard(FoodItemEntity food) {
        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = (int) (12 * requireContext().getResources().getDisplayMetrics().density);
        card.setLayoutParams(params);
        card.setCardBackgroundColor(requireContext().getColor(R.color.white));
        card.setRadius(24f);
        card.setCardElevation(0f);
        card.setStrokeColor(requireContext().getColor(R.color.neutral_100));
        card.setStrokeWidth(1);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);

        ImageView imageBlock = new ImageView(requireContext());
        imageBlock.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (132 * requireContext().getResources().getDisplayMetrics().density)
        ));
        imageBlock.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageBlock.setBackgroundColor(requireContext().getColor(R.color.neutral_100));
        bindFoodImage(imageBlock, food);
        root.addView(imageBlock);

        LinearLayout content = new LinearLayout(requireContext());
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(14, 14, 14, 14);

        TextView title = new TextView(requireContext());
        title.setText(food.foodName);
        title.setTextColor(requireContext().getColor(R.color.neutral_900));
        title.setTextSize(16f);
        title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);

        TextView subtitle = new TextView(requireContext());
        subtitle.setText(!TextUtils.isEmpty(food.description) ? food.description : food.category);
        subtitle.setTextColor(requireContext().getColor(R.color.neutral_500));
        subtitle.setTextSize(13f);
        subtitle.setPadding(0, 6, 0, 0);

        TextView kcal = new TextView(requireContext());
        kcal.setText(String.format(Locale.getDefault(), "%.0f kcal", food.baseCalories));
        kcal.setTextColor(requireContext().getColor(R.color.green_700));
        kcal.setTextSize(14f);
        kcal.setTypeface(kcal.getTypeface(), android.graphics.Typeface.BOLD);
        kcal.setPadding(0, 8, 0, 0);

        content.addView(title);
        content.addView(subtitle);
        content.addView(kcal);
        root.addView(content);
        card.addView(root);

        card.setOnClickListener(v -> showFoodDetail(food));
        return card;
    }

    private void showFoodDetail(FoodItemEntity foodItem) {
        foodsListRoot.setVisibility(View.GONE);
        foodDetailRoot.setVisibility(View.VISIBLE);
        foodDetailHeader.setBackgroundColor(requireContext().getColor(R.color.neutral_100));
        bindFoodImage(foodDetailImage, foodItem);
        foodDetailTitle.setText(foodItem.foodName);
        foodDetailSubtitle.setText(foodItem.category);
        foodDetailDescription.setText(!TextUtils.isEmpty(foodItem.description) ? foodItem.description : "Món ăn từ database.");
        foodDetailKcal.setText(String.format(Locale.getDefault(), "%.0f kcal", foodItem.baseCalories));
        foodDetailEnergy.setText(!TextUtils.isEmpty(foodItem.defaultServingSize) ? foodItem.defaultServingSize : "1 phần");
    }

    private void bindFoodImage(ImageView imageView, FoodItemEntity food) {
        if (food == null || TextUtils.isEmpty(food.imageUrl)) {
            imageView.setImageResource(R.drawable.banh_beo);
            return;
        }

        String resourceName = food.imageUrl;
        if (resourceName.endsWith(".jpg")) {
            resourceName = resourceName.substring(0, resourceName.length() - 4);
        } else if (resourceName.endsWith(".png")) {
            resourceName = resourceName.substring(0, resourceName.length() - 4);
        }

        int resId = getResources().getIdentifier(resourceName, "drawable", requireContext().getPackageName());
        if (resId != 0) {
            imageView.setImageResource(resId);
        } else {
            imageView.setImageResource(R.drawable.banh_beo);
        }
    }
}
