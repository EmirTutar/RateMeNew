package RateMe.ScanActivity.RateProductActivity.Comment;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Der "CommentManager" verwaltet das Laden, Hinzufügen und Löschen von Kommentaren zu einem spezifischen Produkt.
 * Kommentare werden in der Firebase Firestore-Datenbank gespeichert und abgerufen.
 * Diese Klasse sorgt dafür, dass Benutzer mit den Kommentaren interagieren können.
 * @noinspection DataFlowIssue
 */

public class CommentManager {
    private FirebaseFirestore db;
    private String currentProductTitle;
    private Context context;

    public CommentManager(String currentProductTitle, Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.currentProductTitle = currentProductTitle;
        this.context = context;
    }

    public void loadComments(CommentsAdapter commentsAdapter) {
        List<Comment> comments = new ArrayList<>();

        db.collection("ProductRatings").document(currentProductTitle).collection("Comments")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String userName = document.getString("userName");
                            String text = document.getString("text");
                            String userEmail = document.getString("userEmail");
                            String profilePicUrl = document.getString("profilePicUrl");

                            Comment comment = new Comment(id, userName, text, userEmail, profilePicUrl);
                            comments.add(comment);
                        }
                        commentsAdapter.setComments(comments);
                    } else {
                        Log.d("CommentManager", "Error getting comments: ", task.getException());
                    }
                });
    }

    public void submitComment(String commentText, CommentsAdapter commentsAdapter) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();
        //noinspection DataFlowIssue
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String profilePicUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

        //noinspection DataFlowIssue
        DocumentReference userRef = db.collection("User").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("username");

                Map<String, Object> comment = new HashMap<>();
                comment.put("userName", userName);
                comment.put("userEmail", userEmail);
                comment.put("text", commentText);
                comment.put("profilePicUrl", profilePicUrl);

                db.collection("ProductRatings").document(currentProductTitle).collection("Comments")
                        .add(comment)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("CommentManager", "Comment added");
                            Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show();
                            loadComments(commentsAdapter); // Kommentare neu laden
                        })
                        .addOnFailureListener(e -> Log.w("CommentManager", "Error adding comment", e));
            } else {
                Log.d("CommentManager", "Document does not exist");
            }
        }).addOnFailureListener(e -> Log.w("CommentManager", "Error getting document", e));
    }
}
