package com.onurkaptan.trackingapp;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListenSMSService extends Service {
    private static final String TAG = "startSMSService";
    Intent thisIntent;
    private ListenSMSBroadcast smsBroadcastReceiver;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet impemented");
    }

    @Override
    public void onCreate() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String action = intent.getAction();
            thisIntent = intent;
            if (action != null){
                if (action.equals("startSMSService")){
                    startSMSService();
                }else if (action.equals("stopSMSService")){
                    stopSMSService();
                }
            }
        }

        return START_STICKY;
    }

    private void stopSMSService() {
        stopForeground(true);
        stopSelf();
    }

    private void startSMSService() {
        String channelID = "SMS_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationManager != null && notificationManager.getNotificationChannel(channelID) == null){
                NotificationChannel notificationChannel = new NotificationChannel(channelID, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        smsBroadcastReceiver = new ListenSMSBroadcast();
        registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        startForeground(175175, builder.build());
        smsBroadcastReceiver.setListener(new ListenSMSBroadcast.Listener() {
            @Override
            public void onTextReceived(String text) {
                String[] myText = text.split(",");
                if(text.startsWith("FALLDETECTED")){
                    String CHANNEL_ID2 = "Fall_channel";
                    String CHANNEL_NAME2 = "Fall_channel";
                    NotificationManagerCompat manager2 = NotificationManagerCompat.from(getApplicationContext());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID2, CHANNEL_NAME2,
                                NotificationManager.IMPORTANCE_DEFAULT);
                        manager2.createNotificationChannel(channel2);
                        NotificationCompat.Builder mBuilder2 = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID2)
                                .setContentTitle("FALL")
                                .setContentText(myText[1] + " FALL DETECTED")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        NotificationManager notificationManager2 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notificationManager2.notify(1234, mBuilder2.build());
                    }
                }else{
                    DocumentReference docref = firebaseFirestore.collection(firebaseUser.getEmail()).document(myText[1]);
                    docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()){
                                    Map<String, Object> data = documentSnapshot.getData();
                                    ArrayList<String> myLocations = (ArrayList<String>) data.get("location");
                                    int localSize = myLocations.size();
                                    while (localSize > 5){
                                        docref.update("location", FieldValue.arrayRemove(myLocations.get(0)));
                                        localSize--;
                                    }
                                }
                            }
                        }
                    });
                    docref.update("location", FieldValue.arrayUnion(myText[2] +","+ myText[3]+","+myText[4]));
                    if (text.startsWith("EMERGENCY")){
                        Log.d(TAG, "myText[0]:"+ myText[0] + " myText[1]: " + myText[1] + "myText[2]:" + myText[2] + "myText[3]:"+ myText[3]);
                        String CHANNEL_ID = "remoteMessage.getNotification().getTitle()";
                        String CHANNEL_NAME = "remoteMessage.getNotification().getTitle()";
                        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                                    NotificationManager.IMPORTANCE_DEFAULT);
                            manager.createNotificationChannel(channel);
                            NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                                    .setContentTitle("EMERGENCY")
                                    .setContentText(myText[1] +" out of restricted area")
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                            NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                            notificationManager.notify(123,mBuilder.build());
                        }


                    }else if (text.startsWith("REGULAR")){

                    }
                }

            }
        });
    }



}
