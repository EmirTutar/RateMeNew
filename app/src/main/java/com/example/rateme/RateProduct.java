package com.example.rateme;

import com.example.rateme.UserModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.rateme.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateProduct extends AppCompatActivity {
    private CommentsAdapter commentsAdapter;
    private RatingManager ratingManager;
    private String currentProductTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_product);

        ratingManager = new RatingManager();

        currentProductTitle = getIntent().getStringExtra("PRODUCT_TITLE");

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

        commentsAdapter = new CommentsAdapter(new ArrayList<>(), currentProductTitle);
        RecyclerView commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        EditText commentEditText = findViewById(R.id.commentEditText);
        Button submitCommentButton = findViewById(R.id.submitCommentButton);

        //CommentsAdapter commentsAdapter = new CommentsAdapter(new ArrayList<>());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);

        loadComments();

        submitCommentButton.setOnClickListener(view -> {
            String commentText = commentEditText.getText().toString();
            if (!commentText.isEmpty()) {
                submitComment(commentText);
            }
        });
    }

    private void loadComments() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Comment> comments = new ArrayList<>();

        db.collection("ProductRatings").document(currentProductTitle).collection("Comments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String userName = document.getString("userName");
                            String text = document.getString("text");
                            String userEmail = document.getString("userEmail"); // E-Mail hinzugefügt

                            Comment comment = new Comment(id, userName, text, userEmail); // E-Mail-Parameter hinzufügen
                            comments.add(comment);
                        }
                        commentsAdapter.setComments(comments);
                    } else {
                        Log.d("RateProduct", "Error getting comments: ", task.getException());
                    }
                });
    }


    private void submitComment(String commentText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference userRef = db.collection("User").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("username");

                Map<String, Object> comment = new HashMap<>();
                comment.put("userName", userName);
                comment.put("userEmail", userEmail); // E-Mail zum Kommentar hinzufügen
                comment.put("text", commentText);

                db.collection("ProductRatings").document(currentProductTitle).collection("Comments")
                        .add(comment)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show();
                            loadComments(); // Kommentare neu laden
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error adding comment", Toast.LENGTH_SHORT).show();
                            Log.w("RateProduct", "Error adding comment", e);
                        });
            } else {
                Log.d("RateProduct", "Document does not exist");
            }
        }).addOnFailureListener(e -> {
            Log.w("RateProduct", "Error getting document", e);
        });
    }
}
