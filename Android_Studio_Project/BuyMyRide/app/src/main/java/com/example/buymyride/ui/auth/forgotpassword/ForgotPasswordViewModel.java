package com.example.buymyride.ui.auth.forgotpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.util.OneTimeEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ForgotPasswordViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<OneTimeEvent<Boolean>> navigateToSignIn = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isEmailSent = new MutableLiveData<>(false);

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<OneTimeEvent<Boolean>> getNavigateToSignIn() {
        return navigateToSignIn;
    }


    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsEmailSent() {
        return isEmailSent;
    }

    @Inject
    public ForgotPasswordViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void resetPassword(String email) {
        isLoading.setValue(true);
        authRepository.resetPassword(email)
                .thenAccept(result -> {
                    isLoading.postValue(false);
                    isEmailSent.postValue(true);
                    navigateToSignIn.postValue(new OneTimeEvent<>(true));
                })
                .exceptionally(throwable -> {
                    isLoading.postValue(false);
                    errorMessage.postValue(throwable.getMessage());
                    return null;
                });
    }

}
