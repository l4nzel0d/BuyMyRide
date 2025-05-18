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

import com.example.buymyride.databinding.FragmentProfileBinding;
import com.example.buymyride.ui.auth.AuthActivity;
import com.google.android.material.snackbar.Snackbar;

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

        observeViewModel();
        setListeners();
    }

    private void observeViewModel() {
        viewModel.getName().observe(getViewLifecycleOwner(), name -> {
            binding.textName.setText(name != null ? name : "");
        });

        viewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            binding.textEmail.setText(email != null ? email : "");
        });

        viewModel.getPhone().observe(getViewLifecycleOwner(), phone -> {
            binding.textPhone.setText(phone != null ? phone : "");
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.buttonSignOut.setEnabled(!isLoading);
            // Optionally show a progress bar based on isLoading
        });

        viewModel.getNavigateToAuth().observe(getViewLifecycleOwner(), navigate -> {
            if (Boolean.TRUE.equals(navigate)) {
                startActivity(new Intent(requireContext(), AuthActivity.class));
                requireActivity().finish();
            }
        });
    }

    private void setListeners() {
        binding.buttonSignOut.setOnClickListener(v -> {
            viewModel.signOut();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}