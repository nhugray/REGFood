package com.finalterm.regfood.features.foods.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.R;
import com.finalterm.regfood.shared.session.UserSession;

public class FoodsFragment extends Fragment {

    private View foodsListRoot;
    private View foodDetailRoot;
    private FrameLayout foodDetailHeader;
    private TextView foodDetailTitle;
    private TextView foodDetailSubtitle;
    private TextView foodDetailDescription;
    private TextView foodDetailKcal;
    private TextView foodDetailEnergy;
    private TextView foodTag1;
    private TextView foodTag2;
    private TextView foodsDetailUserInitial;

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
        foodDetailTitle = view.findViewById(R.id.tvFoodDetailTitle);
        foodDetailSubtitle = view.findViewById(R.id.tvFoodDetailSubtitle);
        foodDetailDescription = view.findViewById(R.id.tvFoodDetailDescription);
        foodDetailKcal = view.findViewById(R.id.tvFoodDetailKcal);
        foodDetailEnergy = view.findViewById(R.id.tvFoodDetailEnergy);
        foodTag1 = view.findViewById(R.id.tvFoodTag1);
        foodTag2 = view.findViewById(R.id.tvFoodTag2);
        foodsDetailUserInitial = view.findViewById(R.id.tvFoodsDetailUserInitial);

        view.findViewById(R.id.cardBanhMi).setOnClickListener(v -> showFoodDetail(FoodEntry.BANH_MI));
        view.findViewById(R.id.cardMiQuang).setOnClickListener(v -> showFoodDetail(FoodEntry.MI_QUANG));
        view.findViewById(R.id.cardBunChaCa).setOnClickListener(v -> showFoodDetail(FoodEntry.BUN_CHA_CA));
        view.findViewById(R.id.btnRecognizeAgain).setOnClickListener(v ->
            Toast.makeText(requireContext(), R.string.favorite_added_hint, Toast.LENGTH_SHORT).show()
        );

        showFoodsList();
    }

    public void resetToFoodsList() {
        if (foodsListRoot != null) {
            showFoodsList();
        }
    }

    private void showFoodsList() {
        foodsListRoot.setVisibility(View.VISIBLE);
        foodDetailRoot.setVisibility(View.GONE);
    }

    private void showFoodDetail(FoodEntry entry) {
        foodsListRoot.setVisibility(View.GONE);
        foodDetailRoot.setVisibility(View.VISIBLE);

        foodDetailHeader.setBackgroundResource(entry.headerDrawable);
        foodDetailTitle.setText(entry.title);
        foodDetailSubtitle.setText(entry.subtitle);
        foodDetailDescription.setText(entry.description);
        foodDetailKcal.setText(entry.kcal);
        foodDetailEnergy.setText(entry.energy);
        foodTag1.setText(entry.tag1);
        foodTag2.setText(entry.tag2);
        foodsDetailUserInitial.setText(UserSession.isGuest() ? "G" : getEmailInitial(UserSession.getCurrentEmail()));
    }

    private String getEmailInitial(String email) {
        if (email == null || email.isEmpty()) {
            return "U";
        }
        return String.valueOf(Character.toUpperCase(email.charAt(0)));
    }

    private enum FoodEntry {
        BANH_MI(
                R.drawable.bg_food_banhmi,
                R.string.foods_item_1_short,
                R.string.foods_item_1_detail_subtitle,
                R.string.foods_item_1_detail_desc,
                R.string.foods_item_1_detail_kcal,
                R.string.foods_item_1_detail_energy,
                R.string.foods_item_1_tag1,
                R.string.foods_item_1_tag2
        ),
        MI_QUANG(
                R.drawable.bg_food_myquang,
                R.string.foods_item_2_short,
                R.string.foods_item_2_detail_subtitle,
                R.string.foods_item_2_detail_desc,
                R.string.foods_item_2_detail_kcal,
                R.string.foods_item_2_detail_energy,
                R.string.foods_item_2_tag1,
                R.string.foods_item_2_tag2
        ),
        BUN_CHA_CA(
                R.drawable.bg_food_bunchaca,
                R.string.foods_item_3_short,
                R.string.foods_item_3_detail_subtitle,
                R.string.foods_item_3_detail_desc,
                R.string.foods_item_3_detail_kcal,
                R.string.foods_item_3_detail_energy,
                R.string.foods_item_3_tag1,
                R.string.foods_item_3_tag2
        );

        final int headerDrawable;
        final int title;
        final int subtitle;
        final int description;
        final int kcal;
        final int energy;
        final int tag1;
        final int tag2;

        FoodEntry(int headerDrawable, int title, int subtitle, int description, int kcal, int energy, int tag1, int tag2) {
            this.headerDrawable = headerDrawable;
            this.title = title;
            this.subtitle = subtitle;
            this.description = description;
            this.kcal = kcal;
            this.energy = energy;
            this.tag1 = tag1;
            this.tag2 = tag2;
        }
    }
}
