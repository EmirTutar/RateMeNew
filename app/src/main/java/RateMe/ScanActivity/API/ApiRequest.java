package RateMe.ScanActivity.API;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import RateMe.ScanActivity.Scan.Scan_Fragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Die Klasse ApiRequest ist zuständig für die Initiierung und Bearbeitung von API-Anfragen an einen Barcode-Suchdienst.
 * Sie führt HTTP-Anfragen durch, um Produktinformationen basierend auf einem EAN-Code abzurufen. Diese Klasse verwendet
 * den OkHttp-Client für Netzwerkanfragen und verarbeitet die JSON-Antwort, um notwendige Produktdetails und Bild-URLs zu extrahieren.
 * <p>
 * Verwendung:
 * - Um diese Klasse zu nutzen, ruft man`initiateApiRequest` mit einem EAN-Code und einem Callback auf.
 * - Der Callback erhält die formatierten Produktdetails als String und eine Liste von Bild-URLs, falls das Produkt gefunden wurde.
 * - Wenn das Produkt nicht gefunden wird oder ein Fehler in der API-Antwort vorliegt, erhält der Callback eine entsprechende Fehlermeldung.
 * <p>
 * Die Klasse ermöglicht es:
 * - Asynchrone API-Anfragen auf einem separaten Thread durchzuführen, um das Blockieren des UI-Threads zu vermeiden.
 * - Produktdetails und Bild-URLs aus der JSON-Antwort zu extrahieren.
 * - Die Ergebnisse über LiveData im Scan_Fragment zurück an die UI zu posten.
 */

public class ApiRequest {

    public interface ApiCallback {
        void onResultReceived(String result);
    }

    public static void initiateApiRequest(String eanCode, ApiCallback callback) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://barcodes1.p.rapidapi.com/?query=" + eanCode)
                    .get()
                    .addHeader("X-RapidAPI-Key", "a4cbe66b3fmshebde3ae08a8153cp1564a1jsn8cf98436363e")
                    .addHeader("X-RapidAPI-Host", "barcodes1.p.rapidapi.com")
                    .build();

            Log.d("ApiRequest", "API Request URL: " + request.url());

            try {
                //noinspection resource
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("ApiResponse", "API Response: " + result);
                processApiResponse(result, callback);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void processApiResponse(String result, ApiCallback callback) {
        Log.d("ApiResponse", "Raw Response: " + result);
        try {
            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.has("product")) {
                JSONObject productObject = jsonObject.getJSONObject("product");

                String productDetails = extractRequiredAttributes(productObject);
                List<String> imageUrls = extractImageUrls(productObject);
                callback.onResultReceived(productDetails);
                Scan_Fragment.productDetailsLiveData.postValue(productDetails);
                Scan_Fragment.productImagesLiveData.postValue(imageUrls);
            } else {
                Log.d("ApiResponse", "Barcode not found in database");
                callback.onResultReceived("This Barcode is not available");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ApiResponse", "Error parsing API Response: " + e.getMessage());
            callback.onResultReceived("Error parsing API response, it looks like the API is down. You can try again later.");
        }
    }

    private static String extractRequiredAttributes(JSONObject jsonObject) {
        StringBuilder result = new StringBuilder();
        try {

            if (jsonObject.has("title")) {
                result.append("Title: ").append(jsonObject.getString("title")).append("\n");
                Log.d("ApiResponse", "Extracted Attribute: " + jsonObject.getString("title"));
            }

            if (jsonObject.has("barcode_formats")) {
                JSONObject barcodeObject = jsonObject.getJSONObject("barcode_formats");

                Iterator<String> keys = barcodeObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = barcodeObject.getString(key);
                    result.append("Barcode: ").append(value).append("\n");
                    Log.d("ApiResponse", "Extracted barcode_formats: " + key + ": " + value);
                }
            }

            if (jsonObject.has("brand")) {
                if (jsonObject.getString("brand").equals("null")) {
                    if (jsonObject.has("manufacturer")) {
                        if (jsonObject.getString("manufacturer").equals("null")) {
                            result.append("").append("").append("\n");
                            Log.d("ApiResponse", "Extracted Attribute: " + "No manufacturer");
                        } else {
                            result.append("Manufacturer: ").append(jsonObject.getString("manufacturer")).append("\n");
                            Log.d("ApiResponse", "Extracted Attribute: " + jsonObject.getString("manufacturer"));
                        }
                    }

                } else {
                    result.append("Brand: ").append(jsonObject.getString("brand")).append("\n");
                    Log.d("ApiResponse", "Extracted Attribute: " + jsonObject.getString("brand"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private static List<String> extractImageUrls(JSONObject jsonObject) {
        List<String> imageUrls = new ArrayList<>();
        try {
            if (jsonObject.has("images")) {
                JSONArray imagesArray = jsonObject.getJSONArray("images");
                for (int i = 0; i < imagesArray.length(); i++) {
                    imageUrls.add(imagesArray.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrls;
    }

}