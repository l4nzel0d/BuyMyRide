package com.example.buymyride.data.repositories;

import com.example.buymyride.data.models.Car;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
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

}
