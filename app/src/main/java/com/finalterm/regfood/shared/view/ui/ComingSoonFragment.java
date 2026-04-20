package com.finalterm.regfood.shared.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.R;

public class ComingSoonFragment extends Fragment {

    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_SUBTITLE = "arg_subtitle";

    public static ComingSoonFragment newInstance(String title, String subtitle) {
        ComingSoonFragment fragment = new ComingSoonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SUBTITLE, subtitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coming_soon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String title = args != null ? args.getString(ARG_TITLE, getString(R.string.app_name)) : getString(R.string.app_name);
        String subtitle = args != null ? args.getString(ARG_SUBTITLE, getString(R.string.coming_soon_generic)) : getString(R.string.coming_soon_generic);

        TextView titleView = view.findViewById(R.id.tvComingSoonTitle);
        TextView subtitleView = view.findViewById(R.id.tvComingSoonSubtitle);

        titleView.setText(title);
        subtitleView.setText(subtitle);
    }
}
