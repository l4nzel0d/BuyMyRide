package com.example.buymyride.ui.appflow.auth.signup;

import android.content.Intent;
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
import com.example.buymyride.databinding.FragmentSignUpBinding;
import com.example.buymyride.ui.appflow.main.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpFragment extends Fragment {

    SignUpViewModel viewModel;
    private NavController navController;
    private FragmentSignUpBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        setListeners();
        observeViewModel();
    }

    private void setListeners() {
        binding.buttonSignUp.setOnClickListener(v -> {
            String name = binding.inputName.getText().toString().trim();
            String email = binding.inputEmail.getText().toString().trim();
            String phone = binding.inputPhone.getText().toString().trim();
            String password = binding.inputPassword.getText().toString().trim();

            if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !password.isEmpty()) {
                viewModel.signUp(name, email, phone, password);
            } else {
                Snackbar.make(binding.getRoot(), "Пожалуйста, заполните все поля", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.textSignIn.setOnClickListener(v -> {
            viewModel.navigateToSignIn();
        });
    }

    private void observeViewModel() {
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        viewModel.getNavigateEvent().observe(getViewLifecycleOwner(), event -> {
            SignUpNavigationDestination destination = event.getContentIfNotHandled();
            if (destination == null) return;

            switch (destination) {
                case SIGN_IN -> navController.navigate(R.id.action_signUpFragment_to_signInFragment);
                case MAIN -> {
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.buttonSignUp.setEnabled(!isLoading);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}