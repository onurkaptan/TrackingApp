package com.onurkaptan.trackingapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.onurkaptan.trackingapp.R;
import com.onurkaptan.trackingapp.mainscreen.RegularUserMainScreen;
import com.onurkaptan.trackingapp.mainscreen.SupervisorMainScreen;

import java.util.Map;

public class Opening extends AppCompatActivity {
    static final int REQUEST_CODE = 123;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        firebaseAuth = firebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        try {
            firebaseUser = firebaseAuth.getCurrentUser();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        if (firebaseUser != null){
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getEmail());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            Map<String, Object> data = document.getData();
                            String userType = (String) data.get("usertype");
                            if(userType.matches("Supervisor")){
                                Intent intent = new Intent(Opening.this, SupervisorMainScreen.class);
                                if(Build.VERSION.SDK_INT <= 16){
                                    ActivityCompat.finishAffinity(Opening.this);
                                }
                                else {
                                    finishAffinity();
                                }
                                startActivity(intent);
                            }
                            else{
                                Intent intent2 = new Intent(Opening.this, RegularUserMainScreen.class);
                                if(Build.VERSION.SDK_INT <= 16){
                                    ActivityCompat.finishAffinity(Opening.this);
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


    }



    public void goToLogIn(View view){

 /*       if(ContextCompat.checkSelfPermission(Opening.this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(Opening.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(Opening.this, Manifest.permission.READ_CONTACTS )
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Opening.this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(Opening.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(Opening.this, Manifest.permission.READ_CONTACTS)){
                AlertDialog.Builder builder = new AlertDialog.Builder(Opening.this);
                builder.setTitle("Grant Those Permissions");
                builder.setMessage("Camera, Read Contect and Read Storage");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(Opening.this, new String[] {Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else{
                ActivityCompat.requestPermissions(Opening.this, new String[] {Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
            }
        }
        else {*/
            Intent intent = new Intent(Opening.this, LogIn.class);
            startActivity(intent);
       /* } */

    }

    public void goToSignIn(View view){
    /*    if(ContextCompat.checkSelfPermission(Opening.this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(Opening.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(Opening.this, Manifest.permission.READ_CONTACTS )
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Opening.this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(Opening.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(Opening.this, Manifest.permission.READ_CONTACTS)){
                AlertDialog.Builder builder = new AlertDialog.Builder(Opening.this);
                builder.setTitle("Grant Those Permissions");
                builder.setMessage("Camera, Read Contect and Read Storage");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(Opening.this, new String[] {Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else{
                ActivityCompat.requestPermissions(Opening.this, new String[] {Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, REQUEST_CODE);
            }
        }
        else {*/
            Intent intent = new Intent(Opening.this, SignUp.class);
            startActivity(intent);
 /*       }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            Toast toast;
            if (grantResults.length > 0 && grantResults[0] + grantResults[1] + grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                toast = Toast.makeText(getApplicationContext(), "Permissions granted", Toast.LENGTH_LONG);
            } else {
                toast = Toast.makeText(getApplicationContext(), "Permissions denied", Toast.LENGTH_LONG);
            }
            toast.show();

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}