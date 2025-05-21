package com.example.buymyride.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.buymyride.data.models.MyUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
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
    private static final String TAG = "MyUsersRepository";

    @Inject
    public MyUsersRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.usersCollection = firestore.collection("users");
    }

    public CompletableFuture<Void> saveUserData(MyUser myUser) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", myUser.email());
        userData.put("name", myUser.name());
        userData.put("phoneNumber", myUser.phoneNumber());

        firestore.collection("users").document(myUser.userId())
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

    public CompletableFuture<MyUser> getUserData(String userId) {
        CompletableFuture<MyUser> future = new CompletableFuture<>();

        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String email = document.getString("email");
                            String name = document.getString("name");
                            String phoneNumber = document.getString("phoneNumber");
                            MyUser myUser = new MyUser(userId, email, name, phoneNumber);
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

    private CompletableFuture<Void> updateUserDataField(String userId, String fieldName, String newValue) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (userId == null || fieldName == null || newValue == null) {
            future.completeExceptionally(new IllegalArgumentException("User ID, field name, or new value cannot be null."));
            return future;
        }

        DocumentReference userDocRef = usersCollection.document(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put(fieldName, newValue);

        userDocRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }

    public CompletableFuture<Void> updateUserProfile(String userId, String newName, String newPhoneNumber) {
        // Create CompletableFuture to combine results
        CompletableFuture<Void> nameUpdateFuture = updateUserDataField(userId, "name", newName);
        CompletableFuture<Void> phoneUpdateFuture = updateUserDataField(userId, "phoneNumber", newPhoneNumber);

        // Combine the two futures. This future will complete successfully if both complete successfully,
        // or complete exceptionally if either one fails.
        return CompletableFuture.allOf(nameUpdateFuture, phoneUpdateFuture);
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

    public LiveData<List<String>> getFavoriteCarIdsLiveData(String userId) {
        MutableLiveData<List<String>> favoriteCarIdsLiveData = new MutableLiveData<>();

        usersCollection.document(userId)
                .collection("favoriteCars")
                .orderBy("addedAt", Query.Direction.DESCENDING) // Sort by addedAt
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        android.util.Log.e(TAG, "Listen failed.", error);
                        favoriteCarIdsLiveData.setValue(null); // Or handle error appropriately
                        return;
                    }

                    List<String> favoriteIds = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            favoriteIds.add(doc.getId()); // The document ID is the carId
                        }
                    }
                    favoriteCarIdsLiveData.setValue(favoriteIds);
                });

        return favoriteCarIdsLiveData;
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
