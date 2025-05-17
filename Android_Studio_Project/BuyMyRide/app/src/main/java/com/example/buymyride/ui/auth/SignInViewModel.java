package com.example.buymyride.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.repositories.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

class SignInResult {
    private FirebaseUser user;
    private Exception exception;

    public SignInResult(FirebaseUser user) {
        this.user = user;
    }

    public SignInResult(Exception exception) {
        this.exception = exception;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isSuccessful() {
        return user != null;
    }
}

@HiltViewModel
public class SignInViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<SignInResult> signInResult = new MutableLiveData<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public SignInViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<SignInResult> getSignInResult() {
        return signInResult;
    }

    public void signIn(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            signInResult.setValue(new SignInResult(new IllegalArgumentException("Email and password cannot be empty")));
            return;
        }

        executor.execute(() -> {
            try {
                FirebaseUser user = authRepository.signIn(email, password);
                signInResult.postValue(new SignInResult(user));
            } catch (Exception e) {
                signInResult.postValue(new SignInResult(e));
            }
        });
    }
}
