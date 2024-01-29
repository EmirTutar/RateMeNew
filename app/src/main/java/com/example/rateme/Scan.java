package com.example.rateme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rateme.databinding.ScanBinding;
import com.google.zxing.integration.android.IntentIntegrator;

public class Scan extends Fragment {
    private ScanBinding binding;
    private TextView productDetails;
    private RatingManager ratingManager;
    private String currentProductTitle = "";
    public static MutableLiveData<String> productDetailsLiveData = new MutableLiveData<>();

    private BroadcastReceiver ratingUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.rateme.RATING_UPDATED".equals(intent.getAction())) {
                String updatedProductTitle = intent.getStringExtra("productTitle");
                if (updatedProductTitle != null && updatedProductTitle.equals(currentProductTitle)) {
                    updateRatingsView(currentProductTitle);
                }
            }
        }
    };
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Registrieren des BroadcastReceivers
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(ratingUpdateReceiver,
                new IntentFilter("com.example.rateme.RATING_UPDATED"));

        binding = ScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        productDetails = root.findViewById(R.id.product_details);
        ratingManager = new RatingManager();

        Button btnRateProduct = root.findViewById(R.id.btnRateProduct);
        btnRateProduct.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), RateProduct.class);
            intent.putExtra("PRODUCT_TITLE", currentProductTitle); // Produkttitel übergeben
            startActivity(intent);
        });

        productDetailsLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                productDetails.setText(s);
                currentProductTitle = extractTitle(s); // Extrahiert den Titel
                updateRatingsView(currentProductTitle);

                if (!currentProductTitle.isEmpty() && !currentProductTitle.equals("Scan a Product to get more Details") && !currentProductTitle.equals("This Barcode is not available")) {
                    btnRateProduct.setVisibility(View.VISIBLE);
                    updateRatingsView(currentProductTitle);
                } else {
                    btnRateProduct.setVisibility(View.GONE);
                    RatingBar ratingBarShowRating = binding.getRoot().findViewById(R.id.RatingBarShowRating);
                    ratingBarShowRating.setRating(0);
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
                    Toast.makeText(getContext(), "Product already in Favourites", Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.favouriteProductDetails.add(productDetails.getText().toString());
                    Toast.makeText(getContext(), "Product added to Favourites", Toast.LENGTH_SHORT).show();
                    Favourites.updateFavouritesList();
                }
            }
        });

        return root;
    }

    private String extractTitle(String details) {
        if (details.contains("Title: ")) {
            int startIndex = details.indexOf("Title: ") + "Title: ".length();
            int endIndex = details.indexOf("\n", startIndex);
            return details.substring(startIndex, endIndex);
        }
        return "";
    }

    private void updateRatingsView(String productTitle) {
        RatingBar ratingBarShowRating = binding.getRoot().findViewById(R.id.RatingBarShowRating);
        if (!productTitle.isEmpty()) {
            ratingManager.getAverageRatingFromFirebase(productTitle, ratingBarShowRating);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Deregistrieren des Receivers
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(ratingUpdateReceiver);
        binding = null;

    }
}
