package com.example.buymyride.ui.auth.signin;

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

import com.example.buymyride.R;
import com.example.buymyride.databinding.FragmentSignInBinding; // Import generated binding class
import com.example.buymyride.ui.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInFragment extends Fragment {

    SignInViewModel viewModel;
    private NavController navController;
    private FragmentSignInBinding binding; // Use View Binding

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        viewModel = new ViewModelProvider(this).get(SignInViewModel.class); // Get ViewModel instance

        setListeners();
        observeViewModel();
    }

    private void setListeners() {
        binding.buttonSignIn.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString().trim();
            String password = binding.inputPassword.getText().toString().trim();
            if (!email.isEmpty() && !password.isEmpty()) {
                viewModel.signIn(email, password);
            } else {
                Snackbar.make(binding.getRoot(), "Пожалуйста, введите email и пароль", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.textRegister.setOnClickListener(v -> {
            viewModel.navigateToSignUp();
        });

        binding.textResetPassword.setOnClickListener(v -> {
            viewModel.navigateToForgotPassword();
        });
    }

    private void observeViewModel() {
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getNavigateToSignUp().observe(getViewLifecycleOwner(), navigate -> {
            if (Boolean.TRUE.equals(navigate)) {
                navController.navigate(R.id.action_signInFragment_to_signUpFragment);
            }
        });

        viewModel.getNavigateToForgotPassword().observe(getViewLifecycleOwner(), navigate -> {
            if (Boolean.TRUE.equals(navigate)) {
                navController.navigate(R.id.action_signInFragment_to_forgotPassword);
            }
        });

        viewModel.getNavigateToMain().observe(getViewLifecycleOwner(), navigate -> {
            if (Boolean.TRUE.equals(navigate)) {
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // You might want to add a ProgressBar to your layout and control its visibility here
            binding.buttonSignIn.setEnabled(!isLoading);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Important for memory management
    }
}