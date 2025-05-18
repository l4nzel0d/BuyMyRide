package com.example.buymyride.ui.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.ui.auth.AuthActivity;
import com.example.buymyride.ui.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SplashViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Class<? extends Activity>> destinationActivity = new MutableLiveData<>();

    public LiveData<Class<? extends Activity>> getDestinationActivity() {
        return destinationActivity;
    }

    @Inject
    public SplashViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
        decideNavigation();
    }


    private void decideNavigation() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser user = authRepository.getCurrentUser().getValue();
            if (user != null) {
                destinationActivity.postValue(MainActivity.class);
            } else {
                destinationActivity.postValue(AuthActivity.class);
            }
        }, 1);
    }
}
