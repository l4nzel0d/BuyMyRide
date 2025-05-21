package com.example.buymyride.ui.auth.forgotpassword;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.buymyride.R;
import com.example.buymyride.databinding.FragmentForgotPasswordBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {

    private MaterialToolbar topAppBar;
    private NavController navController;
    private ForgotPasswordViewModel viewModel;
    private FragmentForgotPasswordBinding binding;


    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        topAppBar = view.findViewById(R.id.topAppBar);
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        topAppBar.setNavigationOnClickListener(v -> {
            navController.navigate(R.id.action_forgotPasswordFragment_to_signInFragment);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsEmailSent().observe(getViewLifecycleOwner(), isSent -> {
            if (isSent) {
                Snackbar.make(binding.getRoot(), "Password reset email sent. Check your inbox.", Snackbar.LENGTH_LONG)
                        .setAction("Sign In", v -> navController.navigate(R.id.action_forgotPasswordFragment_to_signInFragment))
                        .show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.buttonResetPassword.setEnabled(!isLoading);
        });

        binding.buttonResetPassword.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Snackbar.make(binding.getRoot(), "Please enter your email address", Snackbar.LENGTH_SHORT).show();
                return; // Stop execution if the email is empty
            }
            viewModel.resetPassword(email);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; //  avoid memory leak
    }
}