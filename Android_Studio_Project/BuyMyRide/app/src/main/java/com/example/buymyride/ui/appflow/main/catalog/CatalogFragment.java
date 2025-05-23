package com.example.buymyride.ui.appflow.main.catalog;

import android.os.Bundle;
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

import com.example.buymyride.data.models.CarCardModel;
import com.example.buymyride.databinding.FragmentCatalogBinding;
import com.example.buymyride.ui.adapters.CarCardAdapter;
import com.google.android.material.appbar.MaterialToolbar; // Import MaterialToolbar

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CatalogFragment extends Fragment {
    private Menu toolbarMenu;

    private FragmentCatalogBinding binding;
    private CatalogViewModel viewModel;
    private CarCardAdapter carCardAdapter;
    private NavController navController;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

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

    }


    private void setupRecyclerView() {
        carCardAdapter = new CarCardAdapter(requireContext(), new CarCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CarCardModel carCardModel = carCardAdapter.getCarCardModelAt(position);
                if (carCardModel != null) {
                    CatalogFragmentDirections.ActionCatalogFragmentToCarDetailsFragment action =
                            CatalogFragmentDirections.actionCatalogFragmentToCarDetailsFragment(carCardModel.id());
                    navController.navigate(action);
                } else {
                }
            }

            @Override
            public void onFavoriteClick(int position) {
                CarCardModel carCardModel = carCardAdapter.getCarCardModelAt(position);
                if (carCardModel != null) {
                    viewModel.updateFavoriteStatus(carCardModel.id(), !carCardModel.isFavorite());
                }
            }
        });

        // REVERTED TO LINEAR LAYOUT MANAGER
        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCars.setAdapter(carCardAdapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CatalogViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getCarsForDisplay().observe(getViewLifecycleOwner(), carCardModels -> {
            carCardAdapter.setCarCardModelList(carCardModels);
            updateEmptyStateVisibility(carCardModels);
        });

        viewModel.getNumberOfCars().observe(getViewLifecycleOwner(), numberOfCars -> {
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
        } else {
            binding.noCatalogText.setVisibility(View.GONE);
            binding.recyclerViewCars.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}