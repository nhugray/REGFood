package com.finalterm.regfood.app.navigation;

import com.finalterm.regfood.R;

public enum BottomTab {
    HOME(R.id.navHome),
    FOODS(R.id.navFoods),
    JOURNAL(R.id.navJournal),
    GOALS(R.id.navGoals),
    ACCOUNT(R.id.navAccount);

    public final int itemId;

    BottomTab(int itemId) {
        this.itemId = itemId;
    }
}
