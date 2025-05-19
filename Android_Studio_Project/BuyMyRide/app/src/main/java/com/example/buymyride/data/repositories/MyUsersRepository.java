package com.example.buymyride.data.repositories;

import com.example.buymyride.data.models.MyUser;
import com.example.buymyride.data.models.UserId;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyUsersRepository {

    private final FirebaseFirestore firestore;
    private final CollectionReference usersCollection;

    @Inject
    public MyUsersRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.usersCollection = firestore.collection("users");
    }

    public CompletableFuture<Void> saveUserData(MyUser myUser) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", myUser.getEmail());
        userData.put("name", myUser.getName());
        userData.put("phoneNumber", myUser.getPhoneNumber());

        firestore.collection("users").document(myUser.getUserId().userId())
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }

    public CompletableFuture<MyUser> getUserData(UserId userId) {
        CompletableFuture<MyUser> future = new CompletableFuture<>();

        firestore.collection("users").document(userId.userId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String email = document.getString("email");
                            String name = document.getString("name");
                            String phoneNumber = document.getString("phoneNumber");
                            List<String> favoriteCarIds = (List<String>) document.get("favoriteCarIds");
                            MyUser myUser = new MyUser(userId, email, name, phoneNumber, favoriteCarIds != null ? favoriteCarIds : List.of());
                            future.complete(myUser);
                        } else {
                            future.complete(null); // User data not found
                        }
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    public CompletableFuture<Void> addCarToFavorites(String userId, String carId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DocumentReference favCarRef = usersCollection.document(userId)
                .collection("favoriteCars").document(carId);

        Map<String, Object> data = new HashMap<>();
        data.put("addedAt", FieldValue.serverTimestamp());

        favCarRef.set(data, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }

    public CompletableFuture<Void> removeCarFromFavorites(String userId, String carId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DocumentReference favCarRef = usersCollection.document(userId)
                .collection("favoriteCars").document(carId);

        favCarRef.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }

    public CompletableFuture<List<String>> getFavoriteCarIds(String userId) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        CollectionReference favCarsRef = usersCollection.document(userId).collection("favoriteCars");

        favCarsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> carIds = new java.util.ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    carIds.add(document.getId()); // The document ID is the carId
                }
                future.complete(carIds);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    public CompletableFuture<Boolean> isCarInFavorites(String userId, String carId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        DocumentReference favCarRef = usersCollection.document(userId)
                .collection("favoriteCars").document(carId);

        favCarRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                future.complete(document.exists()); //  returns true if the document exists, false otherwise
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }
}
