    package com.onurkaptan.trackingapp.mainscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onurkaptan.trackingapp.LocationServiceClass;
import com.onurkaptan.trackingapp.R;
import com.onurkaptan.trackingapp.ResObj;
import com.onurkaptan.trackingapp.login.Opening;

import java.util.ArrayList;
import java.util.Map;

public class RegularUserMainScreen extends AppCompatActivity {

    LocationManager locationManager;
    int counter = 0;

    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_LOCATION_PERMISSION2 = 12;
    ArrayList<ResObj> resObjs;

    String[] lat = new String[100];
    String[] longi = new String[100];
    String[] radi = new String[100];

    LocationServiceClass mLocationServiceClass;
    Context mContext;
    public Context getCtx(){
        return mContext;
    }


    private static final String TAG = "RegularUserMainScreen";
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String CURRENT_STATE;
    private GeofencingClient geofencingClient;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.regular_options_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logOutRegular) {
            stopLocationService();
            try {
                firebaseAuth.signOut();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(RegularUserMainScreen.this, Opening.class);
            if (Build.VERSION.SDK_INT <= 16) {
                ActivityCompat.finishAffinity(RegularUserMainScreen.this);
            } else {
                finishAffinity();
            }
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resObjs = new ArrayList<>();

        System.out.println("started");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        try {
            firebaseUser = firebaseAuth.getCurrentUser();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }

        AsyncGetData asyncGetData = new AsyncGetData();
        asyncGetData.execute();
        setContentView(R.layout.activity_regular_user_main_screen);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.SEND_SMS},
                    Integer.parseInt("1234"));
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RegularUserMainScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }


        int permissionAccessFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionAccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Integer.parseInt("12345"));
        }
        int permissionAccessFineLocation3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        if (permissionAccessFineLocation3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    Integer.parseInt("12345"));

        }
            try {
                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() { startLocationService();
}
                        },5000);

            }catch (Exception e){
                System.out.println("Message is:  "+e.getLocalizedMessage());
            }


        mContext=this;


    }
    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null){
            for (ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
                if (LocationServiceClass.class.getName().equals(service.service.getClassName())){
                    if (service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    private void startLocationService(){

        if (!isLocationServiceRunning() && resObjs.size()>0){
            Intent intent = new Intent(getApplicationContext(), LocationServiceClass.class);
            intent.setAction("startLocationService");
            for (int i = 0; i < resObjs.size(); i++){
                lat[i] = resObjs.get(i).getLatitude();
                longi[i] = resObjs.get(i).getLongitude();
                radi[i] = resObjs.get(i).getRadius();
            }
            Bundle bundle = new Bundle();
            bundle.putCharSequenceArray("latitude", lat);
            bundle.putCharSequenceArray("longitude", longi);
            bundle.putCharSequenceArray("radius", radi);
            bundle.putCharSequence("supervisornumber", resObjs.get(0).getSupervisorNumber());
            bundle.putCharSequence("currentUser", firebaseUser.getEmail());
            intent.putExtras(bundle);
            startService(intent);
            Toast.makeText(getApplicationContext(), "Location Service Started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){

        if (isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationServiceClass.class);
            intent.setAction("stopLocationService");
            startService(intent);
            Toast.makeText(getApplicationContext(), "Location Service Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getApplicationContext(), "Call Permission Granted", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(getApplicationContext(), "Call Permission Not Granted", Toast.LENGTH_LONG);
                }
                break;

            case 1234:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getApplicationContext(), "Send Sms Permission Granted", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(getApplicationContext(), "Send Sms Permission Not Granted", Toast.LENGTH_LONG);
                }
                break;


            case 12345:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getApplicationContext(), "ACCESS_FINE_LOCATION Permission Granted", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(getApplicationContext(), "ACCESS_FINE_LOCATION Permission Not Granted", Toast.LENGTH_LONG);
                }
                break;
            case REQUEST_CODE_LOCATION_PERMISSION:
                    startLocationService();
                    break;
            default:
                break;

        }
    }

    public void panic(View view) {
        onCall();
    }




    public void onCall() {
        String defaultNumber = "122";

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    Integer.parseInt("123"));
         }
        else {
                DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getEmail());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Map<String, Object> data = document.getData();
                                String supervisorPhoneNumber = (String) data.get("supervisornumber");
                                String supervisorCheck = (String) data.get("issupervised");
                                if(supervisorCheck.matches("1")){
                                    startActivity(new Intent(Intent.ACTION_CALL)
                                            .setData(Uri.parse("tel:" + supervisorPhoneNumber)));
                                }
                                else {
                                    startActivity(new Intent(Intent.ACTION_CALL)
                                            .setData(Uri.parse("tel:" + defaultNumber)));
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

    class AsyncGetData extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            resObjs.clear();
            firebaseFirestore.collection(firebaseUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult() != null){
                            for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                Map<String,Object> data;
                                data = queryDocumentSnapshot.getData();
                                String sLatitude = (String) data.get("latitude");
                                String sLongitude = (String) data.get("longitude");
                                String sRadius = (String) data.get("radius");
                                String sIsDone = (String) data.get("isDone");
                                String supervisorNumber = (String) data.get("supervisornumber");
                                String sResID = queryDocumentSnapshot.getId();
                                ResObj resObj = new ResObj(sLatitude,sLongitude,sRadius,sIsDone,sResID,supervisorNumber);
                                resObjs.add(resObj);
                            }
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),  task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

}

