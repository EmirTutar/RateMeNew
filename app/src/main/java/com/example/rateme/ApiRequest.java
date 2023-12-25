package com.example.rateme;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiRequest {

    public static void initiateApiRequest(String eanCode, MutableLiveData<String> resultLiveData) {
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
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d("ApiResponse", "API Response: " + result);
                processApiResponse(result, resultLiveData);
            } catch (IOException e) {
                e.printStackTrace();
                postToLiveData(resultLiveData, null);
            }
        }).start();
    }

    private static String extractAllAttributes(JSONObject jsonObject) {
        StringBuilder result = new StringBuilder();

        try {
            if (jsonObject.has("attributes")) {
                JSONObject attributesObject = jsonObject.getJSONObject("attributes");

                Iterator<String> keys = attributesObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = attributesObject.getString(key);

                    result.append(key).append(": ").append(value).append("\n");
                    Log.d("ApiResponse", "Extracted Attribute: " + key + ": " + value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private static void processApiResponse(String result, MutableLiveData<String> resultLiveData) {
        Log.d("ApiResponse", "Raw Response: " + result);

        try {
            JSONObject jsonObject = new JSONObject(result);

            if (jsonObject.has("product")) {
                JSONObject productObject = jsonObject.getJSONObject("product");
                String attributes = extractAllAttributes(productObject);
                postToLiveData(resultLiveData, attributes);
            } else {
                Log.d("ApiResponse", "No 'product' object in API response");
                resultLiveData.postValue(null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ApiResponse", "Error parsing API Response: " + e.getMessage());
            resultLiveData.postValue(null);
        }
    }

    private static void postToLiveData(MutableLiveData<String> liveData, String value) {
        new Handler(Looper.getMainLooper()).post(() -> liveData.setValue(value));
    }
}