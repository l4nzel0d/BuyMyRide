package com.example.buymyride.ui.splash;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.ui.auth.AuthActivity;
import com.example.buymyride.ui.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SplashViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Class<?>> navigationEvent = new MutableLiveData<>();

    public LiveData<Class<?>> getNavigationEvent() {
        return navigationEvent;
    }

    @Inject
    public SplashViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = authRepository.getCurrentUser();
            if (currentUser != null) {
                navigationEvent.postValue(MainActivity.class);
            } else {
                navigationEvent.postValue(AuthActivity.class);
            }
        }, 2000);
    }
}
