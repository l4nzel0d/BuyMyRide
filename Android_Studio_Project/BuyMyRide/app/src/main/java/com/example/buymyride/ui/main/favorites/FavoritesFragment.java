package com.example.buymyride.ui.main.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.buymyride.R;
import com.example.buymyride.data.models.CarCardModel;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.CarsRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;
import com.example.buymyride.ui.adapters.CarCardAdapter;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import androidx.navigation.Navigation;

@AndroidEntryPoint
public class FavoritesFragment extends Fragment {

    private FavoritesViewModel viewModel;
    private RecyclerView recyclerView;
    private CarCardAdapter adapter;
    private TextView noFavoritesText;

    @Inject
    CarsRepository carsRepository;
    @Inject
    MyUsersRepository myUsersRepository;
    @Inject
    AuthRepository authRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_favorite_cars);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noFavoritesText = view.findViewById(R.id.no_favorites_text);

        adapter = new CarCardAdapter(getContext(), new CarCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CarCardModel carCardModel = adapter.getCarCardModelAt(position);
                if (carCardModel != null) {
                    // Navigate to CarDetailsFragment using carCardModel.getId()
                    // Example using Navigation Component:
                    // NavDirections action = FavoritesFragmentDirections.actionFavoritesFragmentToCarDetailsFragment(carCardModel.getId());
                    // Navigation.findNavController(view).navigate(action);
                    android.util.Log.d("FavoritesFragment", "Car clicked with ID: " + carCardModel.getId());
                }
            }

            @Override
            public void onFavoriteClick(int position) {
                CarCardModel carCardModel = adapter.getCarCardModelAt(position);
                if (carCardModel != null) {
                    viewModel.updateFavoriteStatus(carCardModel.getId(), !carCardModel.isFavorite()); // Pass false to remove from favorites
                }
            }
        });
        recyclerView.setAdapter(adapter);

        FavoritesViewModel.Factory factory = new FavoritesViewModel.Factory(carsRepository, myUsersRepository, authRepository);
        viewModel = new ViewModelProvider(this, factory).get(FavoritesViewModel.class);

        viewModel.getFavoriteCars().observe(getViewLifecycleOwner(), carCardModels -> {
            adapter.setCarCardModelList(carCardModels);
            updateEmptyStateVisibility(carCardModels);
        });
    }

    private void updateEmptyStateVisibility(List<CarCardModel> carCardModels) {
        if (carCardModels == null || carCardModels.isEmpty()) {
            noFavoritesText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noFavoritesText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}