package com.example.buymyride.ui.main.catalog;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.example.buymyride.R;
import com.example.buymyride.data.models.Car;
import com.example.buymyride.data.models.CarCardModel;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.CarsRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;
import com.example.buymyride.util.CarSortUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CatalogViewModel extends ViewModel {

    private LiveData<List<Car>> allCarsLiveData;
    private MutableLiveData<String> sortPreference = new MutableLiveData<>("year_desc");
    private MediatorLiveData<List<CarCardModel>> carsForDisplay = new MediatorLiveData<>(); // Use MediatorLiveData
    private LiveData<Integer> numberOfCars;
    private CarsRepository carsRepository;
    private MyUsersRepository myUsersRepository;
    private AuthRepository authRepository;

    // Internal LiveData to hold favorite car IDs for MediatorLiveData
    private LiveData<List<String>> favoriteCarIdsSource;

    // Factory
    public static class Factory implements ViewModelProvider.Factory {
        private final CarsRepository carsRepository;
        private final MyUsersRepository myUsersRepository;
        private final AuthRepository authRepository;

        public Factory(CarsRepository carsRepository, MyUsersRepository myUsersRepository, AuthRepository authRepository) {
            this.carsRepository = carsRepository;
            this.myUsersRepository = myUsersRepository;
            this.authRepository = authRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
            if (modelClass.isAssignableFrom(CatalogViewModel.class)) {
                return (T) new CatalogViewModel(carsRepository, myUsersRepository, authRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    // Constructor
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
        carsForDisplay.addSource(sortPreference, sortPref -> updateCarsForDisplay());

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
        String currentSortPref = sortPreference.getValue();
        String currentUserId = authRepository.getCurrentUserId().getValue();
        List<String> currentFavoriteCarIds = (favoriteCarIdsSource != null && favoriteCarIdsSource.getValue() != null) ? favoriteCarIdsSource.getValue() : new ArrayList<>();

        if (currentCars == null) {
            carsForDisplay.setValue(new ArrayList<>());
            return;
        }

        List<Car> sortedCars = new ArrayList<>(currentCars);
        sortCars(sortedCars, currentSortPref != null ? currentSortPref : "none");

        List<CarCardModel> carCardModels = new ArrayList<>();
        for (Car car : sortedCars) {
            boolean isFavorite = currentUserId != null && currentFavoriteCarIds.contains(car.id());
            carCardModels.add(new CarCardModel(car.id(), car.make(), car.model(), car.year(), (int) car.price(), car.imageUrl(), isFavorite));
        }
        carsForDisplay.setValue(carCardModels);
    }


    // This method is no longer needed as the logic is now within updateCarsForDisplay
    // private List<CarCardModel> createCarCardModels(List<Car> sortedCars, String userId) { /* ... */ }

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

    public LiveData<String> getSortPreference() {
        return this.sortPreference;
    }

    // Instead of exposing setSortPreference, expose this:
    public void onSortOptionSelected(int menuItemId) {
        String sortPreferenceFromMenuId = mapMenuIdToSortPreference(menuItemId);
        if (!sortPreferenceFromMenuId.equals(sortPreference.getValue())) {
            sortPreference.setValue(sortPreferenceFromMenuId);
        }
    }

    private String mapMenuIdToSortPreference(int menuItemId) {
        if (menuItemId == R.id.sort_by_ascending_price) {
            return "price_asc";
        } else if (menuItemId == R.id.sort_by_descending_price) {
            return "price_desc";
        } else if (menuItemId == R.id.sort_by_newest) {
            return "year_desc";
        } else if (menuItemId == R.id.sort_by_oldest) {
            return "year_asc";
        } else {
            return sortPreference.getValue() != null ? sortPreference.getValue() : "year_desc";
        }
    }

    // Helper method to sort the list of cars
    private void sortCars(List<Car> cars, String sortPreference) {
        if (sortPreference.equals("price_asc")) {
            CarSortUtils.sortByPriceAscending(cars);
        } else if (sortPreference.equals("price_desc")) {
            CarSortUtils.sortByPriceDescending(cars);
        } else if (sortPreference.equals("year_asc")) {
            CarSortUtils.sortByYearOldest(cars);
        } else if (sortPreference.equals("year_desc")) {
            CarSortUtils.sortByYearNewest(cars);
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
