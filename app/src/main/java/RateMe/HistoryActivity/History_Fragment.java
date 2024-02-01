package RateMe.HistoryActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rateme.R;
import com.example.rateme.databinding.ActivityHistoryBinding;

import java.util.ArrayList;
import java.util.List;

import RateMe.MainActivity.MainActivity;

/**
 * History ist ein Fragment, das die Scanhistory des Benutzers anzeigt.
 * Es zeigt eine Liste der gescannten Produkte an, wobei jedes Produkt in einem RecyclerView dargestellt wird.
 * Die Liste wird aus den in der MainActivity gespeicherten gescannten Produktdetails generiert.
 */

public class History_Fragment extends Fragment {

    private ActivityHistoryBinding binding;
    private HistoryAdapter adapter;
    private List<String> historyList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = ActivityHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.history_recycler_view);
        adapter = new HistoryAdapter(MainActivity.scannedProductDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
