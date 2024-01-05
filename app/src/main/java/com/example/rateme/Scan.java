package com.example.rateme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    public static MutableLiveData<String> productDetailsLiveData = new MutableLiveData<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {

        binding = ScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        productDetails = root.findViewById(R.id.product_details);

        productDetailsLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                productDetails.setText(s);
            }});

        // Der Scanner-Code aus der MainActivity
        Button scanButton = root.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDetails.setText(" Wait for Response...");
                new IntentIntegrator(requireActivity()).initiateScan();
            }
        });

        Button addToFavouritesButton = root.findViewById(R.id.button_add_to_favourites);
        addToFavouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.favouriteProductDetails.add(productDetails.getText().toString());
                Toast.makeText(getContext(), "Product added to Favourites", Toast.LENGTH_SHORT).show();
                Favourites.updateFavouritesList();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

