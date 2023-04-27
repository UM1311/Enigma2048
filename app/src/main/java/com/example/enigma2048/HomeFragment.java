package com.example.enigma2048;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionInflater;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private RuntimeStateViewModel viewModel;
    private final boolean previous_game = false;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade_home));
        setExitTransition(inflater.inflateTransition(R.transition.fade_home));

        // This callback will only be called when the Fragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());
                dialogBuilder.setTitle("Are you sure you want to exit?");
                dialogBuilder.setMessage("Your progress will be lost.");
                dialogBuilder.setOnDismissListener((dialog) -> {});
                dialogBuilder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());
                dialogBuilder.setPositiveButton("Yes", (dialog, which) -> getActivity().finishAffinity());
                dialogBuilder.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(RuntimeStateViewModel.class);

        Button playButton = view.findViewById(R.id.play_button);
        Button newGameButton = view.findViewById(R.id.new_game_button);
        Button leaderboardsButton = view.findViewById(R.id.leaderboards_button);
        Button exitButton = view.findViewById(R.id.exit_button);

        Log.d("HomeFragment", "onViewCreated: " + viewModel.getValue().getPreviousGame());
        if (viewModel.getValue().getPreviousGame()) {
            playButton.setText("Continue");
            newGameButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setText("New Game");
            newGameButton.setVisibility(View.GONE);
        }

        playButton.setOnClickListener(this);
        newGameButton.setOnClickListener(this);
        leaderboardsButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        MaterialToolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setTitle(null);
        toolbar.setNavigationOnClickListener(null);
        toolbar.getMenu().findItem(R.id.action_settings).setVisible(true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.play_button && previous_game) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, PlayFragment.class, null)
                    .commit();
        } else if (id == R.id.new_game_button || id == R.id.play_button) {
            viewModel.resetValue();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, PlayFragment.class, null)
                    .commit();
        } else if (id == R.id.leaderboards_button) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, LeaderboardsFragment.class, null)
                    .commit();
        } else {
            getActivity().finishAffinity();
        }
    }
}