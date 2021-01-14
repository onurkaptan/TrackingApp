package com.onurkaptan.trackingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onurkaptan.trackingapp.user.AllClients;

import java.util.ArrayList;

public class AllClientsOnMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ArrayList<String> arrayList;
    ArrayList<Marker> markers;
    private static final String TAG = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_clients_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        markers = new ArrayList<>();



    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       firebaseFirestore.collection(firebaseUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               if (error != null){
                   return;
               }
               ArrayList<String> arrayListTest = new ArrayList<>();
               mMap.clear();
               for (QueryDocumentSnapshot queryDocumentSnapshot : value){
                   arrayListTest = (ArrayList<String>) queryDocumentSnapshot.get("location");
                   String name = (String) queryDocumentSnapshot.get("fullname");
                   if (arrayListTest.size() != 0){
                       if(arrayListTest.get(arrayListTest.size()-1) != null){
                            String[] coordinates = arrayListTest.get(arrayListTest.size()-1).split(",");
                            Double latitude = Double.parseDouble(coordinates[0]);
                            Double longitude = Double.parseDouble(coordinates[1]);
                    //         String[] time = coordinates[2].split("\\.");
                            LatLng latLng = new LatLng(latitude,longitude);
           /*                  if (test >= 0){
                                String[] coordinates2 = arrayListTest.get(arrayListTest.size()-2).split(",");
                                Double latitude2 = Double.parseDouble(coordinates2[0]);
                                Double longitude2 = Double.parseDouble(coordinates2[1]);
                                String[] time2 = coordinates2[2].split("\\.");

                                Location currentLocation = new Location("");
                                currentLocation.setLatitude(latitude);
                                currentLocation.setLongitude(longitude);

                                Location beforeLocation = new Location("");
                                beforeLocation.setLatitude(latitude2);
                                beforeLocation.setLongitude(longitude2);

                                double dist = currentLocation.distanceTo(beforeLocation);

                                int timeSeconds = Integer.parseInt(time[0])*3600 + Integer.parseInt(time[1]) * 60 + Integer.parseInt(time[2]);
                                int time2Seconds = Integer.parseInt(time2[0])*3600 + Integer.parseInt(time2[1]) * 60 + Integer.parseInt(time2[2]);
                                double speedMS = dist/(timeSeconds-time2Seconds);
                                if(speedMS > 1.5){
                                    Log.d(TAG, "onEvent: Walking Speed:" + speedMS);
                                }else {
                                    Log.d(TAG, "onEvent: Running Speed:"+ speedMS);

                                }
                            }*/
                            markers.add(mMap.addMarker(new MarkerOptions().position(latLng).title(name+" "+coordinates[0]+","+coordinates[1])));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                       }
                   }
               }


           }
       });

    }

}
