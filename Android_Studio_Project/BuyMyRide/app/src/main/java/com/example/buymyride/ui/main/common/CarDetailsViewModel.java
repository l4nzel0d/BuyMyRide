package com.example.buymyride.ui.main.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.buymyride.data.models.Car;
import com.example.buymyride.data.models.SpecItem;
import com.example.buymyride.data.repositories.CarsRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

public class CarDetailsViewModel extends ViewModel {

    private MutableLiveData<Car> car = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false); // Use LiveData for isFavorite
    private CarsRepository carsRepository;  // Use the injected repository

    private String carId; // Hold the car ID.  Crucial for fetching.

    // Use Factory to pass the carId
    public static class Factory implements ViewModelProvider.Factory {
        private final String carId;
        private final CarsRepository carsRepository;

        public Factory(String carId, CarsRepository carsRepository) {
            this.carId = carId;
            this.carsRepository = carsRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
            if (modelClass.isAssignableFrom(CarDetailsViewModel.class)) {
                return (T) new CarDetailsViewModel(carId, carsRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    // Constructor now takes carId and CarsRepository
    public CarDetailsViewModel(String carId, CarsRepository carsRepository) {
        this.carId = carId;
        this.carsRepository = carsRepository;
        loadCarDetails(); // Load car details immediately upon creation.
    }

    public LiveData<Car> getCar() {
        return car;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public void toggleFavorite() {
        isFavorite.setValue(!isFavorite.getValue());
        // In a real app, you'd update the database here.  Use a Repository method.
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

    public Car getCarDetails() {
        return car.getValue();
    }
}
