package com.example.rateme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

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
        // Wenn User existiert, dann weiterleiten auf MainActivity
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

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


                // wenn Felder leer sind, Hinweis ausgeben
                if(TextUtils.isEmpty(user)){
                    //editTextUsername.setError("Enter Email");
                    Toast.makeText(Register.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    //editTextEmail.setError("Enter Email");
                    Toast.makeText(Register.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    // editTextPassword.setError("Enter Password");
                    Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length() < 6){
                    //editTextPassword.setError("Password must be => 6 Characters");
                    Toast.makeText(Register.this, "Password must be => 6 Characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)){
                    //editTextConfirmPassword.setError("Enter repeated Password");
                    Toast.makeText(Register.this, "Enter repeated Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!TextUtils.equals(confirmPassword,password)){
                    //editTextConfirmPassword.setError("Password have to be the same");
                    //editTextPassword.setError("Password have to be the same");
                    Toast.makeText(Register.this, "Password have to be the same", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();
                //create register account with firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Account created",
                                            Toast.LENGTH_SHORT).show();

                                    firebaseFirestore.collection("User")
                                            .document(mAuth.getInstance().getUid())
                                            .set(new UserModel(user, email));

                                    progressDialog.cancel();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed",
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                }
                            }
                        });
            }
        });

    }
}