package com.example.buymyride.ui.main.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.models.MyUser;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.util.Log; // Import Log for debugging

import com.example.buymyride.data.models.MyUser;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject; // For Hilt injection

import dagger.hilt.android.lifecycle.HiltViewModel; // Import HiltViewModel

@HiltViewModel // Add HiltViewModel annotation
public class EditProfileViewModel extends ViewModel {

    private static final String TAG = "EditProfileViewModel"; // Tag for logging

    private final MyUsersRepository myUsersRepository;
    private final AuthRepository authRepository;

    // Private MutableLiveData fields without underscore prefix
    private final MutableLiveData<MyUser> userData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaveSuccess = new MutableLiveData<>(false);

    // Public getter methods for LiveData
    public LiveData<MyUser> getUserData() {
        return userData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsSaveSuccess() {
        return isSaveSuccess;
    }

    // ViewModel constructor with @Inject for Hilt
    @Inject // Add Inject annotation
    public EditProfileViewModel(MyUsersRepository myUsersRepository, AuthRepository authRepository) {
        this.myUsersRepository = myUsersRepository;
        this.authRepository = authRepository;
        loadUserData(); // Load user data when ViewModel is created
    }

    // Method to load user data from the repository
    private void loadUserData() {
        String userId = authRepository.getCurrentUserId().getValue(); // Get current user ID
        if (userId != null) {
            Log.d(TAG, "loadUserData: Attempting to load data for userId: " + userId);
            isLoading.setValue(true);
            myUsersRepository.getUserData(userId)
                    .thenAccept(user -> {
                        Log.d(TAG, "loadUserData: User data loaded successfully: " + (user != null ? user.name() : "null"));
                        userData.postValue(user); // Use postValue for LiveData updates from background threads
                        isLoading.postValue(false);
                    })
                    .exceptionally(e -> {
                        Log.e(TAG, "loadUserData: Error loading data", e);
                        errorMessage.postValue("Ошибка загрузки данных: " + e.getMessage());
                        isLoading.postValue(false);
                        return null; // Return null to complete the future normally after handling the exception
                    });
        } else {
            Log.w(TAG, "loadUserData: User not authenticated.");
            errorMessage.setValue("Пользователь не авторизован.");
        }
    }

    /**
     * Saves the updated user profile (name and phone number) to the repository.
     *
     * @param newName        The new name to save.
     * @param newPhoneNumber The new phone number to save.
     */
    public void saveChanges(String newName, String newPhoneNumber) {
        Log.d(TAG, "saveChanges: Called with newName=" + newName + ", newPhoneNumber=" + newPhoneNumber);

        String userId = authRepository.getCurrentUserId().getValue();
        if (userId == null) {
            Log.w(TAG, "saveChanges: User not authenticated, cannot save changes.");
            errorMessage.setValue("Ошибка: Пользователь не авторизован.");
            return;
        }

        // Clear previous error message to ensure re-emission if the same error occurs
        errorMessage.setValue(null);

        if (newName == null || newName.trim().isEmpty()) {
            Log.d(TAG, "saveChanges: Validation failed - Name is empty.");
            errorMessage.setValue("Имя не может быть пустым.");
            return;
        }
        if (newPhoneNumber == null || newPhoneNumber.trim().isEmpty()) {
            Log.d(TAG, "saveChanges: Validation failed - Phone number is empty.");
            errorMessage.setValue("Номер телефона не может быть пустым.");
            return;
        }

        isLoading.setValue(true);
        isSaveSuccess.setValue(false); // Reset success state
        Log.d(TAG, "saveChanges: Starting Firestore update for userId: " + userId);

        myUsersRepository.updateUserProfile(userId, newName, newPhoneNumber)
                .thenAccept(aVoid -> {
                    Log.d(TAG, "saveChanges: Firestore update successful.");
                    isSaveSuccess.postValue(true); // Indicate success
                    isLoading.postValue(false);
                    // Optionally, reload user data to ensure UI is consistent with DB
                    // loadUserData();
                })
                .exceptionally(e -> {
                    Log.e(TAG, "saveChanges: Error during Firestore update", e);
                    errorMessage.postValue("Ошибка сохранения: " + e.getMessage());
                    isLoading.postValue(false);
                    isSaveSuccess.postValue(false); // Ensure success is false on error
                    return null;
                });
    }

    // Method to reset error message
    public void clearErrorMessage() {
        Log.d(TAG, "clearErrorMessage: Called.");
        errorMessage.setValue(null);
    }

    // Method to reset save success state
    public void resetSaveSuccess() {
        Log.d(TAG, "resetSaveSuccess: Called.");
        isSaveSuccess.setValue(false);
    }
}

