package com.onurkaptan.trackingapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaptan.trackingapp.R;
import com.onurkaptan.trackingapp.mainscreen.RegularUserMainScreen;
import com.onurkaptan.trackingapp.mainscreen.SupervisorMainScreen;

import java.util.Map;

public class LogIn extends AppCompatActivity {
    EditText eMail;
    EditText password;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        progressBar = findViewById(R.id.progressBarLogIn);
        progressBar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        eMail = findViewById(R.id.editTextUsernameLogIn);
        password = findViewById(R.id.editTextPasswordLogIn);
        firebaseFirestore = FirebaseFirestore.getInstance();

    }


    public void logIn(View view){
        String email = eMail.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (email.isEmpty()) {
            eMail.setError(getString(R.string.input_error_email));
            eMail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eMail.setError(getString(R.string.input_error_email_invalid));
            eMail.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            password.setError(getString(R.string.input_error_password));
            password.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    DocumentReference documentReference = firebaseFirestore.collection("users").document(email);
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    Map<String, Object> data = document.getData();
                                    String userType = (String) data.get("usertype");
                                    if(userType.matches("Supervisor")){
                                        Intent intent = new Intent(LogIn.this, SupervisorMainScreen.class);
                                        if(Build.VERSION.SDK_INT <= 16){
                                            ActivityCompat.finishAffinity(LogIn.this);
                                        }
                                        else {
                                            finishAffinity();
                                        }
                                        startActivity(intent);
                                    }
                                    else{
                                        Intent intent2 = new Intent(LogIn.this, RegularUserMainScreen.class);
                                        if(Build.VERSION.SDK_INT <= 16){
                                            ActivityCompat.finishAffinity(LogIn.this);
                                        }
                                        else {
                                            finishAffinity();
                                        }
                                        startActivity(intent2);
                                    }
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "E-mail doesn't exist", Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Get failed with " + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(),"signInWithEmail:failure " + task.getException(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}