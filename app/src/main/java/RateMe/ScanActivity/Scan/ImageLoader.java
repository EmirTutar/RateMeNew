package RateMe.ScanActivity.Scan;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * ImageLoader ist spezialisiert auf das Laden und Anzeigen von Bildern
 * im Scan. Diese Klasse nutzt die Glide-Bibliothek, um Bilder zu laden.
 */

public class ImageLoader {
    private Context context;

    public ImageLoader(Context context) {
        this.context = context;
    }

    public void loadImageIntoView(String imageUrl, ImageView imageView) {
        Glide.with(context).load(imageUrl).into(imageView);
    }
}
