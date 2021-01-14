package com.onurkaptan.trackingapp.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.onurkaptan.trackingapp.R;
import com.onurkaptan.trackingapp.login.LogIn;
import com.onurkaptan.trackingapp.mainscreen.RegularUserMainScreen;
import com.onurkaptan.trackingapp.mainscreen.SupervisorMainScreen;
import com.onurkaptan.trackingapp.user.User;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddClient extends AppCompatActivity {

    EditText mail;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    static String supervisorNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        mail = findViewById(R.id.emailAddActivity);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }


    public void addClient(View view){
        String e_mail = mail.getText().toString();

        DocumentReference documentReferenceCurrentUser = firebaseFirestore.collection("users").document(firebaseUser.getEmail());
        documentReferenceCurrentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Map<String,Object> data = document.getData();
                        supervisorNumber = (String) data.get("phonenumber");
                    }
                }
            }
        });

        DocumentReference documentReferenceAddingUser = firebaseFirestore.collection("users").document(e_mail);
        documentReferenceAddingUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Map<String,Object> data = document.getData();
                        String isSupervis = (String) data.get("issupervised");
                        if(isSupervis.equals("-1")){
                            Toast.makeText(getApplicationContext(), "Can't Add supervisors", Toast.LENGTH_LONG).show();
                        }else{
                            documentReferenceAddingUser.update("supervisornumber" , supervisorNumber).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Can't Updated", Toast.LENGTH_LONG).show();
                                }
                            });
                            documentReferenceAddingUser.update("issupervised" , (String) "1".toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Can't Updated", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Email doesn't exist", Toast.LENGTH_LONG).show();
                }
            }
        });


        DocumentReference documentReferenceCurrentUserCurrentUserToAdd = firebaseFirestore.collection(firebaseUser.getEmail()).document(e_mail);
        documentReferenceCurrentUserCurrentUserToAdd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document1 = task.getResult();
                    if (document1.exists()){
                        Toast.makeText(getApplicationContext(), "User exists", Toast.LENGTH_LONG).show();
                    }else {
                        DocumentReference documentReferenceCurrentUserToAdd = firebaseFirestore.collection("users").document(e_mail);
                        documentReferenceCurrentUserToAdd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document2 = task.getResult();
                                    if (document2.exists()){
                                        Map<String,Object> data = document2.getData();
                                        //try
                                        ArrayList<String> a = new ArrayList<>();
                                        data.put("location", a);
                                        firebaseFirestore.collection(firebaseUser.getEmail()).document(e_mail).set(data);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    /*    ArrayList<String> arrayList = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("location",arrayList);
        firebaseFirestore.collection("locations").document(e_mail).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i("ADD ARRAY", "onComplete: Successful");
                }
            }
        });*/
    }

}