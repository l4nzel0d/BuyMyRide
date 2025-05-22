package com.example.buymyride.ui.main.common;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.buymyride.data.models.Car;
import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.data.repositories.CarsRepository;
import com.example.buymyride.data.repositories.MyUsersRepository;
import com.example.buymyride.databinding.FragmentCarDetailsBinding;
import com.example.buymyride.ui.adapters.SpecItemAdapter;
import com.example.buymyride.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CarDetailsFragment extends Fragment {

    private FragmentCarDetailsBinding binding;
    private CarDetailsViewModel viewModel;
    private MenuItem favoriteMenuItem;

    public CarDetailsFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCarDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CarDetailsViewModel.class);

        binding.recyclerViewCarSpecs.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteMenuItem = binding.topAppBar.getMenu().findItem(R.id.action_favorite);

        setListeners();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsFavorite().observe(getViewLifecycleOwner(), isFavorite -> {
            if (favoriteMenuItem != null) {
                favoriteMenuItem.setIcon(isFavorite
                        ? R.drawable.ic_favorites_filled
                        : R.drawable.ic_favorites);
                favoriteMenuItem.setChecked(isFavorite);

                Drawable icon = favoriteMenuItem.getIcon();
                if (icon != null) {
                    icon = DrawableCompat.wrap(icon);
                    DrawableCompat.setTint(icon, ContextCompat.getColor(requireContext(),
                            isFavorite ? R.color.color_primary : R.color.black));
                    favoriteMenuItem.setIcon(icon);
                }
            }
        });

        viewModel.getCar().observe(getViewLifecycleOwner(), car -> {
            if (car != null) {
                updateUI(car);
                binding.carTextDetailsLayout.setVisibility(View.VISIBLE);
            } else {
                binding.carTextDetailsLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setListeners() {
        binding.topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_favorite) {
                viewModel.toggleFavorite();
                return true;
            }
            return false;
        });

        binding.topAppBar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void updateUI(Car car) {
        binding.topAppBar.setTitle(car.make() + " " + car.model()); // Set toolbar title

        Glide.with(requireContext())
                .load(car.imageUrl())
                .into(binding.image);

        binding.textMake.setText(car.make());
        binding.textModel.setText(car.model());
        binding.textYear.setText(String.valueOf(car.year()));
        binding.textPrice.setText(String.valueOf(car.price()));
        binding.textPrice.setText(String.format("%,d", car.price()).replace(',', ' ') + " ₽");
        binding.textCreditInfo.setText("от " + String.format("%,d", car.creditPrice()).replace(',', ' ') + " ₽ / месяц");

        // Set up the RecyclerView adapter for the specifications
        SpecItemAdapter adapter = new SpecItemAdapter(car.specs());
        binding.recyclerViewCarSpecs.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
