package com.example.buymyride.ui.main.profile;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.models.UserId;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MyUsersRepository myUsersRepository;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<String> _name = new MutableLiveData<>();
    private final MutableLiveData<String> _email = new MutableLiveData<>();
    private final MutableLiveData<String> _phone = new MutableLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> _navigateToAuth = new MutableLiveData<>();

    @Inject
    public ProfileViewModel(AuthRepository authRepository, MyUsersRepository myUsersRepository) {
        this.authRepository = authRepository;
        this.myUsersRepository = myUsersRepository;
        loadCurrentUser();
    }

    public LiveData<String> getName() {
        return _name;
    }

    public LiveData<String> getEmail() {
        return _email;
    }

    public LiveData<String> getPhone() {
        return _phone;
    }

    public LiveData<String> getErrorMessage() {
        return _errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    public LiveData<Boolean> getNavigateToAuth() {
        return _navigateToAuth;
    }

    private void loadCurrentUser() {
        _isLoading.setValue(true);
        authRepository.getCurrentUserId().observeForever(userId -> {
            if (userId != null) {
                fetchUserData(userId);
            } else {
                mainThreadHandler.post(() -> {
                    _name.setValue(null);
                    _email.setValue(null);
                    _phone.setValue(null);
                    _isLoading.setValue(false);
                });
            }
        });
    }

    private void fetchUserData(UserId userId) {
        ioExecutor.execute(() -> {
            myUsersRepository.getUserData(userId)
                    .thenAccept(user -> {
                        mainThreadHandler.post(() -> {
                            if (user != null) {
                                _name.setValue(user.getName());
                                _email.setValue(user.getEmail());
                                _phone.setValue(user.getPhoneNumber());
                            } else {
                                _errorMessage.setValue("User data not found.");
                            }
                            _isLoading.setValue(false);
                        });
                    })
                    .exceptionally(throwable -> {
                        mainThreadHandler.post(() -> {
                            _errorMessage.setValue("Failed to load user data: " + throwable.getMessage());
                            _isLoading.setValue(false);
                        });
                        return null;
                    });
        });
    }

    public void signOut() {
        _isLoading.setValue(true);
        authRepository.signOut();
        _navigateToAuth.setValue(true);
        _isLoading.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        ioExecutor.shutdown();
    }
}
