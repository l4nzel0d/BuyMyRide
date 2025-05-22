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
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final MutableLiveData<OneTimeEvent<SignInNavigationDestination>> navigateEvent = new MutableLiveData<>();
    public LiveData<OneTimeEvent<SignInNavigationDestination>> getNavigateEvent() {
        return navigateEvent;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
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
                    navigateEvent.postValue(new OneTimeEvent<>(SignInNavigationDestination.MAIN));
                })
                .exceptionally(throwable -> {
                    isLoading.setValue(false);
                    errorMessage.setValue(throwable.getMessage());
                    return null; // Return null because exceptionally expects a return value
                });
    }

    public void navigateToSignUp() {
        navigateEvent.setValue(new OneTimeEvent<>(SignInNavigationDestination.SIGN_UP));
    }

    public void navigateToForgotPassword() {
        navigateEvent.setValue(new OneTimeEvent<>(SignInNavigationDestination.FORGOT_PASSWORD));
    }
}
