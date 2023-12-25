package com.example.rateme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rateme.databinding.ScanBinding;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.List;
public class Scan extends Fragment {

    private ScanBinding binding;
    private static final MutableLiveData<String> mText = new MutableLiveData<>();

    private RecyclerView recyclerView;
    private ApiAdapter adapter;
    private List<String> productList = new ArrayList<>();

    public Scan() {
        mText.setValue("Scan");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.scan, container, false);

        // Der Scanner-Code aus der MainActivity
        Button scanButton = root.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(requireActivity()).initiateScan();
            }
        });

        recyclerView = root.findViewById(R.id.dataView);
        adapter = new ApiAdapter(productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        mText.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // Aktualisieren Sie hier Ihre Produktliste und benachrichtigen Sie den Adapter
                productList.add(s);
                adapter.notifyDataSetChanged();
            }
        });
        return root;
    }
    public static void fetchProductDetails(String barcode) {
        ApiRequest.initiateApiRequest(barcode, mText);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

