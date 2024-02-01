package RateMe.ScanActivity.Scan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rateme.R;
import com.example.rateme.databinding.ScanBinding;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.List;

import RateMe.FavouritesActivity.Favourites_Fragment;
import RateMe.MainActivity.MainActivity;
import RateMe.ScanActivity.RateProductActivity.Rate.RateProduct;
import RateMe.ScanActivity.RateProductActivity.Rate.RatingManager;

/**
 * Scan_Fragment ist verantwortlich für die Scan-Funktionalität der App.
 * Diese Klasse ist verantwortlich für das Handling des Scan-Vorgangs,
 * Anzeigen von Produktdetails und Bildern und Interaktion mit anderen
 * Teilen der App wie Favoriten und Produktbewertungen.
 *
 * Die Klasse nutzt MutableLiveData, um auf Änderungen der Produktdaten
 * zu reagieren und aktualisiert die UI entsprechend. Sie kommuniziert
 * mit verschiedenen Managern, um Aufgaben wie Bildladen und Datenmanagement
 * zu verwalten.
 */

public class Scan_Fragment extends Fragment {
    private ScanBinding binding;
    private ScanUIManager uiManager;
    private ScanDataManager dataManager;
    private ImageLoader imageLoader;
    private RatingManager ratingManager;
    private String currentProductTitle = "";
    public static MutableLiveData<String> productDetailsLiveData = new MutableLiveData<>();
    public static MutableLiveData<List<String>> productImagesLiveData = new MutableLiveData<>();

    private BroadcastReceiver ratingUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.rateme.RATING_UPDATED".equals(intent.getAction())) {
                String updatedProductTitle = intent.getStringExtra("productTitle");
                if (updatedProductTitle != null && updatedProductTitle.equals(currentProductTitle)) {
                    RatingBar ratingBarShowRating = binding.getRoot().findViewById(R.id.RatingBarShowRating);
                    ratingManager.getAverageRating(currentProductTitle, ratingBarShowRating);
                }
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupUIManager();
        setupDataManager();
        setupImageLoader();
        setupRatingManager();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(ratingUpdateReceiver, new IntentFilter("com.example.rateme.RATING_UPDATED"));

        setupButtons();

        productImagesLiveData.observe(getViewLifecycleOwner(), this::handleImageUrlsChange);
        productDetailsLiveData.observe(getViewLifecycleOwner(), this::handleProductDetailsChange);

        return root;
    }

    private void setupUIManager() {
        uiManager = new ScanUIManager(binding.productDetails, binding.productImageView, binding.buttonAddToFavourites, binding.noImageTextView, binding.RatingBarShowRating, binding.progressBar, binding.progressBar2);
    }

    private void setupDataManager() {
        dataManager = new ScanDataManager();
    }

    private void setupImageLoader() {
        imageLoader = new ImageLoader(getContext());
    }

    private void setupRatingManager() {
        ratingManager = new RatingManager();
    }

    private void setupButtons() {
        binding.btnRateProduct.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), RateProduct.class);
            intent.putExtra("PRODUCT_TITLE", currentProductTitle);
            startActivity(intent);
        });

        binding.scanButton.setOnClickListener(view -> {
            uiManager.setProductDetailsText("Wait for Response...");
            uiManager.setProgressBarVisibility(true);
            new IntentIntegrator(requireActivity()).initiateScan();
        });

        binding.buttonAddToFavourites.setOnClickListener(view -> handleAddToFavourites());
    }

    private void handleImageUrlsChange(List<String> imageUrls) {
        if (!imageUrls.isEmpty()) {
            imageLoader.loadImageIntoView(imageUrls.get(0), binding.productImageView);
            uiManager.setProductImageViewVisibility(true);
        } else {
            uiManager.setProductImageViewVisibility(false);
        }
    }

    private void handleProductDetailsChange(String details) {
        uiManager.setProductDetailsText(details);
        uiManager.setProgressBarVisibility(false);
        currentProductTitle = dataManager.extractTitle(details);

        if (!currentProductTitle.isEmpty() && !currentProductTitle.equals("Scan a Product to get more Details") && !currentProductTitle.equals("This Barcode is not available")) {
            binding.btnRateProduct.setVisibility(View.VISIBLE);
            RatingBar ratingBarShowRating = binding.getRoot().findViewById(R.id.RatingBarShowRating);
            ratingManager.getAverageRating(currentProductTitle, ratingBarShowRating);
        } else {
            binding.btnRateProduct.setVisibility(View.GONE);
            RatingBar ratingBarShowRating = binding.getRoot().findViewById(R.id.RatingBarShowRating);
            ratingBarShowRating.setRating(0);
        }
    }


    private void handleAddToFavourites() {
        if (MainActivity.favouriteProductDetails.contains(currentProductTitle)) {
            Toast.makeText(getContext(), "Product already in Favourites", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity.favouriteProductDetails.add(currentProductTitle);
            Toast.makeText(getContext(), "Product added to Favourites", Toast.LENGTH_SHORT).show();
            Favourites_Fragment.updateFavouritesList();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(ratingUpdateReceiver);
        binding = null;
    }
}
