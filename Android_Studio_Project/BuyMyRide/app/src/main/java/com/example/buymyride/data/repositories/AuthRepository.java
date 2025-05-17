package com.example.buymyride.data.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepository {
    private final FirebaseAuth firebaseAuth;

    @Inject
    public AuthRepository(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public FirebaseUser signIn(String email, String password) throws FirebaseAuthException {
        try {
            return firebaseAuth.signInWithEmailAndPassword(email, password).getResult().getUser();
        } catch (Exception e) {
            throw (FirebaseAuthException) e;
        }
    }
}
