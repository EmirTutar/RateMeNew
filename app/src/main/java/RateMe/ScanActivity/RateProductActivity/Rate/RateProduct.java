package RateMe.ScanActivity.RateProductActivity.Rate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rateme.R;

import java.util.ArrayList;

import RateMe.ScanActivity.RateProductActivity.Comment.CommentManager;
import RateMe.ScanActivity.RateProductActivity.Comment.CommentsAdapter;

/**
 * Die Aktivität "RateProduct" ermöglicht es Benutzern, Produkte zu bewerten und Kommentare abzugeben.
 * Diese Aktivität zeigt auch vorhandene Kommentare zu dem ausgewählten Produkt an.
 * Benutzer können ihre Bewertungen abgeben und Kommentare zu den Produkten hinzufügen,
 * die in der Firebase Firestore-Datenbank gespeichert werden.
 */

public class RateProduct extends AppCompatActivity {
    private CommentsAdapter commentsAdapter;

    private CommentManager commentManager;

    private RatingManager ratingManager;
    private String currentProductTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_product);

        ratingManager = new RatingManager();

        currentProductTitle = getIntent().getStringExtra("PRODUCT_TITLE");
        commentManager = new CommentManager(currentProductTitle, this);

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button submitButton = findViewById(R.id.btnSubmitRating);

        submitButton.setOnClickListener(view -> {
            float rating = ratingBar.getRating();
            if (!currentProductTitle.isEmpty()) {
                ratingManager.saveOrUpdateRating(currentProductTitle, rating, new RatingManager.RatingUpdateCallback() {
                    @Override
                    public void onRatingUpdated() {
                        Toast.makeText(RateProduct.this, "Rating submitted", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent("com.example.rateme.RATING_UPDATED");
                        intent.putExtra("productTitle", currentProductTitle);
                        LocalBroadcastManager.getInstance(RateProduct.this).sendBroadcast(intent);
                    }
                });
            } else {
                Toast.makeText(this, "No product title available", Toast.LENGTH_SHORT).show();
            }
        });

        commentsAdapter = new CommentsAdapter(new ArrayList<>(), currentProductTitle, this);
        RecyclerView commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        EditText commentEditText = findViewById(R.id.commentEditText);
        Button submitCommentButton = findViewById(R.id.submitCommentButton);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);

        commentManager.loadComments(commentsAdapter);

        submitCommentButton.setOnClickListener(view -> {
            String commentText = commentEditText.getText().toString();
            if (!commentText.isEmpty()) {
                commentManager.submitComment(commentText, commentsAdapter);
            }
        });
    }
}
