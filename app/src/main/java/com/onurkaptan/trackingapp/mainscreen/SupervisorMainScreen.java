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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.onurkaptan.trackingapp.AllClientsOnMap;
import com.onurkaptan.trackingapp.AllClientsTrack;
import com.onurkaptan.trackingapp.ListenSMSBroadcast;
import com.onurkaptan.trackingapp.ListenSMSService;
import com.onurkaptan.trackingapp.user.AddClient;
import com.onurkaptan.trackingapp.user.AllClients;
import com.onurkaptan.trackingapp.R;
import com.onurkaptan.trackingapp.user.RemoveClient;
import com.onurkaptan.trackingapp.login.Opening;

public class SupervisorMainScreen extends AppCompatActivity {



    FirebaseAuth firebaseAuth;
    private ListenSMSBroadcast smsBroadcastReceiver;
    private static final String TAG = "SupervisorMainScreen";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.supervisor_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.addPatient){
            Intent intent = new Intent(SupervisorMainScreen.this, AddClient.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.deletePatient){
            Intent intent = new Intent(SupervisorMainScreen.this, RemoveClient.class);
            startActivity(intent);
        }

        else if(item.getItemId() == R.id.logOutSupervisor){
            stopSMSService();
            try {
                firebaseAuth.signOut();
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(SupervisorMainScreen.this, Opening.class);
            if(Build.VERSION.SDK_INT <= 16){
                ActivityCompat.finishAffinity(SupervisorMainScreen.this);
            }
            else {
                finishAffinity();
            }
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_main_screen);
        firebaseAuth = FirebaseAuth.getInstance();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_SMS},
                    Integer.parseInt("1234"));
            Log.d(TAG, "onCreate: 1 ");
        }
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    Integer.parseInt("12345"));
            Log.d(TAG, "onCreate: 2");
        }
        int permissionCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permissionCheck3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.SEND_SMS},
                    Integer.parseInt("12346"));
            Log.d(TAG, "onCreate: 3");
        }

        startSMSService();

    }

    public void goToAllClients(View view){
        Intent intent = new Intent(SupervisorMainScreen.this, AllClients.class);
        startActivity(intent);

    }


    private boolean isSMSServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null){
            for (ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
                if (ListenSMSService.class.getName().equals(service.service.getClassName())){
                    if (service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startSMSService(){
        if (!isSMSServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), ListenSMSService.class);
            intent.setAction("startSMSService");
            startService(intent);
  //         Toast.makeText(getApplicationContext(), "SMS Service Started", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToTrack(View view){
        Intent intent = new Intent(SupervisorMainScreen.this, AllClientsTrack.class);
        startActivity(intent);
    }

    private void stopSMSService(){
        if (isSMSServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), ListenSMSService.class);
            intent.setAction("stopSMSService");
            startService(intent);
            Toast.makeText(getApplicationContext(), "SMS Service Stopped", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1234: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
            }
            case 12345:
                break;
            case 123456:
                break;
            default:
                break;// other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void goToAllMaps(View view){
    Intent intent = new Intent(SupervisorMainScreen.this, AllClientsOnMap.class);
    startActivity(intent);


    }






}