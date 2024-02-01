package RateMe.HistoryActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rateme.R;

import java.util.List;

/**
 * HistoryAdapter ist ein RecyclerView-Adapter, der für die Darstellung der gescannten Produkte
 * in der History-Liste verantwortlich ist. Er verwaltet eine Liste von Produkt-Strings und stellt
 * diese in der RecyclerView dar. Der Adapter ermöglicht es auch, Produkte aus der Liste zu entfernen.
 */


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<String> productList;

    public HistoryAdapter(List<String> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String product = productList.get(position);
        holder.productTextView.setText(product);

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                productList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, productList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productTextView;
        public ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            productTextView = itemView.findViewById(R.id.text_product);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
