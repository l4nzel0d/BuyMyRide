package com.example.buymyride.ui.main.profile.main;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.models.MyUser;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;
import com.example.buymyride.util.OneTimeEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MyUsersRepository myUsersRepository;

    private final MutableLiveData<String> cachedName = new MutableLiveData<>("");
    private final MutableLiveData<String> cachedEmail = new MutableLiveData<>("");
    private final MutableLiveData<String> cachedPhone = new MutableLiveData<>("");

    private final MutableLiveData<OneTimeEvent<String>> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final MutableLiveData<OneTimeEvent<ProfileNavigationDestination>> navigateEvent = new MutableLiveData<>();
    private final LiveData<MyUser> currentUserData;

    private Observer<MyUser> userDataObserver;

    @Inject
    public ProfileViewModel(AuthRepository authRepository, MyUsersRepository myUsersRepository) {
        this.authRepository = authRepository;
        this.myUsersRepository = myUsersRepository;

        isLoading.setValue(true);

        currentUserData = Transformations.switchMap(authRepository.getLiveFirebaseUser(), firebaseUser -> {
            if (firebaseUser == null) {
                handleSignedOutState();
                return new MutableLiveData<>(null);
            } else {
                isLoading.setValue(true);
                return myUsersRepository.getUserDataLiveData(firebaseUser.getUid());
            }
        });


        observeUserDataStates();
    }

    private void observeUserDataStates() {
        userDataObserver = user -> {
            isLoading.setValue(false);

            if (user != null) {
                cachedName.setValue(user.name());
                cachedEmail.setValue(user.email());
                cachedPhone.setValue(user.phoneNumber());
            } else if (authRepository.getLiveFirebaseUser().getValue() != null) {
                errorMessage.setValue(new OneTimeEvent<>("User profile data not found or failed to load."));
            }
        };
        currentUserData.observeForever(userDataObserver);
    }


    public LiveData<String> getName() {
        return cachedName;
    }

    public LiveData<String> getEmail() {
        return cachedEmail;
    }

    public LiveData<String> getPhone() {
        return cachedPhone;
    }

    public LiveData<OneTimeEvent<String>> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<OneTimeEvent<ProfileNavigationDestination>> getNavigateEvent() {
        return navigateEvent;
    }

    public void signOut() {
        authRepository.signOut();
    }

    public void navigateToEditProfile() {
        navigateEvent.setValue(new OneTimeEvent<>(ProfileNavigationDestination.EDIT_PROFILE));
    }

    private void handleSignedOutState() {
        navigateEvent.postValue(new OneTimeEvent<>(ProfileNavigationDestination.AUTH));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (userDataObserver != null) {
            currentUserData.removeObserver(userDataObserver);
        }
    }
}
