package com.example.buymyride.data.repositories;

import com.example.buymyride.data.models.Car;
import com.example.buymyride.data.models.SpecItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CarsRepository {

    private final FirebaseFirestore firestore;
    private final CollectionReference carsCollection;

    @Inject
    public CarsRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.carsCollection = firestore.collection("cars");
    }

    public CompletableFuture<List<Car>> getCars() {
        CompletableFuture<List<Car>> future = new CompletableFuture<>();
        carsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Car> cars = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Car car = documentToCar(document);
                            if (car != null) {
                                cars.add(car);
                            }
                        }
                        future.complete(cars);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
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
}
