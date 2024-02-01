package RateMe.SettingsActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.rateme.databinding.ActivitySettingsProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.rateme.databinding.ActivitySettingsProfileBinding binding = ActivitySettingsProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final TextView username = binding.currentUserNameTextView;
        final TextView email = binding.currentUserEmailTextView;
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();

        DocumentReference documentReferenceRef = firebaseFirestore.collection("User").document(userId);

        documentReferenceRef.addSnapshotListener((value, error) -> {
            if (value != null && value.exists()) {
                username.setText(value.getString("username"));
                email.setText(value.getString("email"));
            } else {
                Log.d("Tag", "Document does not exist");
            }
        });
    }
}
