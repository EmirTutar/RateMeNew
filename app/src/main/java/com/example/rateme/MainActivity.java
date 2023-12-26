package com.example.rateme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rateme.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<String> scannedProductDetails = new ArrayList<>();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    FirebaseFirestore firebaseFirestore;
    TextView currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseFirestore=FirebaseFirestore.getInstance();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentUser=findViewById(R.id.currentUser);

        // Setup für die Navigation
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_scan).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        String userId = FirebaseAuth.getInstance().getUid();
        // Pfad zur Benutzerdokument in Firestore
        DocumentReference documentReferenceRef = firebaseFirestore.collection("User").document(userId);

        // Daten aus Firestore abrufen
        documentReferenceRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    currentUser.setText(value.getString("username"));
                }
                else{
                    Log.d("Tag", "onEvent: Document do not exist");
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    String scannedData = result.getContents();
                    Toast.makeText(this, "Gescannter Barcode: " + scannedData, Toast.LENGTH_LONG).show();
                    initiateApiRequest(scannedData);
                } else {
                    Toast.makeText(this, "Scan abgebrochen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void initiateApiRequest(String barcode) {
        ApiRequest.initiateApiRequest(barcode, new ApiRequest.ApiCallback() {
            @Override
            public void onResultReceived(String result) {
                if (result != null) {
                    scannedProductDetails.add(0, result); // Fügt die neuen Daten am Anfang der Liste hinzu
                    Scan.productDetailsLiveData.postValue(result);
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    public void logout (View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Welcome.class));
        finish();
    }
}
