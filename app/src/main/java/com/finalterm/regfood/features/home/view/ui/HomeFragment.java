package com.finalterm.regfood.features.home.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.R;

public class HomeFragment extends Fragment {

    private View homeStateRoot;
    private View historyStateRoot;
    private View scanStateRoot;
    private View resultStateRoot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeStateRoot = view.findViewById(R.id.homeStateRoot);
        historyStateRoot = view.findViewById(R.id.historyStateRoot);
        scanStateRoot = view.findViewById(R.id.scanStateRoot);
        resultStateRoot = view.findViewById(R.id.resultStateRoot);

        view.findViewById(R.id.btnScanMeal).setOnClickListener(v -> showScanState());
        view.findViewById(R.id.btnManualLog).setOnClickListener(v -> showHistoryState());
        view.findViewById(R.id.btnBackFromHistory).setOnClickListener(v -> showHomeState());
        view.findViewById(R.id.btnCapturePhoto).setOnClickListener(v -> showResultState());
        view.findViewById(R.id.btnUploadPhoto).setOnClickListener(v -> showResultState());
        view.findViewById(R.id.btnBackHomeFromResultCard).setOnClickListener(v ->
            Toast.makeText(requireContext(), R.string.favorite_added_hint, Toast.LENGTH_SHORT).show()
        );
        view.findViewById(R.id.btnScanAnother).setOnClickListener(v -> showScanState());

        showHomeState();
    }

    public void resetToMainHome() {
        if (homeStateRoot != null) {
            showHomeState();
        }
    }

    private void showHomeState() {
        homeStateRoot.setVisibility(View.VISIBLE);
        historyStateRoot.setVisibility(View.GONE);
        scanStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.GONE);
    }

    private void showHistoryState() {
        homeStateRoot.setVisibility(View.GONE);
        historyStateRoot.setVisibility(View.VISIBLE);
        scanStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.GONE);
    }

    private void showScanState() {
        homeStateRoot.setVisibility(View.GONE);
        historyStateRoot.setVisibility(View.GONE);
        scanStateRoot.setVisibility(View.VISIBLE);
        resultStateRoot.setVisibility(View.GONE);
    }

    private void showResultState() {
        homeStateRoot.setVisibility(View.GONE);
        historyStateRoot.setVisibility(View.GONE);
        scanStateRoot.setVisibility(View.GONE);
        resultStateRoot.setVisibility(View.VISIBLE);
    }
}
