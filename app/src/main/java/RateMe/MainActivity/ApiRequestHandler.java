package RateMe.MainActivity;

import RateMe.ScanActivity.API.ApiRequest;
import RateMe.ScanActivity.Scan.Scan_Fragment;

/**
 * ApiRequestHandler ist zuständig für das Senden von Anfragen an die externe Barcode-API.
 * Die Klasse verarbeitet die API-Antwort und gibt die Daten an einen Callback weiter, um sie in der App zu verwenden.
 */

public class ApiRequestHandler {

    public interface ApiResultCallback {
        void onApiResultReceived(String result);
    }

    public static void initiateApiRequest(String barcode, ApiResultCallback callback) {
        ApiRequest.initiateApiRequest(barcode, result -> {
            Scan_Fragment.productDetailsLiveData.postValue(result);
            callback.onApiResultReceived(result);
        });

    }
}
