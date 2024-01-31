package RateMe.MainActivity.Barcode;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import RateMe.MainActivity.ApiRequestHandler;

/**
 * BarcodeScanner ist eine Hilfsklasse zur Handhabung von Barcode-Scans.
 * Sie verwendet die ZXing-Bibliothek, um den Scan-Prozess auszuführen und das Ergebnis zu erfassen.
 * Die Klasse leitet das Scan-Ergebnis an einen Listener weiter, der auf Scan-Ergebnisse reagiert.
 */

public class BarcodeScanner {

    private final AppCompatActivity activity;
    private final OnBarcodeScanResultListener scanResultListener;

    public BarcodeScanner(AppCompatActivity activity, OnBarcodeScanResultListener listener) {
        this.activity = activity;
        this.scanResultListener = listener;
    }

    public interface OnBarcodeScanResultListener {
        void onBarcodeScanResult(String result);
    }

    public void initiateScan() {
        new IntentIntegrator(activity).initiateScan();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            ApiRequestHandler.initiateApiRequest(result.getContents(), new ApiRequestHandler.ApiResultCallback() {
                @Override
                public void onApiResultReceived(String apiResult) {
                    scanResultListener.onBarcodeScanResult(apiResult);
                }
            });
        }
    }
}
