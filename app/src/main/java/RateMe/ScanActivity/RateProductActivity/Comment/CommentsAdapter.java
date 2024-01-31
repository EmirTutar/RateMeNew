package RateMe.ScanActivity.RateProductActivity.Comment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rateme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Der "CommentsAdapter" ist ein Adapter für ein RecyclerView, der die Darstellung von Kommentaren
 * in der RateProduct-Aktivität ermöglicht. Er bindet die Daten aus der Kommentarliste
 * an die Ansichten im RecyclerView. Der Adapter verwaltet auch die Darstellung von Kommentaren
 * und ermöglicht das Löschen von Kommentaren durch den Benutzer.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private List<Comment> commentsList;
    private String currentProductTitle;

    public CommentsAdapter(List<Comment> commentsList, String currentProductTitle) {
        this.currentProductTitle = currentProductTitle;
        this.commentsList = commentsList;
    }
    public void setComments(List<Comment> commentsList) {
        this.commentsList = commentsList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = commentsList.get(position);
        holder.commentTextView.setText(comment.getText());
        holder.userNameTextView.setText(comment.getUserName());

        // Überprüfen, ob der eingeloggte Benutzer der Autor des Kommentars ist
        if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(comment.getEmail())) {
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.deleteButton.setOnClickListener(v -> {
                deleteComment(comment, position);
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    private void deleteComment(Comment comment, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ProductRatings").document(currentProductTitle).collection("Comments")
                .document(comment.getId()) // Verwenden Sie die ID, um das Dokument zu identifizieren
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Kommentar erfolgreich gelöscht
                    commentsList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    // Fehlerbehandlung
                    Log.w("CommentsAdapter", "Error deleting comment", e);
                });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView commentTextView;
        public TextView userNameTextView;
        public View deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
