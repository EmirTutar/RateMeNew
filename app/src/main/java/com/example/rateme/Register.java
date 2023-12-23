package com.example.rateme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Register extends AppCompatActivity {

    public static final String Tag = "Tag";
    EditText editTextUsername ,editTextEmail, editTextPassword, editTextConfirmPassword;
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

        progressDialog=new ProgressDialog(this);
    }

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth= FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextUsername = findViewById(R.id.username);
        editTextConfirmPassword = findViewById(R.id.rePassword);
        buttonReg = findViewById(R.id.buttonSignUp);
        progressBar = findViewById(R.id.progressBar);
        clickToLoginText = findViewById(R.id.clickToLogin);

        // Wenn man auf Textview drückt, kommt man zur Login Seite
        clickToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String user, email, password, confirmPassword;
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString();
                user = editTextUsername.getText().toString();
                confirmPassword = editTextConfirmPassword.getText().toString();

                //delete Progressbar after 2000 delayMillis
                Handler handler=new Handler();
                handler.postDelayed(()->{
                    progressBar.setVisibility(View.GONE);
                }, 2000);


// Abfrage erstellen
                Query query = firebaseFirestore.collection("User")
                        .whereEqualTo("username", user);

// Abfrage durchführen
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Überprüfen, ob es Ergebnisse gibt
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            Toast.makeText(Register.this, "This Username already exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                            if(TextUtils.isEmpty(user)){
                    //editTextUsername.setError("Enter Email");
                    Toast.makeText(Register.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    editTextUsername.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    //editTextEmail.setError("Enter Email");
                    Toast.makeText(Register.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    editTextEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    // editTextPassword.setError("Enter Password");
                    Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    editTextPassword.requestFocus();
                    return;
                }
                if(password.length() < 6){
                    //editTextPassword.setError("Password must be => 6 Characters");
                    Toast.makeText(Register.this, "Password must be => 6 Characters", Toast.LENGTH_SHORT).show();
                    editTextPassword.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)){
                    //editTextConfirmPassword.setError("Enter repeated Password");
                    Toast.makeText(Register.this, "Enter repeated Password", Toast.LENGTH_SHORT).show();
                    editTextConfirmPassword.requestFocus();
                    return;
                }
                if(!TextUtils.equals(confirmPassword,password)){
                    //editTextConfirmPassword.setError("Password have to be the same");
                    //editTextPassword.setError("Password have to be the same");
                    Toast.makeText(Register.this, "Password have to be the same", Toast.LENGTH_SHORT).show();
                    editTextConfirmPassword.requestFocus();
                    editTextPassword.requestFocus();
                    return;
                }

                progressDialog.show();
                //create register account with firebase
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser= mAuth.getCurrentUser();

                                    //send verification to email
                                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Register.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(Tag, "onFailure: Email not sent "+ e.getMessage());
                                        }
                                    });
                                    Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();

                                    //create in Firestore new User
                                    firebaseFirestore.collection("User")
                                            .document(mAuth.getInstance().getUid())
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
                                        Toast.makeText(Register.this, "Error: "+ errorMessage.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.cancel();
                                }
                            }
                });
            }
        });

    }
}