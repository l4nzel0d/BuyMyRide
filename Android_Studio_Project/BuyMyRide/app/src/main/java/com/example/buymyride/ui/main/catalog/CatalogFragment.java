package com.example.buymyride.ui.main.catalog;

import android.os.Bundle;
import android.util.Log; // Import Log class
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager; // Reverted to LinearLayoutManager

import com.example.buymyride.R;
import com.example.buymyride.data.models.CarCardModel;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.CarsRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;
import com.example.buymyride.databinding.FragmentCatalogBinding;
import com.example.buymyride.ui.adapters.CarCardAdapter;
import com.google.android.material.appbar.MaterialToolbar; // Import MaterialToolbar

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CatalogFragment extends Fragment {
    private Menu toolbarMenu;
    private static final String TAG = "CatalogFragment"; // Define a TAG for logging

    private FragmentCatalogBinding binding;
    private CatalogViewModel viewModel;
    private CarCardAdapter carCardAdapter;
    private NavController navController;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No need for setHasOptionsMenu(true) when handling toolbar menu directly
        Log.d(TAG, "onCreate: Fragment created.");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        Log.d(TAG, "onCreateView: Layout inflated using View Binding.");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: View created.");

        navController = Navigation.findNavController(view);
        Log.d(TAG, "onViewCreated: NavController initialized.");

        setupRecyclerView();
        setupViewModel();
        setupToolbar(); // New method to set up the toolbar and its menu

        observeViewModel();
    }

    private boolean handleSortMenuItem(MenuItem item) {
        viewModel.onSortOptionSelected(item.getItemId());
        return true;
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = binding.catalogToolbar;
        toolbar.setOnMenuItemClickListener(item -> handleSortMenuItem(item));


        toolbarMenu = toolbar.getMenu();

        // Observe sortPreference to update checked menu item
        viewModel.getSortOption().observe(getViewLifecycleOwner(), sortOption -> {
            if (toolbarMenu == null || sortOption == null) return;


            // Check the correct menu item according to the current sortPreference
            int menuItemId = sortOption.getMenuItemId();
            MenuItem itemToCheck = toolbarMenu.findItem(menuItemId);
            if (itemToCheck != null) {
                itemToCheck.setChecked(true);
            }
        });

        Log.d(TAG, "setupToolbar: Toolbar menu listener set and sortPreference observer attached.");
    }


    private void setupRecyclerView() {
        carCardAdapter = new CarCardAdapter(requireContext(), new CarCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CarCardModel carCardModel = carCardAdapter.getCarCardModelAt(position);
                if (carCardModel != null) {
                    Log.d(TAG, "onItemClick: Car card clicked, ID: " + carCardModel.getId());
                    CatalogFragmentDirections.ActionCatalogFragmentToCarDetailsFragment action =
                            CatalogFragmentDirections.actionCatalogFragmentToCarDetailsFragment(carCardModel.getId());
                    navController.navigate(action);
                } else {
                    Log.w(TAG, "onItemClick: Clicked item at position " + position + " but CarCardModel was null.");
                }
            }

            @Override
            public void onFavoriteClick(int position) {
                CarCardModel carCardModel = carCardAdapter.getCarCardModelAt(position);
                if (carCardModel != null) {
                    Log.d(TAG, "onFavoriteClick: Favorite icon clicked for car ID: " + carCardModel.getId() + ", current favorite status: " + carCardModel.isFavorite());
                    viewModel.updateFavoriteStatus(carCardModel.getId(), !carCardModel.isFavorite());
                } else {
                    Log.w(TAG, "onFavoriteClick: Clicked favorite for item at position " + position + " but CarCardModel was null.");
                }
            }
        });

        // REVERTED TO LINEAR LAYOUT MANAGER
        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCars.setAdapter(carCardAdapter);
        Log.d(TAG, "setupRecyclerView: RecyclerView initialized with adapter and LinearLayoutManager.");
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CatalogViewModel.class);
        Log.d(TAG, "setupViewModel: CatalogViewModel initialized.");
    }

    private void observeViewModel() {
        viewModel.getCarsForDisplay().observe(getViewLifecycleOwner(), carCardModels -> {
            Log.d(TAG, "observeViewModel: carsForDisplay LiveData updated. Number of models: " + (carCardModels != null ? carCardModels.size() : "null"));
            carCardAdapter.setCarCardModelList(carCardModels);
            updateEmptyStateVisibility(carCardModels);
        });

        viewModel.getNumberOfCars().observe(getViewLifecycleOwner(), numberOfCars -> {
            Log.d(TAG, "observeViewModel: numberOfCars LiveData updated. Count: " + numberOfCars);
            if (numberOfCars > 0) {
                binding.catalogToolbar.setSubtitle(numberOfCars + " предложений");
            } else {
                binding.catalogToolbar.setSubtitle("");
            }
        });
    }

    private void updateEmptyStateVisibility(List<CarCardModel> carCardModels) {
        if (carCardModels == null || carCardModels.isEmpty()) {
            binding.noCatalogText.setVisibility(View.VISIBLE);
            binding.recyclerViewCars.setVisibility(View.GONE);
            Log.d(TAG, "updateEmptyStateVisibility: Displaying 'no catalog text'.");
        } else {
            binding.noCatalogText.setVisibility(View.GONE);
            binding.recyclerViewCars.setVisibility(View.VISIBLE);
            Log.d(TAG, "updateEmptyStateVisibility: Displaying cars, count: " + carCardModels.size());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "onDestroyView: View binding set to null.");
    }
}