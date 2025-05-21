package com.example.buymyride.ui.auth.forgotpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.repositories.AuthRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ForgotPasswordViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToSignIn = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isEmailSent = new MutableLiveData<>(false);
    private Executor executor = Executors.newSingleThreadExecutor();

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getNavigateToSignIn() {
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
        executor.execute(() -> {
            authRepository.resetPassword(email)
                    .thenAccept(result -> {
                        isLoading.postValue(false);
                        if (result.isSuccessful()) {
                            isEmailSent.postValue(true);
                            navigateToSignIn.postValue(true);
                            //  Consider navigating back here or showing a dialog
                        } else {
                            errorMessage.postValue("Failed to send reset email: " + result.getException().getMessage());
                        }
                    })
                    .exceptionally(throwable -> {
                        isLoading.postValue(false);
                        errorMessage.postValue("Error: " + throwable.getMessage());
                        return null;
                    });
        });
    }

}
