package com.example.rateme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class RateProduct extends AppCompatActivity {
    private RatingManager ratingManager;
    private String currentProductTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_product);

        // Initialisieren des RatingManagers
        ratingManager = new RatingManager();

        // Erhalten des Produkttitels aus dem Intent
        currentProductTitle = getIntent().getStringExtra("PRODUCT_TITLE");

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button submitButton = findViewById(R.id.btnSubmitRating);

        submitButton.setOnClickListener(view -> {
            float rating = ratingBar.getRating();
            if (!currentProductTitle.isEmpty()) {
                ratingManager.saveOrUpdateRating(currentProductTitle, rating, new RatingManager.RatingUpdateCallback() {
                    @Override
                    public void onRatingUpdated() {
                        // Aktualisieren der Bewertungsansicht
                        Toast.makeText(RateProduct.this, "Rating submitted", Toast.LENGTH_SHORT).show();
                         finish(); // Schließen der Aktivität
                        Intent intent = new Intent("com.example.rateme.RATING_UPDATED");
                        intent.putExtra("productTitle", currentProductTitle);
                        LocalBroadcastManager.getInstance(RateProduct.this).sendBroadcast(intent);

                    }
                });
            } else {
                Toast.makeText(this, "No product title available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
