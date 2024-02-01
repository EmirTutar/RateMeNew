package RateMe.LoginActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rateme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Die Register-Klasse ermöglicht es neuen Benutzern, ein Konto zu erstellen.
 * Sie erfasst Benutzerinformationen wie Benutzername, E-Mail und Passwort und verwendet Firebase Authentication,
 * um das Konto zu erstellen und eine E-Mail-Verifizierung zu senden. Die Klasse führt auch eine Validierung
 * der Eingabedaten durch und zeigt Fortschrittsdialoge während des Registrierungsprozesses an.
 * @noinspection deprecation
 */

public class Register extends AppCompatActivity {

    public static final String Tag = "Tag";
    EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    TextView clickToLoginText;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
    }

    /** @noinspection DataFlowIssue */
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity_signup);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextUsername = findViewById(R.id.username);
        editTextConfirmPassword = findViewById(R.id.rePassword);
        buttonReg = findViewById(R.id.buttonSignUp);
        progressBar = findViewById(R.id.progressBar);
        clickToLoginText = findViewById(R.id.clickToLogin);

        clickToLoginText.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        buttonReg.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String user, email, password, confirmPassword;
            email = editTextEmail.getText().toString().trim();
            password = editTextPassword.getText().toString();
            user = editTextUsername.getText().toString();
            confirmPassword = editTextConfirmPassword.getText().toString();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);

            Query query = firebaseFirestore.collection("User")
                    .whereEqualTo("username", user);

            // Abfrage durchführen
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult() != null && !task.getResult().isEmpty()) {
                        Toast.makeText(Register.this, "This Username already exist", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (TextUtils.isEmpty(user)) {
                Toast.makeText(Register.this, "Enter Username", Toast.LENGTH_SHORT).show();
                editTextUsername.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Register.this, "Enter Email", Toast.LENGTH_SHORT).show();
                editTextEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
                editTextPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(Register.this, "Password must be => 6 Characters", Toast.LENGTH_SHORT).show();
                editTextPassword.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(Register.this, "Enter repeated Password", Toast.LENGTH_SHORT).show();
                editTextConfirmPassword.requestFocus();
                return;
            }

            if (!TextUtils.equals(confirmPassword, password)) {
                Toast.makeText(Register.this, "Password have to be the same", Toast.LENGTH_SHORT).show();
                editTextConfirmPassword.requestFocus();
                editTextPassword.requestFocus();
                return;
            }

            progressDialog.show();
            //create register account with firebase
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    //send verification to email
                    firebaseUser.sendEmailVerification().addOnSuccessListener(unused -> Toast.makeText(Register.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Log.d(Tag, "onFailure: Email not sent " + e.getMessage()));
                    Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();

                    //create in Firestore new User
                    firebaseFirestore.collection("User")
                            .document(FirebaseAuth.getInstance().getUid())
                            .set(new UserModel(user, email));

                    progressDialog.cancel();

                    //start Login after registration
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();

                } else {
                    // Registrierung fehlgeschlagen
                    Exception exception = task.getException();
                    if (exception instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(Register.this, "Email already exist", Toast.LENGTH_SHORT).show();
                    } else if (exception instanceof FirebaseAuthWeakPasswordException) {
                        Toast.makeText(Register.this, "password is too weak", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = exception.getMessage();
                        //noinspection StringOperationCanBeSimplified
                        Toast.makeText(Register.this, "Error: " + errorMessage.toString(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.cancel();
                }
            });
        });

    }
}