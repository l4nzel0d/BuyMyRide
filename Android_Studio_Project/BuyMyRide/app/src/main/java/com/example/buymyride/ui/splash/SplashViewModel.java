package com.example.buymyride.ui.splash;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.buymyride.ui.auth.AuthActivity;
import com.example.buymyride.ui.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SplashViewModel {
    private final FirebaseAuth mAuth;
    private final MutableLiveData<Class<?>> navigationEvent = new MutableLiveData<>();

    public LiveData<Class<?>> getNavigationEvent() {
        return navigationEvent;
    }

    @Inject
    public SplashViewModel(FirebaseAuth firebaseAuth) {
        this.mAuth = firebaseAuth;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                navigationEvent.postValue(MainActivity.class);
            } else {
                navigationEvent.postValue(AuthActivity.class);
            }
        }, 2000);
    }
}
