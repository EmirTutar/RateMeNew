package com.example.rateme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.rateme.databinding.ScanBinding;
import com.google.zxing.integration.android.IntentIntegrator;

public class Scan extends Fragment {

    private ScanBinding binding;
    private final MutableLiveData<String> mText;

    public Scan() {
        mText = new MutableLiveData<>();
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

