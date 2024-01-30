package RateMe.HistoryActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import RateMe.MainActivity.MainActivity;
import com.example.rateme.R;
import com.example.rateme.databinding.HistoryBinding;

import java.util.ArrayList;
import java.util.List;
public class History extends Fragment {

    private HistoryBinding binding;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<String> historyList = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = HistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.history_recycler_view);
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
