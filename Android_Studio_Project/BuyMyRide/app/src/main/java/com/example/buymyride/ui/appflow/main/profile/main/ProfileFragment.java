package com.example.buymyride.ui.appflow.main.profile.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.buymyride.databinding.FragmentProfileBinding;
import com.example.buymyride.ui.appflow.auth.AuthActivity;
import com.google.android.material.snackbar.Snackbar;
import com.example.buymyride.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private FragmentProfileBinding binding;
    private NavController navController; // Declare NavController


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        navController = Navigation.findNavController(view); // Initialize NavController

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

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.buttonSignOut.setEnabled(!isLoading);
            binding.buttonEditProfile.setEnabled(!isLoading);
        });

        viewModel.getNavigateEvent().observe(getViewLifecycleOwner(), event -> {
            ProfileNavigationDestination destination = event.getContentIfNotHandled();
            if (destination == null) return;

            switch (destination) {
                case EDIT_PROFILE -> navController.navigate(R.id.action_profileFragment_to_editProfileFragment);
                case AUTH -> {
                    Intent intent = new Intent(requireContext(), AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                }
            }
        });
    }

    private void setListeners() {
        binding.buttonSignOut.setOnClickListener(v -> {
            viewModel.signOut();
        });

        binding.buttonEditProfile.setOnClickListener(v -> {
            viewModel.navigateToEditProfile();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}