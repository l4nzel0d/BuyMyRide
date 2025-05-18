package com.example.buymyride.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.buymyride.R;
import com.example.buymyride.ui.auth.AuthActivity;
import com.example.buymyride.ui.splash.SplashActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // XML с nav_host_fragment

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment); // ID должен совпадать
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        } else {
            throw new IllegalStateException("NavHostFragment not found");
        }
    }
}