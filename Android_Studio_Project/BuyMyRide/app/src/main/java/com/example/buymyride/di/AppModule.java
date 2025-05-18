package com.example.buymyride.di;

import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(FirebaseAuth firebaseAuth) {
        return new AuthRepository(firebaseAuth);
    }

    @Provides
    @Singleton
    public MyUsersRepository provideMyUsersRepository(FirebaseFirestore firebaseFirestore) {
        return new MyUsersRepository(firebaseFirestore);
    }
}
