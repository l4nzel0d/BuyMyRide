package com.example.buymyride.data.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {

    private final FirebaseFirestore firestore;

    @Inject
    public UserRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public Task<Void> saveUserData(String userId, String name, String phoneNumber) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phoneNumber", phoneNumber);

        return firestore.collection("users").document(userId).set(userData);
    }
}
