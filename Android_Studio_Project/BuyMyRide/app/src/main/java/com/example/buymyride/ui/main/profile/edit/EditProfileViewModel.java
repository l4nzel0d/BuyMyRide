package com.example.buymyride.ui.main.profile.edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.models.MyUser;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


import com.example.buymyride.util.OneTimeEvent;

@HiltViewModel // Add HiltViewModel annotation
public class EditProfileViewModel extends ViewModel {

    private final MyUsersRepository myUsersRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<MyUser> userData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<OneTimeEvent<String>> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<OneTimeEvent<String>> successMessage = new MutableLiveData<>();
    private final MutableLiveData<OneTimeEvent<EditProfileNavigationDestination>> navigateEvent = new MutableLiveData<>();

    public LiveData<OneTimeEvent<EditProfileNavigationDestination>> getNavigateEvent() {
        return navigateEvent;
    }

    // Public getter methods for LiveData
    public LiveData<MyUser> getUserData() {
        return userData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<OneTimeEvent<String>> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<OneTimeEvent<String>> getSuccessMessage() {
        return successMessage;
    }

    @Inject
    public EditProfileViewModel(MyUsersRepository myUsersRepository, AuthRepository authRepository) {
        this.myUsersRepository = myUsersRepository;
        this.authRepository = authRepository;
        loadUserData();
    }

    private void loadUserData() {
        String userId = authRepository.getCurrentUserId().getValue(); // Get current user ID
        if (userId != null) {
            isLoading.setValue(true);
            myUsersRepository.getUserData(userId)
                    .thenAccept(user -> {
                        userData.postValue(user); // Use postValue for LiveData updates from background threads
                        isLoading.postValue(false);
                    })
                    .exceptionally(e -> {
                        errorMessage.postValue(new OneTimeEvent<>("Ошибка загрузки данных: " + e.getMessage()));
                        isLoading.postValue(false);
                        return null; // Return null to complete the future normally after handling the exception
                    });
        } else {
            errorMessage.setValue(new OneTimeEvent<>("Пользователь не авторизован."));
        }
    }

    public void saveChanges(String newName, String newPhoneNumber) {
        String userId = authRepository.getCurrentUserId().getValue();
        if (userId == null) {
            errorMessage.setValue(new OneTimeEvent<>("Ошибка: Пользователь не авторизован."));
            return;
        }

        if (newName == null || newName.trim().isEmpty()) {
            errorMessage.setValue(new OneTimeEvent<>("Имя не может быть пустым."));
            return;
        }
        if (newPhoneNumber == null || newPhoneNumber.trim().isEmpty()) {
            errorMessage.setValue(new OneTimeEvent<>("Номер телефона не может быть пустым."));
            return;
        }

        isLoading.setValue(true);

        myUsersRepository.updateUserProfile(userId, newName, newPhoneNumber)
                .thenAccept(aVoid -> {
                    isLoading.postValue(false);
                    successMessage.postValue(new OneTimeEvent<>("Данные успешно обновлены!"));
                    navigateEvent.postValue(new OneTimeEvent<>(EditProfileNavigationDestination.GO_BACK));
                })
                .exceptionally(e -> {
                    errorMessage.postValue(new OneTimeEvent<>("Ошибка сохранения: " + e.getMessage()));
                    isLoading.postValue(false);
                    return null;
                });
    }
}

