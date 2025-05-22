package com.example.buymyride.ui.auth.signin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.util.OneTimeEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignInViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<OneTimeEvent<Boolean>> navigateToSignUp = new MutableLiveData<>();
    private MutableLiveData<OneTimeEvent<Boolean>> navigateToForgotPassword = new MutableLiveData<>();
    private MutableLiveData<OneTimeEvent<Boolean>> navigateToMain = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);


    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<OneTimeEvent<Boolean>> getNavigateToSignUp() {
        return navigateToSignUp;
    }

    public LiveData<OneTimeEvent<Boolean>> getNavigateToForgotPassword() {
        return navigateToForgotPassword;
    }

    public LiveData<OneTimeEvent<Boolean>> getNavigateToMain() {
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
                    navigateToMain.setValue(new OneTimeEvent<>(true));
                })
                .exceptionally(throwable -> {
                    isLoading.setValue(false);
                    errorMessage.setValue(throwable.getMessage());
                    return null; // Return null because exceptionally expects a return value
                });
    }

    public void navigateToSignUp() {
        navigateToSignUp.setValue(new OneTimeEvent<>(true));
    }

    public void navigateToForgotPassword() {
        navigateToForgotPassword.setValue(new OneTimeEvent<>(true));
    }
}
