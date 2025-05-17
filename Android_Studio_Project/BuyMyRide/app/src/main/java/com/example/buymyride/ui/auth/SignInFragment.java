package com.example.buymyride.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import androidx.fragment.app.Fragment;


import com.example.buymyride.databinding.FragmentSignInBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInFragment extends Fragment {

    private FragmentSignInBinding binding;
    private NavController navController;
    @Inject
    SignInViewModel viewModel;

    public SignInFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        navController = Navigation.findNavController(view);
//
//        setupClickListeners();
//        observeViewModel();
//    }
//
//    private void setupClickListeners() {
//        binding.buttonSignIn.setOnClickListener(v -> {
//            String email = binding.inputEmail.getText().toString().trim();
//            String password = binding.inputPassword.getText().toString().trim();
//            viewModel.signIn(email, password);
//        });
//
//        binding.textRegister.setOnClickListener(v ->
//                navController.navigate(R.id.action_signInFragment_to_signUpFragment));
//
//        binding.layoutForgotPassword.textResetPassword.setOnClickListener(v -> {
//            navController.navigate(R.id.action_signInFragment_to_forgotPasswordFragment);
//        });
//    }
//
//
//    private void observeViewModel() {
//        viewModel.getSignInResult().observe(getViewLifecycleOwner(), result -> {
//            if (result.isSuccessful()) {
//                Toast.makeText(getContext(), "Sign in successful. User: " + result.getUser().getEmail(), Toast.LENGTH_SHORT).show();
//                navController.navigate(R.id.action_signInFragment_to_catalogFragment);
//                requireActivity().finish();
//            } else if (result.getException() != null) {
//                Toast.makeText(getContext(), "Authentication failed: " + result.getException().getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}