package com.onurkaptan.trackingapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaptan.trackingapp.R;
import com.onurkaptan.trackingapp.mainscreen.RegularUserMainScreen;
import com.onurkaptan.trackingapp.mainscreen.SupervisorMainScreen;
import com.onurkaptan.trackingapp.user.User;

public class SignUp extends AppCompatActivity {
    EditText eMail;
    EditText fullName;
    EditText passwordText;
    EditText passwordCheckText;
    EditText phoneNumber;
    Spinner spinner1;
    Button signInButton;
    ProgressBar progressBar;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar = findViewById(R.id.progressBarSignIn);
        progressBar.setVisibility(View.GONE);
        eMail = findViewById(R.id.emailSignUp);
        fullName = findViewById(R.id.fullNameSignUp);
        passwordText = findViewById(R.id.editTextPasswordSignUp);
        passwordCheckText = findViewById(R.id.editTextPasswordCheckSignUp);
        signInButton = findViewById(R.id.buttonSignIn);
        phoneNumber = findViewById(R.id.phoneNumberSignUp);
        spinner1 = (Spinner) findViewById(R.id.spinnerSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();



        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void signUp(View view){

        String name = fullName.getText().toString().trim();
        String email = eMail.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String passwordCheck = passwordCheckText.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String userType = spinner1.getSelectedItem().toString().trim();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        if (name.isEmpty()) {
            fullName.setError(getString(R.string.input_error_name));
            fullName.requestFocus();
            return;
        }

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

        if (password.isEmpty()) {
            passwordText.setError(getString(R.string.input_error_password));
            passwordText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordText.setError(getString(R.string.input_error_password_length));
            passwordText.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            phoneNumber.setError(getString(R.string.input_error_phone));
            phoneNumber.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            phoneNumber.setError(getString(R.string.input_error_phone_invalid));
            phoneNumber.requestFocus();
            return;
        }

        if(!password.matches(passwordCheck)){
            passwordText.setError(getString(R.string.input_error_password_check));
            passwordText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //store data;
                    User user = new User(name, email, password, phone, userType);
                    firebaseFirestore.collection("users").document(email).set(user.getHashMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            if(userType.matches("Supervisor")){
                                Intent intent = new Intent(SignUp.this, SupervisorMainScreen.class);
                                if(Build.VERSION.SDK_INT <= 16){
                                    ActivityCompat.finishAffinity(SignUp.this);
                                }
                                else {
                                    finishAffinity();
                                }
                                startActivity(intent);
                            }
                            else {
                                Intent intent2 = new Intent(SignUp.this, RegularUserMainScreen.class);
                                if(Build.VERSION.SDK_INT <= 16){
                                    ActivityCompat.finishAffinity(SignUp.this);
                                }
                                else {
                                    finishAffinity();
                                }
                                startActivity(intent2);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUp.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                }
                else {
                    Toast.makeText(SignUp.this,task.getException().getMessage(),Toast.LENGTH_LONG);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }
























}