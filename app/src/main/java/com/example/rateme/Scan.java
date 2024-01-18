package com.example.rateme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.rateme.databinding.ScanBinding;
import com.google.zxing.integration.android.IntentIntegrator;

public class Scan extends Fragment {
    private ScanBinding binding;
    private TextView productDetails;
    private RatingManager ratingManager;
    private String currentProductTitle = "";
    public static MutableLiveData<String> productDetailsLiveData = new MutableLiveData<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = ScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        productDetails = root.findViewById(R.id.product_details);
        ratingManager = new RatingManager();

        productDetailsLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                productDetails.setText(s);
                currentProductTitle = extractTitle(s); // Extrahiert den Titel
                updateRatingsView(currentProductTitle);
            }
        });

        RatingBar ratingBarRate = root.findViewById(R.id.ratingBarRate);
        ratingBarRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser && !currentProductTitle.isEmpty()) {
                    ratingManager.saveRatingToFirebase(currentProductTitle, rating);
                }
            }
        });

        Button scanButton = root.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDetails.setText("Wait for Response...");
                new IntentIntegrator(requireActivity()).initiateScan();
            }
        });

        ImageButton addToFavouritesButton = root.findViewById(R.id.button_add_to_favourites);
        addToFavouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.favouriteProductDetails.contains(productDetails.getText().toString())
                        || productDetails.getText().toString().equals("Scan a Product to get more Details")
                        || productDetails.getText().toString().equals("Wait for Response...")){
                    Toast.makeText(getContext(), "Product cannot be added to Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.favouriteProductDetails.add(productDetails.getText().toString());
                    Toast.makeText(getContext(), "Product added to Favourites", Toast.LENGTH_SHORT).show();
                    Favourites.updateFavouritesList();
                }
            }
        });

        return root;
    }

    // Extrahiert den Titel aus dem Detailstring
    private String extractTitle(String details) {
        if (details.contains("Title: ")) {
            int startIndex = details.indexOf("Title: ") + "Title: ".length();
            int endIndex = details.indexOf("\n", startIndex);
            return details.substring(startIndex, endIndex);
        }
        return "";
    }

    // Aktualisiert die Bewertungsansicht
    private void updateRatingsView(String productTitle) {
        RatingBar ratingBarShowRating = binding.getRoot().findViewById(R.id.RatingBarShowRating);
        if (!productTitle.isEmpty()) {
            ratingManager.getAverageRatingFromFirebase(productTitle, ratingBarShowRating);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
