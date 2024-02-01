package RateMe.SettingsActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rateme.R;

/**
 * Einstiegspunkt für die Einstellungen der App. Erlaubt den Zugriff auf Unterseiten wie
 * Profil, Berechtigungen und Über Uns.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_fragment_container, new SettingsFragment())
                    .commit();
        }
    }
}
