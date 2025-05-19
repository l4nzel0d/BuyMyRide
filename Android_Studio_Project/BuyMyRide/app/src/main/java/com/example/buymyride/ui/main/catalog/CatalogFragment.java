package com.example.buymyride.ui.main.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.buymyride.R;

public class CatalogFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button navigateButton = view.findViewById(R.id.btn_navigate_to_car_details);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a NavController
                NavController navController = Navigation.findNavController(v);

                // Create a NavDirections object using the generated Directions class
                NavDirections action = CatalogFragmentDirections.actionCatalogFragmentToCarDetailsFragment("5NolgTtJxh7Xkddiyhh0");

                // Navigate using the NavController
                navController.navigate(action);
            }
        });
    }
}
