package RateMe.ScanActivity.Scan;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * ScanUIManager verwaltet die Benutzeroberfläche des Scan-Fragments.
 * Die Klasse ist verantwortlich für die Aktualisierung der Ansichten
 * wie Produktdetails, Produktbilder und Ladeanzeigen.
 */
public class ScanUIManager {
    private TextView productDetails;
    private ImageView productImageView;
    private ImageButton addToFavouritesButton;
    private TextView noImageTextView;
    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private ProgressBar progressBar2;

    public ScanUIManager(TextView productDetails, ImageView productImageView, ImageButton addToFavouritesButton, TextView noImageTextView, RatingBar ratingBar, ProgressBar progressBar, ProgressBar progressBar2) {
        this.productDetails = productDetails;
        this.productImageView = productImageView;
        this.addToFavouritesButton = addToFavouritesButton;
        this.noImageTextView = noImageTextView;
        this.ratingBar = ratingBar;
        this.progressBar = progressBar;
        this.progressBar2 = progressBar2;
    }

    public void setProductDetailsText(String text) {
        productDetails.setText(text);
    }

    public void setRatingBarRating(float rating) {
        ratingBar.setRating(rating);
    }

    public void setProgressBarVisibility(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        progressBar.setVisibility(visibility);
        progressBar2.setVisibility(visibility);
    }

    public void setProductImageViewVisibility(boolean hasImage) {
        if (hasImage) {
            productImageView.setVisibility(View.VISIBLE);
            addToFavouritesButton.setVisibility(View.VISIBLE);
            noImageTextView.setVisibility(View.GONE);
        } else {
            productImageView.setVisibility(View.GONE);
            addToFavouritesButton.setVisibility(View.GONE);
            noImageTextView.setVisibility(View.VISIBLE);
        }
    }
}
