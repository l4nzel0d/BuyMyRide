package com.example.buymyride.ui.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.buymyride.R;
import com.example.buymyride.databinding.FragmentProfileBinding;
import com.example.buymyride.ui.auth.AuthActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setListeners();
        observeViewModel();
    }

    private void setListeners() {
        binding.buttonSignOut.setOnClickListener(v -> {
            viewModel.signOut();
        });
    }

    private void observeViewModel() {
        viewModel.navigateToAuth.observe(getViewLifecycleOwner(), navigate -> {
            if (Boolean.TRUE.equals(navigate)) {
                startActivity(new Intent(requireContext(), AuthActivity.class));
                requireActivity().finish(); // Optional: Finish MainActivity if ProfileFragment is its only main screen
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
