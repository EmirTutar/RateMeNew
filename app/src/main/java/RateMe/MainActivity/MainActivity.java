package RateMe.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rateme.R;
import RateMe.ScanActivity.API.ApiRequest;
import RateMe.LoginActivity.Welcome;
import RateMe.ScanActivity.Scan.Scan;
import com.example.rateme.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<String> scannedProductDetails = new ArrayList<>();
    public static List<String> favouriteProductDetails = new ArrayList<>();

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    TextView currentUserName, currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        currentUserName = findViewById(R.id.currentUserNameTextView);
        currentUserEmail = findViewById(R.id.currentUserEmailTextView);

        // Setup für die Navigation
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_scan).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setVisibility(View.INVISIBLE);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.navigation_settings) {
                    logoutButton.setVisibility(View.VISIBLE);
                } else {
                    logoutButton.setVisibility(View.INVISIBLE);
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
                if ((result != null) && !result.equals("This Barcode is not available") && !result.equals("Error parsing API response, it looks like the API is down. You can try again later.")) {
                    if (scannedProductDetails.contains(result)) {
                        scannedProductDetails.remove(result);
                    }
                    scannedProductDetails.add(0, result);
                    Scan.productDetailsLiveData.postValue(result);
                } else {
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
