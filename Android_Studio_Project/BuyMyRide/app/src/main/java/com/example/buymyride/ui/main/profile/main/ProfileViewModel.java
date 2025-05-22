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


    private final LiveData<String> name;
    private final LiveData<String> email;
    private final LiveData<String> phone;

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
                navigateEvent.postValue(new OneTimeEvent<>(ProfileNavigationDestination.AUTH));
                errorMessage.setValue(null);
                isLoading.setValue(false);
                return new MutableLiveData<>(null);
            } else {
                isLoading.setValue(true);
                return myUsersRepository.getUserDataLiveData(firebaseUser.getUid());
            }
        });

        name = Transformations.map(currentUserData, user -> user != null ? user.name() : "");
        email = Transformations.map(currentUserData, user -> user != null ? user.email() : "");
        phone = Transformations.map(currentUserData, user -> user != null ? user.phoneNumber() : "");

        observeUserDataStates();
    }

    private void observeUserDataStates() {
        userDataObserver = user -> {
            isLoading.setValue(false);

            if (user == null && authRepository.getLiveFirebaseUser().getValue() != null) {
                errorMessage.setValue(new OneTimeEvent<>("User profile data not found or failed to load."));
            } else if (user != null) {
                errorMessage.setValue(null);
            }
        };
        currentUserData.observeForever(userDataObserver);
    }



    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPhone() {
        return phone;
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
        isLoading.setValue(true);
        authRepository.signOut();
    }

    public void navigateToEditProfile() {
        navigateEvent.setValue(new OneTimeEvent<>(ProfileNavigationDestination.EDIT_PROFILE));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (userDataObserver != null) {
            currentUserData.removeObserver(userDataObserver);
        }
    }
}
