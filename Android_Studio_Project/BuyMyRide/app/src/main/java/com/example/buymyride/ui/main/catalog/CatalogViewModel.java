package com.example.buymyride.ui.main.catalog;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.buymyride.data.models.Car;
import com.example.buymyride.data.models.CarCardModel;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.CarsRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;
import com.example.buymyride.util.SortOption;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CatalogViewModel extends ViewModel {

    private LiveData<List<Car>> allCarsLiveData;
    private MutableLiveData<SortOption> sortOption = new MutableLiveData<>(SortOption.YEAR_DESC);
    private MediatorLiveData<List<CarCardModel>> carsForDisplay = new MediatorLiveData<>(); // Use MediatorLiveData
    private LiveData<Integer> numberOfCars;
    private CarsRepository carsRepository;
    private MyUsersRepository myUsersRepository;
    private AuthRepository authRepository;

    // Internal LiveData to hold favorite car IDs for MediatorLiveData
    private LiveData<List<String>> favoriteCarIdsSource;

    @Inject
    public CatalogViewModel(CarsRepository carsRepository, MyUsersRepository myUsersRepository, AuthRepository authRepository) {
        this.carsRepository = carsRepository;
        this.myUsersRepository = myUsersRepository;
        this.authRepository = authRepository;
        this.allCarsLiveData = carsRepository.getAllCarsLiveData();
        initializeCarsForDisplay();
        initializeNumberOfCars();
    }

    private void initializeCarsForDisplay() {
        // We'll update carsForDisplay whenever allCarsLiveData, sortPreference, or favoriteCarIdsSource changes
        carsForDisplay.addSource(allCarsLiveData, cars -> updateCarsForDisplay());
        carsForDisplay.addSource(sortOption, sortOption -> updateCarsForDisplay());

        // We need to get the favoriteCarIdsSource dynamically based on the current user
        // This will be null initially if user is not logged in, or if ID is not available yet
        // We'll update this source whenever the user ID changes.
        carsForDisplay.addSource(authRepository.getCurrentUserId(), userId -> {
            // Remove previous favoriteCarIdsSource if it exists
            if (favoriteCarIdsSource != null) {
                carsForDisplay.removeSource(favoriteCarIdsSource);
            }

            if (userId != null) {
                // If user is logged in, get their favorite car IDs LiveData
                favoriteCarIdsSource = myUsersRepository.getFavoriteCarIdsLiveData(userId);
                carsForDisplay.addSource(favoriteCarIdsSource, favoriteIds -> updateCarsForDisplay());
            } else {
                // If no user, or user logs out, clear the favorite car IDs source
                favoriteCarIdsSource = new MutableLiveData<>(new ArrayList<>()); // Empty list
                carsForDisplay.addSource(favoriteCarIdsSource, favoriteIds -> updateCarsForDisplay());
            }
            // Trigger an immediate update since the favorite source might have changed
            updateCarsForDisplay();
        });

        // Initial trigger for update
        updateCarsForDisplay();
    }

    // This method will be called whenever any of the source LiveData (allCarsLiveData, sortPreference, favoriteCarIdsSource) changes.
    private void updateCarsForDisplay() {
        List<Car> currentCars = allCarsLiveData.getValue();
        SortOption currentSortOption = sortOption.getValue();
        String currentUserId = authRepository.getCurrentUserId().getValue();
        List<String> currentFavoriteCarIds = (favoriteCarIdsSource != null && favoriteCarIdsSource.getValue() != null) ? favoriteCarIdsSource.getValue() : new ArrayList<>();

        if (currentCars == null) {
            carsForDisplay.setValue(new ArrayList<>());
            return;
        }

        List<Car> sortedCars = new ArrayList<>(currentCars);
        sortedCars.sort(currentSortOption.getComparator());

        List<CarCardModel> carCardModels = new ArrayList<>();
        for (Car car : sortedCars) {
            boolean isFavorite = currentUserId != null && currentFavoriteCarIds.contains(car.id());
            carCardModels.add(new CarCardModel(car.id(), car.make(), car.model(), car.year(), (int) car.price(), car.imageUrl(), isFavorite));
        }
        carsForDisplay.setValue(carCardModels);
    }

    private void initializeNumberOfCars() {
        this.numberOfCars = Transformations.map(allCarsLiveData, cars -> (cars != null) ? cars.size() : 0);
    }

    // LiveData for the list of cars to display
    public LiveData<List<CarCardModel>> getCarsForDisplay() {
        return carsForDisplay;
    }

    // LiveData for the number of cars
    public LiveData<Integer> getNumberOfCars() {
        return numberOfCars;
    }

    public LiveData<SortOption> getSortOption() {
        return this.sortOption;
    }

    public void onSortOptionSelected(int menuItemId) {
        SortOption selectedOption = SortOption.fromMenuItemId(menuItemId);
        if (selectedOption != null && selectedOption != sortOption.getValue()) {
            sortOption.setValue(selectedOption);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // carsRepository.cleanup(); // Cleanup is handled elsewhere for Singleton
        // Important: Remove the source for favoriteCarIdsSource if it exists
        if (favoriteCarIdsSource != null) {
            carsForDisplay.removeSource(favoriteCarIdsSource);
        }
    }

    // Method to update the favorite status of a car
    public void updateFavoriteStatus(String carId, boolean isFavorite) {
        String userId = authRepository.getCurrentUserId().getValue();
        if (userId != null) {
            CompletableFuture<Void> updateFuture;
            if (isFavorite) {
                updateFuture = myUsersRepository.addCarToFavorites(userId, carId);
            } else {
                updateFuture = myUsersRepository.removeCarFromFavorites(userId, carId);
            }
            updateFuture.exceptionally(throwable -> {
                System.err.println("Error updating favorite status: " + throwable.getMessage());
                return null;
            });
            // The LiveData observers will react to changes automatically
        }
    }
}




