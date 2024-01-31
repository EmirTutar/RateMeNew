package RateMe.MainActivity.Navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;

import com.example.rateme.R;

/**
 * NavigationManager ist eine Hilfsklasse, die für die Navigation zwischen den verschiedenen Fragmenten der App zuständig ist.
 * Sie verwaltet die Interaktionen mit dem NavController und bietet eine zentrale Stelle zur Verwaltung der Navigationslogik.
 * Zusätzlich kümmert sich die Klasse um die Sichtbarkeit des Logout-Buttons basierend auf dem aktuellen Navigationskontext.
 */

public class NavigationManager {

    private final NavController navController;
    private final Button logoutButton;

    public NavigationManager(NavController navController, Button logoutButton) {
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
