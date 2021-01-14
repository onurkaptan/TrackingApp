      package com.onurkaptan.trackingapp;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.onurkaptan.trackingapp.constants.Constants;

import java.util.Calendar;
import java.util.Date;

  public class LocationServiceClass extends Service {
    private static final String TAG = "DENEMEEEE";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_LOCATION_PERMISSION2 = 12;
    double latitude;
    double longitude;
    String[] latitudes = new String[100];
    String[] longitudes = new String[100];
    String[] radiuses = new String[100];
    String supervisorNumber;
    String currentUser;
    Intent thisIntent;
    int minute;
    int second;
    int hour;
    boolean flag;
      private SensorManager mSensorManager;
      private Sensor mAccelerometer;
      private ShakeDetector mShakeDetector;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet impemented");
    }

      private final LocationCallback locationCallback = new LocationCallback() {
          @Override
          public void onLocationResult(LocationResult locationResult) {
              super.onLocationResult(locationResult);
              flag = false;
              if (locationResult != null && locationResult.getLastLocation() != null) {
                  double dist = 0;
                  latitude = locationResult.getLastLocation().getLatitude();
                  longitude = locationResult.getLastLocation().getLongitude();

                  for (int i = 0; i < latitudes.length; i++) {
                      if (latitudes[i] != null || longitudes[i] != null || radiuses[i] != null) {
                          Location currentLocation = new Location("");
                          currentLocation.setLatitude(latitude);
                          currentLocation.setLongitude(longitude);
                          Location geofence = new Location("");
                          geofence.setLatitude(Double.parseDouble(latitudes[i]));
                          geofence.setLongitude(Double.parseDouble(longitudes[i]));
                          dist = currentLocation.distanceTo(geofence);
                          minute = Calendar.getInstance().get(Calendar.MINUTE);
                          second = Calendar.getInstance().get(Calendar.SECOND);
                          hour = Calendar.getInstance().get(Calendar.HOUR);
                          if (dist <= Double.parseDouble(radiuses[i])) {
                              if (minute == 0 && second < 15) {
                                  PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, thisIntent, 0);
                                  SmsManager smsManager = SmsManager.getDefault();
                                  smsManager.sendTextMessage(/*supervisorNumber*/"5554", null,
                                          "REGULAR" +"," +currentUser+ "," + latitude + "," + longitude+","+hour+"."+minute+"."+second,
                                          pi, null);
                                  Toast.makeText(getApplicationContext(), "Message Send", Toast.LENGTH_SHORT).show();
                              }
                              flag = true;
                              break;
                          }
                      }

                  }
                  if (!flag){
                      PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
                              0, thisIntent, 0);
                      SmsManager smsManager = SmsManager.getDefault();
                      smsManager.sendTextMessage(/*supervisorNumber*/"5554", null,
                              "EMERGENCY" +"," +currentUser + "," + latitude + "," +
                                      longitude+","+hour+"."+minute+"."+second
                              , pi, null);
                      Toast.makeText(getApplicationContext(), "Message Send", Toast.LENGTH_SHORT).show();
                  }
         //         Log.d(TAG, "LOCATION_UPDATE: " + latitude + "," + longitude + "distance: " + dist);
              }
          }
      };

    @SuppressLint("MissingPermission")
    private void startLocationService(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count, double gForce) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, thisIntent, 0);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(/*supervisorNumber*/"5554", null,
                        "FALLDETECTED" +","+currentUser,
                        pi, null);
                //Toast.makeText(MainActivity.this, "FALL DETECTED!!!!!", Toast.LENGTH_LONG).show();
            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);


        Log.d(TAG, "startLocationService: begin");
        String channelID = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        Calendar.getInstance().get(Calendar.MINUTE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationManager != null && notificationManager.getNotificationChannel(channelID) == null){
                NotificationChannel notificationChannel = new NotificationChannel(channelID, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
//        locationRequest.setSmallestDisplacement();
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
        startForeground(175, builder.build());


    }

    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        mSensorManager.unregisterListener(mShakeDetector);
        stopForeground(true);
        stopSelf();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String action = intent.getAction();
            thisIntent = intent;
            if (action != null){
                if (action.equals("startLocationService")){
                    Bundle bundle = intent.getExtras();
                    latitudes = (String[]) bundle.getCharSequenceArray("latitude");
                    longitudes = (String[]) bundle.getCharSequenceArray("longitude");
                    radiuses = (String[]) bundle.getCharSequenceArray("radius");
                    supervisorNumber = (String) bundle.getCharSequence("supervisornumber");
                    currentUser = (String) bundle.getCharSequence("currentUser");
                    startLocationService();
                }else if (action.equals("stopLocationService")){
                    stopLocationService();
                }
            }

        }
        return START_STICKY;
    }

}
