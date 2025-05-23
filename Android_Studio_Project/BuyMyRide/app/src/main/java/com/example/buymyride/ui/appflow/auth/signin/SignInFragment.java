package com.example.buymyride.ui.appflow.auth.signin;

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
import com.example.buymyride.ui.appflow.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInFragment extends Fragment {

    SignInViewModel viewModel;
    private NavController navController;
    private FragmentSignInBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);

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
                Snackbar.make(binding.getRoot(), "Пожалуйста, введите email и пароль.", Snackbar.LENGTH_SHORT).show();
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
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.buttonSignIn.setEnabled(!isLoading);
        });

        viewModel.getNavigateEvent().observe(getViewLifecycleOwner(), event -> {
            SignInNavigationDestination destination = event.getContentIfNotHandled();
            if (destination == null) return;

            switch (destination) {
                case SIGN_UP -> navController.navigate(R.id.action_signInFragment_to_signUpFragment);
                case FORGOT_PASSWORD -> navController.navigate(R.id.action_signInFragment_to_forgotPassword);
                case MAIN -> {
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}