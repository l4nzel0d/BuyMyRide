package com.example.buymyride.data.repositories;

import com.example.buymyride.data.models.MyUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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
}
