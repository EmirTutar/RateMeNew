package RateMe.MainActivity.Navigation;

import android.view.View;
import android.widget.ImageButton;

import androidx.navigation.NavController;

import com.example.rateme.R;

/**
 * NavigationManager ist eine Hilfsklasse, die für die Navigation zwischen den verschiedenen Fragmenten der App zuständig ist.
 * Sie verwaltet die Interaktionen mit dem NavController und bietet eine zentrale Stelle zur Verwaltung der Navigationslogik.
 * Zusätzlich kümmert sich die Klasse um die Sichtbarkeit des Logout-Buttons basierend auf dem aktuellen Navigationskontext.
 */

public class NavigationManager {

    private final NavController navController;
    private final ImageButton logoutButton;

    public NavigationManager(NavController navController, ImageButton logoutButton) {
        this.navController = navController;
        this.logoutButton = logoutButton;
        setupNavigation();
    }

    private void setupNavigation() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_settings) {
                logoutButton.setVisibility(View.VISIBLE);
            } else {
                logoutButton.setVisibility(View.INVISIBLE);
            }
        });
    }
}
