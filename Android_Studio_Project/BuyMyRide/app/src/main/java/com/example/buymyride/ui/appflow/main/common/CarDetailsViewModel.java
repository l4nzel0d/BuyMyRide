package com.example.buymyride.ui.appflow.main.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.buymyride.data.models.Car;
import com.example.buymyride.data.models.SpecItem;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.CarsRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CarDetailsViewModel extends ViewModel {

    private MutableLiveData<Car> car = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false); // Use LiveData for isFavorite
    private CarsRepository carsRepository;  // Use the injected repository
    private MyUsersRepository myUsersRepository; // Add MyUsersRepository
    private AuthRepository authRepository;  // Add AuthRepository

    private String carId; // Hold the car ID.  Crucial for fetching.


    @Inject
    public CarDetailsViewModel(SavedStateHandle savedStateHandle, CarsRepository carsRepository, MyUsersRepository myUsersRepository, AuthRepository authRepository) { // Add to Constructor
        this.carId = savedStateHandle.get("carId");
        if (this.carId == null) {
            // Handle case where carId is not provided (e.g., throw IllegalArgumentException or log error)
            throw new IllegalArgumentException("Car ID is required for CarDetailsViewModel");
        }
        this.carsRepository = carsRepository;
        this.myUsersRepository = myUsersRepository;
        this.authRepository = authRepository;
        loadCarDetails();
        checkFavoriteStatus(); // Check the favorite status when the ViewModel is created
    }

    public LiveData<Car> getCar() {
        return car;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public void toggleFavorite() {
        String userId = authRepository.getCurrentUserId().getValue(); // Get user ID
        if (userId == null) return;

        if (Boolean.TRUE.equals(isFavorite.getValue())) {
            // Remove from favorites
            myUsersRepository.removeCarFromFavorites(userId, carId)
                    .thenAccept(aVoid -> {
                        isFavorite.postValue(false); // Update LiveData
                    })
                    .exceptionally(throwable -> {
                        // Handle error (e.g., show a toast)
                        return null;
                    });
        } else {
            // Add to favorites
            myUsersRepository.addCarToFavorites(userId, carId)
                    .thenAccept(aVoid -> {
                        isFavorite.postValue(true); // Update LiveData
                    })
                    .exceptionally(throwable -> {
                        // Handle error
                        return null;
                    });
        }
    }

    private void loadCarDetails() {
        // Fetch car details using the carId
        CompletableFuture<Car> future = carsRepository.getCarById(carId);
        future.thenAccept(car -> {
            this.car.postValue(car); // Use postValue for background thread updates
        }).exceptionally(throwable -> {
            // Handle the error appropriately (e.g., show an error message)
            System.err.println("Failed to load car details: " + throwable.getMessage());
            this.car.postValue(null); // Or post an error state.
            return null; // Important: Return null to prevent crashing.
        });
    }

    private void checkFavoriteStatus() {
        String userId = authRepository.getCurrentUserId().getValue(); // Get User ID
        if (userId != null) {
            myUsersRepository.isCarInFavorites(userId, carId)
                    .thenAccept(result -> {
                        isFavorite.postValue(result);
                    })
                    .exceptionally(throwable -> {
                        // Handle error, default to false
                        isFavorite.postValue(false);
                        return null;
                    });
        } else {
            // User not logged in, default to false
            isFavorite.postValue(false);
        }
    }
}
