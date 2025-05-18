package com.example.buymyride.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.buymyride.data.model.Result;
import com.example.buymyride.data.model.UserId;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepository {
    private final FirebaseAuth firebaseAuth;

    @Inject
    public AuthRepository(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }


    public CompletableFuture<Result<UserId>> signUp(String email, String password) {
        CompletableFuture<Result<UserId>> future = new CompletableFuture<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            UserId userId = new UserId(firebaseUser.getUid());
                            future.complete(Result.success(userId));
                        } else {
                            future.complete(Result.error(new RuntimeException("User is null")));
                        }
                    } else {
                        future.complete(Result.error(new RuntimeException(task.getException().getMessage())));
                    }
                });
        return future;
    }

    public CompletableFuture<Result<UserId>> signIn(String email, String password) {
        CompletableFuture<Result<UserId>> future = new CompletableFuture<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            UserId userId = new UserId(firebaseUser.getUid());
                            future.complete(Result.success(userId));
                        } else {
                            future.complete(Result.error(new RuntimeException("User is null")));
                        }
                    } else {
                        future.complete(Result.error(new RuntimeException(task.getException().getMessage())));
                    }
                });
        return future;
    }

    public LiveData<UserId> getCurrentUserId() {
        MutableLiveData<UserId> currentUserId = new MutableLiveData<>();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            currentUserId.setValue(new UserId(user.getUid()));
        } else {
            currentUserId.setValue(null);
        }
        return currentUserId;
    }

    public void signOut() {
        firebaseAuth.signOut();
    }
}
