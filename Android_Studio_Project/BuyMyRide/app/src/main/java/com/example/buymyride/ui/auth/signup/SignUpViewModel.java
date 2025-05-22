package com.example.buymyride.ui.auth.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.models.MyUser;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;
import com.example.buymyride.util.OneTimeEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignUpViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MyUsersRepository myUsersRepository;
    private final MutableLiveData<OneTimeEvent<String>> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final MutableLiveData<OneTimeEvent<SignUpNavigationDestination>> navigateEvent = new MutableLiveData<>();
    public LiveData<OneTimeEvent<SignUpNavigationDestination>> getNavigateEvent() {
        return navigateEvent;
    }


    public LiveData<OneTimeEvent<String>> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @Inject
    public SignUpViewModel(AuthRepository authRepository, MyUsersRepository myUsersRepository) {
        this.authRepository = authRepository;
        this.myUsersRepository = myUsersRepository;
    }

    public void signUp(final String name, final String email, final String phoneNumber, final String password) {
        isLoading.setValue(true);

        authRepository.signUp(email, password)
                .thenCompose(userId -> {
                    MyUser myUser = new MyUser(userId, email, name, phoneNumber);
                    return myUsersRepository.saveUserData(myUser);
                })
                .thenAccept(aVoid -> {
                    isLoading.postValue(false);
                    navigateEvent.postValue(new OneTimeEvent<>(SignUpNavigationDestination.MAIN));
                })
                .exceptionally(throwable -> {
                    isLoading.postValue(false);
                    errorMessage.postValue(new OneTimeEvent<>(throwable.getMessage()));
                    return null;
                });
    }

    public void navigateToSignIn() {
        navigateEvent.setValue(new OneTimeEvent<>(SignUpNavigationDestination.SIGN_IN));
    }
}
