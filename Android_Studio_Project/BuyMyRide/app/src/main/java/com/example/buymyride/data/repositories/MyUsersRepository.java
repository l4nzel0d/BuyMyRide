package com.example.buymyride.data.repositories;

import com.example.buymyride.data.models.MyUser;
import com.example.buymyride.data.models.UserId;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyUsersRepository {

    private final FirebaseFirestore firestore;

    @Inject
    public MyUsersRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public CompletableFuture<Void> saveUserData(MyUser myUser) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", myUser.getEmail());
        userData.put("name", myUser.getName());
        userData.put("phoneNumber", myUser.getPhoneNumber());

        firestore.collection("users").document(myUser.getUserId().getUserId())
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

        firestore.collection("users").document(userId.getUserId())
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
}
