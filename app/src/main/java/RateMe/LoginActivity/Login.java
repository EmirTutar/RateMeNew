package RateMe.LoginActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rateme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import RateMe.MainActivity.MainActivity;

/**
 * Die Login-Klasse ermöglicht es Benutzern, sich mit ihren Anmeldedaten einzuloggen.
 * Sie stellt Funktionen zum Einloggen, Registrieren eines neuen Kontos und Zurücksetzen des Passworts bereit.
 * Nutzt Firebase Authentication für den Anmeldeprozess und bietet visuelles Feedback durch einen Ladebalken.
 */

public class Login extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView clickToRegisterText, forgetPasswordText;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity_login);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailLogin);
        editTextPassword = findViewById(R.id.passwordLogin);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);
        clickToRegisterText = findViewById(R.id.clickToRegister);
        forgetPasswordText = findViewById(R.id.forgetYourPassword);

        clickToRegisterText.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });


        forgetPasswordText.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused -> Toast.makeText(Login.this, "Email sent", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        buttonLogin.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email, password;
            email = editTextEmail.getText().toString();
            password = editTextPassword.getText().toString();

            //delete Progressbar after 2000 delayMillis
            Handler handler = new Handler();
            handler.postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);

            // Nachricht wenn Felder leer sind
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            //Login for Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            //check if user is already verified
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            //if user exist and already verified email, than open MainActivity
                            if (firebaseUser.isEmailVerified()) {
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "please verify your email", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        forgetPasswordText.setOnClickListener(v -> {
            final EditText resetMail = new EditText(v.getContext());
            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password?");
            passwordResetDialog.setMessage("Enter your Email to Received Reset Link.");
            passwordResetDialog.setView(resetMail);
            passwordResetDialog.setPositiveButton("Send", (dialog, which) -> {
                //extract email end send reset Link
                String mail = resetMail.getText().toString();
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(unused -> Toast.makeText(Login.this, "Reset Link Sent to your Email.", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(Login.this, "Error! Reset Link is not sent.", Toast.LENGTH_SHORT).show());
            });
            passwordResetDialog.setNegativeButton("Close", (dialog, which) -> {
                //close the dialog
            });

            passwordResetDialog.create().show();
        });

    }
}