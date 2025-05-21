package com.example.buymyride.ui.main.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.buymyride.R;

import com.example.buymyride.databinding.FragmentEditProfileBinding;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment"; // Tag for logging

    private FragmentEditProfileBinding binding;
    private EditProfileViewModel viewModel;
    private NavController navController;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        Log.d(TAG, "onCreateView: Layout inflated.");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Fragment view created.");

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        Log.d(TAG, "onViewCreated: NavController and ViewModel initialized.");

        setupToolbar();
        observeViewModel();
        setupListeners();
    }

    private void setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener(v -> {
            Log.d(TAG, "setupToolbar: Back navigation clicked.");
            navController.navigateUp();
        });
        // Title is already set in XML: app:title="Изменение данных"
    }

    private void observeViewModel() {
        Log.d(TAG, "observeViewModel: Setting up ViewModel observers.");

        // Observe user data to pre-fill fields
        viewModel.getUserData().observe(getViewLifecycleOwner(), myUser -> {
            if (myUser != null) {
                Log.d(TAG, "observeViewModel: User data received. Name: " + myUser.name() + ", Phone: " + myUser.phoneNumber());
                binding.inputName.setText(myUser.name()); // Changed to myUser.name()
                binding.inputPhone.setText(myUser.phoneNumber()); // Changed to myUser.phoneNumber()
            } else {
                Log.d(TAG, "observeViewModel: User data is null.");
            }
        });

        // Observe loading state to disable/enable button
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "observeViewModel: isLoading changed to " + isLoading);
            binding.buttonConfirmChanges.setEnabled(!isLoading);
            // Optionally show/hide a progress indicator here
        });

        // Observe error messages to display in Snackbar
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            Log.d(TAG, "observeViewModel: errorMessage LiveData changed. Value: " + errorMessage);
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.d(TAG, "observeViewModel: Displaying Snackbar with message: " + errorMessage);
                Snackbar.make(requireActivity().findViewById(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG)
                        .addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                // Clear the message only if it was dismissed by timeout or user action, not by a new message
                                if (event != DISMISS_EVENT_SWIPE && event != DISMISS_EVENT_ACTION) {
//                                    viewModel.clearErrorMessage(); // Clear the message after Snackbar is dismissed
                                    Log.d(TAG, "observeViewModel: Snackbar dismissed, clearing error message.");
                                }
                            }
                        })
                        .show();
            } else {
                Log.d(TAG, "observeViewModel: errorMessage is null or empty, not showing Snackbar.");
            }
        });

        // Observe save success to navigate back
        viewModel.getIsSaveSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            Log.d(TAG, "observeViewModel: isSaveSuccess LiveData changed. Value: " + isSuccess);
            if (Boolean.TRUE.equals(isSuccess)) {
                Log.d(TAG, "observeViewModel: Data save successful, navigating back.");
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Данные успешно обновлены!", Snackbar.LENGTH_SHORT).show();
                navController.navigate(R.id.action_editProfileFragment_to_profileFragment);
                viewModel.resetSaveSuccess(); // Reset success state after navigation
            }
        });
    }

    private void setupListeners() {
        Log.d(TAG, "setupListeners: Setting up button click listener.");
        binding.buttonConfirmChanges.setOnClickListener(v -> {
            String newName = binding.inputName.getText().toString().trim();
            String newPhoneNumber = binding.inputPhone.getText().toString().trim();
            Log.d(TAG, "setupListeners: Confirm changes button clicked. New Name: '" + newName + "', New Phone: '" + newPhoneNumber + "'");
            viewModel.saveChanges(newName, newPhoneNumber);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
        Log.d(TAG, "onDestroyView: Binding nulled.");
    }
}
