package com.example.buymyride.ui.main.profile.edit;

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


import com.example.buymyride.databinding.FragmentEditProfileBinding;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private EditProfileViewModel viewModel;
    private NavController navController;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);

        setupToolbar();
        observeViewModel();
        setupListeners();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> {
            navController.navigateUp();
        });
    }

    private void observeViewModel() {

        // Observe user data to pre-fill fields
        viewModel.getUserData().observe(getViewLifecycleOwner(), myUser -> {
            if (myUser != null) {
                binding.inputName.setText(myUser.name()); // Changed to myUser.name()
                binding.inputPhone.setText(myUser.phoneNumber()); // Changed to myUser.phoneNumber()
            }
        });

        // Observe loading state to disable/enable button
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.buttonConfirmChanges.setEnabled(!isLoading);
        });

        // Observe error messages to display in Snackbar
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getNavigateEvent().observe(getViewLifecycleOwner(), event -> {
            EditProfileNavigationDestination destination = event.getContentIfNotHandled();
            if (destination == null) return;

            switch (destination) {
                case GO_BACK -> navController.navigateUp();
            }
        });
    }

    private void setupListeners() {
        binding.buttonConfirmChanges.setOnClickListener(v -> {
            String newName = binding.inputName.getText().toString().trim();
            String newPhoneNumber = binding.inputPhone.getText().toString().trim();
            viewModel.saveChanges(newName, newPhoneNumber);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
