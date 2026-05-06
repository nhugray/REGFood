package com.finalterm.regfood;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finalterm.regfood.app.navigation.BottomTab;
import com.finalterm.regfood.features.foods.view.ui.FoodsFragment;
import com.finalterm.regfood.features.goals.view.ui.GoalsFragment;
import com.finalterm.regfood.features.home.view.ui.HomeFragment;
import com.finalterm.regfood.features.insights.view.ui.InsightsFragment;
import com.finalterm.regfood.features.journal.view.ui.JournalFragment;
import com.finalterm.regfood.shared.session.UserSession;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_HOME = "tag_home";
    private static final String TAG_FOODS = "tag_foods";
    private static final String TAG_JOURNAL = "tag_journal";
    private static final String TAG_GOALS = "tag_goals";
    private static final String TAG_INSIGHTS = "tag_insights";

    private BottomTab currentTab = BottomTab.HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomNavigation();
        if (savedInstanceState == null) {
            selectTab(BottomTab.HOME, true);
        }
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> selectTab(BottomTab.HOME, false));
        findViewById(R.id.navFoods).setOnClickListener(v -> selectTab(BottomTab.FOODS, false));
        findViewById(R.id.navJournal).setOnClickListener(v -> selectTab(BottomTab.JOURNAL, false));
        findViewById(R.id.navGoals).setOnClickListener(v -> selectTab(BottomTab.GOALS, false));
        findViewById(R.id.navInsights).setOnClickListener(v -> selectTab(BottomTab.INSIGHTS, false));
    }

    private void selectTab(BottomTab selectedTab, boolean forceRefresh) {
        if (!forceRefresh && selectedTab == currentTab) {
            resetCurrentTabView(selectedTab);
            return;
        }

        currentTab = selectedTab;
        updateBottomNavigationState(selectedTab);

        switch (selectedTab) {
            case HOME:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentHost, new HomeFragment(), TAG_HOME)
                        .commit();
                break;
            case FOODS:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentHost, new FoodsFragment(), TAG_FOODS)
                        .commit();
                break;
            case JOURNAL:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentHost, new JournalFragment(), TAG_JOURNAL)
                        .commit();
                break;
            case GOALS:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentHost, new GoalsFragment(), TAG_GOALS)
                        .commit();
                break;
            case INSIGHTS:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentHost, new InsightsFragment(), TAG_INSIGHTS)
                        .commit();
                break;
        }
    }

    public void openHomeLoginState() {
        UserSession.requestLoginPrompt();
        selectTab(BottomTab.HOME, true);
    }

    private void resetCurrentTabView(BottomTab selectedTab) {
        if (selectedTab == BottomTab.HOME) {
            HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
            if (homeFragment != null) {
                homeFragment.resetToMainHome();
            }
            return;
        }

        if (selectedTab == BottomTab.FOODS) {
            FoodsFragment foodsFragment = (FoodsFragment) getSupportFragmentManager().findFragmentByTag(TAG_FOODS);
            if (foodsFragment != null) {
                foodsFragment.resetToFoodsList();
            }
        }
    }

    private void updateBottomNavigationState(BottomTab selectedTab) {
        for (BottomTab tab : BottomTab.values()) {
            LinearLayout itemRoot = findViewById(tab.itemId);
            ImageView icon = itemRoot.findViewById(R.id.navIcon);
            TextView label = itemRoot.findViewById(R.id.navLabel);
            boolean isSelected = tab == selectedTab;

            itemRoot.setSelected(isSelected);
            itemRoot.setBackgroundResource(
                    isSelected ? R.drawable.bg_nav_item_selected : android.R.color.transparent
            );

            int iconColor = ContextCompat.getColor(
                    this,
                    isSelected ? R.color.green_700 : R.color.neutral_500
            );
            int textColor = ContextCompat.getColor(
                    this,
                    isSelected ? R.color.green_800 : R.color.neutral_500
            );

            icon.setColorFilter(iconColor);
            label.setTextColor(textColor);
        }
    }
}