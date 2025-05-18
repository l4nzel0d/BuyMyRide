package com.example.buymyride.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.repositories.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignInViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToSignUp = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToForgotPassword = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToMain = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getNavigateToSignUp() {
        return navigateToSignUp;
    }

    public LiveData<Boolean> getNavigateToForgotPassword() {
        return navigateToForgotPassword;
    }

    public LiveData<Boolean> getNavigateToMain() {
        return navigateToMain;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Inject
    public SignInViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void signIn(String email, String password) {
        isLoading.setValue(true);
        authRepository.signIn(email, password)
                .thenAccept(result -> {
                    isLoading.setValue(false);
                    if (result.isSuccessful()) {
                        navigateToMain.setValue(true);
                    } else {
                        errorMessage.setValue("Sign-in failed: " + (result.getException() != null ? result.getException().getMessage() : "Unknown error"));
                    }
                })
                .exceptionally(throwable -> {
                    isLoading.setValue(false);
                    errorMessage.setValue("Sign-in failed: " + throwable.getMessage());
                    return null; // Return null because exceptionally expects a return value
                });
    }

    public void navigateToSignUp() {
        navigateToSignUp.setValue(true);
    }

    public void navigateToForgotPassword() {
        navigateToForgotPassword.setValue(true);
    }
}
