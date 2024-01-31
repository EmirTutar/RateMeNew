package RateMe.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rateme.R;
import com.example.rateme.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import RateMe.LoginActivity.Welcome;
import RateMe.MainActivity.Barcode.BarcodeScanner;
import RateMe.MainActivity.Navigation.NavigationManager;
import RateMe.ScanActivity.Scan.Scan_Fragment;

/**
 * MainActivity ist die Hauptaktivität der Anwendung.
 * Sie beinhaltet die Navigation zwischen verschiedenen Fragmenten und verwaltet die Hauptfunktionen der App.
 * Die Klasse ist verantwortlich für das ausführen des Barcode-Scans und die Verarbeitung der Scan-Ergebnisse.
 * Zudem werden die Daten für die History und die Favoritenliste hier verwaltet.
 */

public class MainActivity extends AppCompatActivity implements BarcodeScanner.OnBarcodeScanResultListener{

    public static List<String> scannedProductDetails = new ArrayList<>();
    public static List<String> favouriteProductDetails = new ArrayList<>();
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private BarcodeScanner barcodeScanner;
    private NavigationManager navigationManager;

    public void onBarcodeScanResult(String result) {
        // Verarbeiten des Scanergebnisses
        updateScannedProductDetails(result);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup für die Navigation
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_scan).build();
        NavigationUI.setupWithNavController(binding.navView, navController);

        barcodeScanner = new BarcodeScanner(this, this);
        navigationManager = new NavigationManager(navController, findViewById(R.id.logout));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                String scannedData = result.getContents();
                ApiRequestHandler.initiateApiRequest(scannedData, apiResult -> {
                    // Verarbeiten Sie hier das Ergebnis
                    if (!apiResult.equals("This Barcode is not available") && !apiResult.equals("Error parsing API response")) {
                        updateScannedProductDetails(apiResult);
                    }
                });
            } else {
                Toast.makeText(this, "Scan abgebrochen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateScannedProductDetails(String result) {
        if (scannedProductDetails.contains(result)) {
            scannedProductDetails.remove(result);
        }
        scannedProductDetails.add(0, result);
        Scan_Fragment.productDetailsLiveData.postValue(result);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Welcome.class));
        finish();
    }
}
