package com.example.buymyride.ui.appflow.splash;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.ui.appflow.auth.AuthActivity;
import com.example.buymyride.ui.appflow.main.MainActivity;
import com.example.buymyride.ui.util.OneTimeEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SplashViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<OneTimeEvent<SplashNavigationDestination>> navigateEvent =
            new MutableLiveData<>();

    public LiveData<OneTimeEvent<SplashNavigationDestination>> getNavigateEvent() {
        return navigateEvent;
    }

    @Inject
    public SplashViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;

        LiveData<String> liveUserId = authRepository.getLiveUserId();

        Observer<String> observer = new Observer<>() {
            @Override
            public void onChanged(String userId) {
                SplashNavigationDestination destination = (userId != null)
                        ? SplashNavigationDestination.MAIN
                        : SplashNavigationDestination.AUTH;

                navigateEvent.setValue(new OneTimeEvent<>(destination));

                liveUserId.removeObserver(this);
            }
        };

        liveUserId.observeForever(observer);
    }
}
