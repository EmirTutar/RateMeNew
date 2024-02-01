package RateMe.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rateme.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Die Welcome-Klasse dient als Startbildschirm der Anwendung, der dem Benutzer die Optionen zum Einloggen oder Registrieren bietet.
 * Sie leitet Benutzer je nach Auswahl zur Login- oder Registrierungsaktivität weiter.
 * Die Klasse prüft auch, ob bereits ein Benutzer eingeloggt ist, und leitet ihn entsprechend zur Hauptaktivität weiter.
 */

public class Welcome extends AppCompatActivity {

    Button login, signup;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity_welcome);

        login = findViewById(R.id.welcomeButtonLogin);
        signup = findViewById(R.id.welcomeButtonSignUp);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        signup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });
    }
}