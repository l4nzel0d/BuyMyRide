package com.example.buymyride.ui.main.catalog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buymyride.R;
import com.example.buymyride.data.models.CarCardModel;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.CarsRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;
import com.example.buymyride.databinding.FragmentCatalogBinding;
import com.example.buymyride.ui.adapters.CarCardAdapter;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CatalogFragment extends Fragment {
    private static final String TAG = "CatalogFragment";

    private FragmentCatalogBinding binding;
    private CatalogViewModel viewModel;
    private CarCardAdapter carCardAdapter;
    private NavController navController;

    @Inject
    CarsRepository carsRepository;
    @Inject
    MyUsersRepository myUsersRepository;
    @Inject
    AuthRepository authRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate: Fragment created, options menu enabled.");
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
        observeViewModel();
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

        binding.recyclerViewCars.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCars.setAdapter(carCardAdapter);
    }

    private void setupViewModel() {
        // Instantiate the factory using injected dependencies
        CatalogViewModel.Factory factory = new CatalogViewModel.Factory(carsRepository, myUsersRepository, authRepository);
        viewModel = new ViewModelProvider(this, factory).get(CatalogViewModel.class);
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
            binding.textNumberOfOffers.setText(numberOfCars + " предложений");
        });
    }

    private void updateEmptyStateVisibility(List<CarCardModel> carCardModels) {
        if (carCardModels == null || carCardModels.isEmpty()) {
            binding.noCatalogText.setVisibility(View.VISIBLE);
            binding.recyclerViewCars.setVisibility(View.GONE);
            binding.textNumberOfOffers.setVisibility(View.GONE);
            Log.d(TAG, "updateEmptyStateVisibility: Displaying 'no catalog text'.");
        } else {
            binding.noCatalogText.setVisibility(View.GONE);
            binding.recyclerViewCars.setVisibility(View.VISIBLE);
            binding.textNumberOfOffers.setVisibility(View.VISIBLE);
            Log.d(TAG, "updateEmptyStateVisibility: Displaying cars, count: " + carCardModels.size());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.sort_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu: Sort menu inflated.");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String sortPreference = "";
        if (id == R.id.sort_by_ascending_price) {
            sortPreference = "price_asc";
        } else if (id == R.id.sort_by_descending_price) {
            sortPreference = "price_desc";
        } else if (id == R.id.sort_by_newest) {
            sortPreference = "year_desc";
        } else if (id == R.id.sort_by_oldest) {
            sortPreference = "year_asc";
        }

        if (!sortPreference.isEmpty()) {
            viewModel.setSortPreference(sortPreference);
            Log.d(TAG, "onOptionsItemSelected: Sort preference set to: " + sortPreference);
            // Optionally, you might want to check the item to show it's selected
            // item.setChecked(true); // You'd need to manage unchecked items for the group
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected: Unhandled menu item ID: " + id);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d(TAG, "onDestroyView: View binding set to null.");
    }
}
