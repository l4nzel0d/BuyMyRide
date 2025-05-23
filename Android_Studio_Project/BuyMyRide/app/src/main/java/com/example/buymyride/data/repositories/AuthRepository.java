package com.example.buymyride.data.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> liveFirebaseUser = new MutableLiveData<>();

    @Inject
    public AuthRepository(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;

        firebaseAuth.addAuthStateListener(auth -> liveFirebaseUser.setValue(auth.getCurrentUser()));

        liveFirebaseUser.setValue(firebaseAuth.getCurrentUser());
    }

    public LiveData<String> getLiveUserId() {
        return Transformations.map(liveFirebaseUser, firebaseUser ->
                firebaseUser != null ? firebaseUser.getUid() : null);
    }

    public CompletableFuture<String> signUp(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            future.complete(userId);
                        } else {
                            future.completeExceptionally(new RuntimeException("User is null after sign-up"));
                        }
                    } else {
                        future.completeExceptionally(
                                task.getException() != null ? task.getException() : new RuntimeException("Unknown sign-up error")
                        );
                    }
                });
        return future;
    }

    public CompletableFuture<String> signIn(String email, String password) {
        CompletableFuture<String> future = new CompletableFuture<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            future.complete(userId);
                        } else {
                            future.completeExceptionally(new RuntimeException("User is null after successful sign-in"));
                        }
                    } else {
                        future.completeExceptionally(task.getException() != null ? task.getException() : new RuntimeException("Unknown sign-in error"));
                    }
                });
        return future;
    }

    public LiveData<String> getCurrentUserId() {
        MutableLiveData<String> currentUserId = new MutableLiveData<>();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            currentUserId.setValue(user.getUid());
        } else {
            currentUserId.setValue(null);
        }
        return currentUserId;
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public CompletableFuture<Void> resetPassword(String email) { // Changed return type to CompletableFuture<Void>
        CompletableFuture<Void> future = new CompletableFuture<>();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        future.complete(null); // Complete successfully with null for Void
                    } else {
                        // Complete exceptionally with the actual exception
                        future.completeExceptionally(task.getException() != null ?
                                task.getException() : new RuntimeException("Unknown error sending reset email"));
                    }
                });
        return future;
    }
}
