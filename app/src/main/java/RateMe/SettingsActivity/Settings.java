package RateMe.SettingsActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.rateme.R;
import com.example.rateme.databinding.SettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Settings extends Fragment {

    private SettingsBinding binding;
    private final MutableLiveData<String> mText;
    TextView name, email;

    public Settings() {
        mText = new MutableLiveData<>();
        mText.setValue("Settings");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton logoutButton = getActivity().findViewById(R.id.logout);
        logoutButton.setVisibility(View.VISIBLE);

        final TextView username = binding.currentUserNameTextView;
        final TextView email = binding.currentUserEmailTextView;
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();
        // Pfad zur Benutzerdokument in Firestore
        DocumentReference documentReferenceRef = firebaseFirestore.collection("User").document(userId);

        // Daten aus Firestore abrufen
        documentReferenceRef.addSnapshotListener((DocumentSnapshot value, FirebaseFirestoreException error) -> {
            if (value != null && value.exists()) {
                username.setText(value.getString("username"));
                email.setText(value.getString("email"));
            } else {
                Log.d("Tag", "onEvent: Document does not exist");
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
