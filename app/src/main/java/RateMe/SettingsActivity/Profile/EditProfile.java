package RateMe.SettingsActivity.Profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rateme.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import RateMe.LoginActivity.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * EditProfile ermöglicht das Bearbeiten des Profilbilds des Benutzers.
 * Benutzer können über ImagePicker ein Bild auswählen, es zuschneiden und auf Firebase Storage hochladen.
 * Anschließend wird die Bild-URL in der Firebase Database aktualisiert.
 * Die Klasse bietet eine UI mit Optionen zum Speichern oder Abbrechen der Änderungen.
 */

public class EditProfile extends AppCompatActivity {

    private CircleImageView profilImageView;
    private ImageView profilePicture;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_profile_edit_profile);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        StorageReference storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        profilImageView = findViewById(R.id.profile_image);
        Button closeButton = findViewById(R.id.button_close_picture);
        Button saveButton = findViewById(R.id.button_save_picture);
        profilePicture = findViewById(R.id.profile_image);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_picture").child(userId);
        storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri uri = task.getResult();
                UserModel.setProfilePic(getBaseContext(), uri, profilePicture);
            }
        });

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            UserModel.setProfilePic(getBaseContext(), selectedImageUri, profilImageView);
                        }
                    }
                }
        );
        profilImageView.setOnClickListener((v) -> ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));
        saveButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                storageReference.putFile(selectedImageUri)
                        .addOnCompleteListener(task -> {
                            profilePicture.setImageURI(selectedImageUri);

                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        });
            }
        });
        closeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        });
    }
}