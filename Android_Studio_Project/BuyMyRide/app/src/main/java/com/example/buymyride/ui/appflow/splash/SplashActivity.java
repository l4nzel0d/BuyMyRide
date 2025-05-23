package com.example.buymyride.ui.appflow.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.splashscreen.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.buymyride.R;
import com.example.buymyride.ui.appflow.auth.AuthActivity;
import com.example.buymyride.ui.appflow.main.MainActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {

    private SplashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        viewModel = new ViewModelProvider(this).get(SplashViewModel.class);

        splashScreen.setKeepOnScreenCondition(() ->
                viewModel.getNavigateEvent().getValue() == null
        );

        viewModel.getNavigateEvent().observe(this, event -> {
            if (event == null) return;

            SplashNavigationDestination destination = event.getContentIfNotHandled();
            if (destination == null) return;

            Class<?> targetActivity = switch (destination) {
                case MAIN -> MainActivity.class;
                case AUTH -> AuthActivity.class;
            };

            startActivity(new Intent(this, targetActivity));
            finish();
        });
    }
}