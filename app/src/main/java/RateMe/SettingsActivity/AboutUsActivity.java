package RateMe.SettingsActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rateme.R;

/**
 * Stellt Informationen über die App und der Entwickler bereit.
 */

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about_us);
    }
}
