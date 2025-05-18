package com.example.buymyride.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.model.UserId;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignUpViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToSignIn = new MutableLiveData<>();
    private MutableLiveData<Boolean> navigateToMain = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getNavigateToSignIn() {
        return navigateToSignIn;
    }

    public LiveData<Boolean> getNavigateToMain() {
        return navigateToMain;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Inject
    public SignUpViewModel(AuthRepository authRepository, UserRepository userRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    public void signUp(String name, String email, String phoneNumber, String password) {
        isLoading.setValue(true);
        authRepository.signUp(email, password)
                .thenAccept(result -> { // Changed from addOnCompleteListener
                    if (result.isSuccessful()) {
                        UserId userId = result.getData();  // Get UserId from result
                        // For now, not storing name and phone, just navigating
                        navigateToMain.setValue(true);
                        isLoading.setValue(false);

                    } else {
                        isLoading.setValue(false);
                        errorMessage.setValue("Registration failed: " + result.getException().getMessage());
                    }
                })
                .exceptionally(throwable -> {  // Handle exceptions
                    isLoading.setValue(false);
                    errorMessage.setValue("Registration failed: " + throwable.getMessage());
                    return null; // Return null because exceptionally expects a return value
                });
    }

    public void navigateToSignIn() {
        navigateToSignIn.setValue(true);
    }
}
