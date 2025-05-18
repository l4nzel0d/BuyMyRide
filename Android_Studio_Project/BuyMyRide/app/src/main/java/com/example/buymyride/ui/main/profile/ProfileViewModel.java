package com.example.buymyride.ui.main.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.repositories.AuthRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> _navigateToAuth = new MutableLiveData<>();
    public LiveData<Boolean> navigateToAuth = _navigateToAuth;

    @Inject
    public ProfileViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void signOut() {
        authRepository.signOut();
        _navigateToAuth.setValue(true);
    }
}
