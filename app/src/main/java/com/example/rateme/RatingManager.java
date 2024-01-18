package com.example.rateme;

import android.util.Log;
import android.widget.RatingBar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class RatingManager {
    private FirebaseFirestore db;

    public RatingManager() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveRatingToFirebase(String productTitle, float rating) {
        CollectionReference ratingsRef = db.collection("ProductRatings").document(productTitle).collection("Ratings");
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);
        ratingsRef.add(ratingData);
    }

    public void getAverageRatingFromFirebase(String productTitle, RatingBar ratingBar) {
        CollectionReference ratingsRef = db.collection("ProductRatings").document(productTitle).collection("Ratings");

        ratingsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                float total = 0;
                int count = 0;

                for (QueryDocumentSnapshot document : querySnapshot) {
                    Number rating = (Number) document.getData().get("rating");
                    if (rating != null) {
                        total += rating.floatValue();
                        count++;
                    }
                }

                float average = count > 0 ? total / count : 0;
                ratingBar.setRating(average);
            } else {
                Log.w("Firestore", "Error getting ratings.", task.getException());
            }
        });
    }
}
