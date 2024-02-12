package RateMe.SettingsActivity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.storage.StorageTask;

import RateMe.LoginActivity.Login;
import RateMe.LoginActivity.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;




public class EditProfile extends AppCompatActivity {

    private CircleImageView profilImgaeView;
    private Button closeButton, saveButton;
    private TextView profileChangeBtn;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private String myUri="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;
    private ImageView profilePicture;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        profilImgaeView = findViewById(R.id.profile_image);
        closeButton = findViewById(R.id.button_close_picture);
        saveButton = findViewById(R.id.button_save_picture);
        profilePicture = findViewById(R.id.profile_image);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_picture").child(userId);
        storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri uri = task.getResult();
                UserModel.setProfilePic(getBaseContext(), uri, profilePicture);
            }
        });

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            UserModel.setProfilePic(getBaseContext(),selectedImageUri,profilImgaeView);
                        }
                    }
                }
        );
        profilImgaeView.setOnClickListener((v)->{
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });

        saveButton.setOnClickListener(v -> {
            if(selectedImageUri!=null){
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