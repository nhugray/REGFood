package com.finalterm.regfood.features.journal.view.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.regfood.MainActivity;
import com.finalterm.regfood.R;
import com.finalterm.regfood.shared.session.UserSession;

public class JournalFragment extends Fragment {

    private View guestGateBar;
    private View guestPreviewRoot;
    private View memberInteractiveRoot;
    private TextView tvCompletion;
    private TextView tvWaterMl;
    private CheckBox cbBreakfast;
    private CheckBox cbLunch;
    private CheckBox cbDinner;

    private int currentWaterMl = 1500;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        guestGateBar = view.findViewById(R.id.journalGuestGateBar);
        guestPreviewRoot = view.findViewById(R.id.journalGuestPreviewRoot);
        memberInteractiveRoot = view.findViewById(R.id.journalMemberInteractiveRoot);
        tvCompletion = view.findViewById(R.id.tvJournalCompletion);
        tvWaterMl = view.findViewById(R.id.tvJournalWaterMl);
        cbBreakfast = view.findViewById(R.id.cbBreakfastDone);
        cbLunch = view.findViewById(R.id.cbLunchDone);
        cbDinner = view.findViewById(R.id.cbDinnerDone);

        view.findViewById(R.id.btnJournalLogin).setOnClickListener(v -> navigateToLogin());
        view.findViewById(R.id.btnWaterPlus).setOnClickListener(v -> adjustWater(250));
        view.findViewById(R.id.btnWaterMinus).setOnClickListener(v -> adjustWater(-250));
        view.findViewById(R.id.btnJournalSave).setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.journal_saved_success, Toast.LENGTH_SHORT).show()
        );

        View.OnClickListener updateListener = v -> updateCompletionStatus();
        cbBreakfast.setOnClickListener(updateListener);
        cbLunch.setOnClickListener(updateListener);
        cbDinner.setOnClickListener(updateListener);

        updateWaterUi();
        updateCompletionStatus();
        renderAccessState();
    }

    private void renderAccessState() {
        boolean isGuest = UserSession.isGuest();
        guestGateBar.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        guestPreviewRoot.setVisibility(isGuest ? View.VISIBLE : View.GONE);
        memberInteractiveRoot.setVisibility(isGuest ? View.GONE : View.VISIBLE);
    }

    private void adjustWater(int delta) {
        currentWaterMl = Math.max(0, Math.min(4000, currentWaterMl + delta));
        updateWaterUi();
    }

    private void updateWaterUi() {
        if (tvWaterMl == null) {
            return;
        }
        tvWaterMl.setText(getString(R.string.journal_water_ml_format, currentWaterMl));
    }

    private void updateCompletionStatus() {
        int completed = 0;
        if (cbBreakfast.isChecked()) {
            completed++;
        }
        if (cbLunch.isChecked()) {
            completed++;
        }
        if (cbDinner.isChecked()) {
            completed++;
        }
        tvCompletion.setText(getString(R.string.journal_completion_format, completed, 3));
    }

    private void navigateToLogin() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openHomeLoginState();
        }
    }
}
