package com.example.buymyride.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.buymyride.data.models.Car;
import com.example.buymyride.data.models.SpecItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CarsRepository {

    private static final String TAG = "CarsRepository";
    private final FirebaseFirestore firestore;
    private final CollectionReference carsCollection;

    // LiveData for real-time updates of all cars
    private final MutableLiveData<List<Car>> allCarsLiveData = new MutableLiveData<>();
    private ListenerRegistration allCarsListenerRegistration; // To manage the Firestore listener

    @Inject
    public CarsRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.carsCollection = firestore.collection("cars");
        setupAllCarsListener(); // Initialize the real-time listener when repository is created
    }

    /**
     * Sets up a real-time listener for the entire 'cars' collection.
     * Updates allCarsLiveData whenever there's a change in the collection.
     */
    private void setupAllCarsListener() {
        // Remove any existing listener to prevent duplicates and memory leaks
        if (allCarsListenerRegistration != null) {
            allCarsListenerRegistration.remove();
        }

        allCarsListenerRegistration = carsCollection.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                // Consider how you want to handle this error in UI (e.g., set LiveData to error state)
                allCarsLiveData.postValue(new ArrayList<>()); // Post empty list on error
                return;
            }

            if (snapshot != null) {
                List<Car> cars = new ArrayList<>();
                for (QueryDocumentSnapshot document : snapshot) {
                    Car car = documentToCar(document);
                    if (car != null) {
                        cars.add(car);
                    }
                }
                allCarsLiveData.postValue(cars); // Post the new list to LiveData
            } else {
                Log.d(TAG, "Current data: null snapshot");
                allCarsLiveData.postValue(new ArrayList<>()); // Post empty list if snapshot is null
            }
        });
    }

    /**
     * Provides a LiveData stream of all cars, updating in real-time.
     *
     * @return LiveData containing a list of Car objects.
     */
    public LiveData<List<Car>> getAllCarsLiveData() {
        return allCarsLiveData;
    }

    public CompletableFuture<Car> getCarById(String carId) {
        CompletableFuture<Car> future = new CompletableFuture<>();
        carsCollection.document(carId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Car car = documentToCar(document);
                        if (car != null) {
                            future.complete(car);
                        } else {
                            future.completeExceptionally(new Exception("Car with ID " + carId + " not found"));
                        }
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }

    private Car documentToCar(DocumentSnapshot document) {
        if (document != null && document.exists()) {
            try {
                String id = document.getId();
                String imageUrl = document.getString("imageUrl");
                String make = document.getString("make");
                String model = document.getString("model");
                Long yearLong = document.getLong("year");
                int year = yearLong != null ? yearLong.intValue() : 0;
                Long price = document.getLong("price");
                Long creditPrice = document.getLong("creditPrice");

                List<SpecItem> specsList = new ArrayList<>();
                List<Map<String, String>> specsMapList = (List<Map<String, String>>) document.get("specs");
                if (specsMapList != null) {
                    for (Map<String, String> specMap : specsMapList) {
                        for (Map.Entry<String, String> entry : specMap.entrySet()) {
                            String specName = entry.getKey();
                            String specValue = entry.getValue();
                            specsList.add(new SpecItem(specName, specValue));
                        }
                    }
                }
                return new Car(id, imageUrl, make, model, year, price != null ? price : 0L, creditPrice != null ? creditPrice : 0L, specsList);
            } catch (Exception e) {
                System.err.println("Error processing document: " + document.getId() + " - " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * Cleans up Firestore listeners to prevent memory leaks.
     * Call this when the repository is no longer needed (e.g., in ViewModel's onCleared()).
     */
    public void cleanup() {
        if (allCarsListenerRegistration != null) {
            allCarsListenerRegistration.remove();
            allCarsListenerRegistration = null;
            Log.d(TAG, "All cars listener removed.");
        }
    }
}
