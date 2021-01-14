package com.onurkaptan.trackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onurkaptan.trackingapp.user.AllClients;

import java.util.ArrayList;
import java.util.Map;

public class AllClientsTrack extends AppCompatActivity {


    ListView listView;
    ArrayList<String> clientNames;
    ArrayList<String> clientEmails;
    ArrayAdapter arrayAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Runnable runnable;
    Handler handler;
    String supervisorNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_clients_track);
        listView = findViewById(R.id.listView2);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        clientNames = new ArrayList<>();
        clientEmails = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());



        firebaseFirestore.collection(firebaseUser.getEmail()).orderBy("fullname", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                        Map<String,Object> data;
                        data = queryDocumentSnapshot.getData();
                        clientNames.add((String) data.get("fullname"));
                        clientEmails.add((String) data.get("email"));
                        supervisorNumber = (String)data.get("supervisornumber");
                    }
                }else{
                    Toast.makeText(getApplicationContext(),  task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                arrayAdapter = new ArrayAdapter(AllClientsTrack.this, android.R.layout.simple_list_item_1, clientNames);
                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new  Intent(AllClientsTrack.this, TrackMap.class);
                        intent.putExtra("email", clientEmails.get(position));
                        intent.putExtra("supervisornumber", supervisorNumber);
                        startActivity(intent);
                    }
                });
            }
        });
        new Handler(getMainLooper()).postDelayed(new Runnable(){
            @Override
            public void run() {

            }

        },2000);

    }
}