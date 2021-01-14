package com.onurkaptan.trackingapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onurkaptan.trackingapp.R;
import com.onurkaptan.trackingapp.mainscreen.SupervisorMainScreen;

public class RemoveClient extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    EditText e_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_client);

        e_mail = findViewById(R.id.emailRemoveActivity);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }


    public void removeClient(View view){
        String eMail = e_mail.getText().toString();
        Toast.makeText(getApplicationContext(), eMail, Toast.LENGTH_LONG).show();
        System.out.println("Email is: " + eMail);
        DocumentReference eMailExists = firebaseFirestore.collection("users").document(eMail);
        eMailExists.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        System.out.println("Accessed To User");
                        DocumentReference yourPatient = firebaseFirestore.collection(firebaseUser.getEmail()).document(eMail);
                        yourPatient.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()){
                                        firebaseFirestore.collection(firebaseUser.getEmail()).document(eMail).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "successfully removed", Toast.LENGTH_LONG).show();

                                                DocumentReference updateInUser = firebaseFirestore.collection("users").document(eMail);
                                                updateInUser.update("issupervised",(String) "0".toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                 }).addOnFailureListener(new OnFailureListener() {
                                                     @Override
                                                     public void onFailure(@NonNull Exception e) {
                                                         Log.w("Error updating document", e);
                                                     }
                                                 });

                                                updateInUser.update("supervisornumber",(String) "0".toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Error updating document", e);
                                                    }
                                                });

                                                Intent intent = new Intent(RemoveClient.this, SupervisorMainScreen.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),  e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Not your patient", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(), "E-mail doesn't exist", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}