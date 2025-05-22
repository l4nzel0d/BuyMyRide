package com.example.buymyride.ui.main.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.annotation.NonNull;
import androidx.lifecycle.Transformations;

import com.example.buymyride.data.models.Car;
import com.example.buymyride.data.models.CarCardModel;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.CarsRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoritesViewModel extends ViewModel {

    private LiveData<List<String>> favoriteCarIdsLiveData;
    private LiveData<List<CarCardModel>> favoriteCars;
    private CarsRepository carsRepository;
    private MyUsersRepository myUsersRepository;
    private AuthRepository authRepository;

    @Inject
    public FavoritesViewModel(CarsRepository carsRepository, MyUsersRepository myUsersRepository, AuthRepository authRepository) {
        this.carsRepository = carsRepository;
        this.myUsersRepository = myUsersRepository;
        this.authRepository = authRepository;
        String userId = authRepository.getCurrentUserId().getValue();
        if (userId != null) {
            favoriteCarIdsLiveData = myUsersRepository.getFavoriteCarIdsLiveData(userId);
            favoriteCars = Transformations.switchMap(favoriteCarIdsLiveData, carIds -> {
                MutableLiveData<List<CarCardModel>> carCardModelsLiveData = new MutableLiveData<>();
                if (carIds == null || carIds.isEmpty()) {
                    carCardModelsLiveData.setValue(new ArrayList<>());
                    return carCardModelsLiveData;
                }
                List<CompletableFuture<CarCardModel>> futures = new ArrayList<>();
                for (String carId : carIds) {
                    futures.add(carsRepository.getCarById(carId)
                            .thenCombine(myUsersRepository.isCarInFavorites(userId, carId),
                                    (car, isFav) -> new CarCardModel(car.id(), car.make(), car.model(), car.year(), (int) car.price(), car.imageUrl(), isFav)));
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .thenApply(results -> {
                            List<CarCardModel> models = new ArrayList<>();
                            for (CompletableFuture<CarCardModel> future : futures) {
                                try {
                                    models.add(future.join());
                                } catch (Exception e) {
                                    // Handle potential exceptions during fetching car details
                                    System.err.println("Error fetching car details: " + e.getMessage());
                                }
                            }
                            carCardModelsLiveData.postValue(models);
                            return null;
                        });
                return carCardModelsLiveData;
            });
        } else {
            favoriteCars = new MutableLiveData<>(new ArrayList<>());
            favoriteCarIdsLiveData = new MutableLiveData<>(new ArrayList<>());
        }
    }

    // LiveData for the list of favorite cars
    public LiveData<List<CarCardModel>> getFavoriteCars() {
        return favoriteCars;
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
        }
    }
}