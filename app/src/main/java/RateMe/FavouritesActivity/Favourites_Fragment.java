package RateMe.FavouritesActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import RateMe.MainActivity.MainActivity;
import com.example.rateme.R;
import com.example.rateme.databinding.FavouritesBinding;

/**
 * Favourites ist ein Fragment, das die Lieblingsprodukte des Benutzers anzeigt.
 * Es verwendet einen RecyclerView, um die Produktdetails in einer Liste darzustellen.
 * Benutzer können Produkte zu ihren Favoriten hinzufügen und aus dieser Liste entfernen.
 */

public class Favourites_Fragment extends Fragment {

    private static FavouritesAdapter adapter;
    private FavouritesBinding binding;
    private final MutableLiveData<String> mText;

    public Favourites_Fragment() {
        mText = new MutableLiveData<>();
        mText.setValue("Favourites");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textFavourites;
        mText.observe(getViewLifecycleOwner(), textView::setText);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewFavourites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FavouritesAdapter adapter = new FavouritesAdapter(MainActivity.favouriteProductDetails);
        recyclerView.setAdapter(adapter);


        return root;
    }
    public static void updateFavouritesList() {
        if (adapter != null) {
            adapter.updateFavouritesList(MainActivity.favouriteProductDetails);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
