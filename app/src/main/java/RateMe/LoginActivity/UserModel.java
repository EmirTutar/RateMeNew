package RateMe.LoginActivity;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


/**
 * Das UserModel repräsentiert die Struktur eines Benutzerobjekts.
 * Es beinhaltet Eigenschaften wie Benutzername und E-Mail, die für die Verwaltung von Benutzerkonten verwendet werden.
 * Diese Klasse wird hauptsächlich für die Speicherung und Abfrage von Benutzerdaten in Firebase Firestore verwendet.
 */

public class UserModel {

    String username, email;

    public UserModel(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
