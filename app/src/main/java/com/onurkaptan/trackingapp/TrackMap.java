package com.onurkaptan.trackingapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;

public class TrackMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    String clientEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Intent intent = getIntent();
        clientEmail = intent.getStringExtra("email");

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
        DocumentReference docRef = firebaseFirestore.collection(firebaseUser.getEmail()).document(clientEmail);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    mMap.clear();
                    Map<String,Object> data;
                    data = snapshot.getData();
                    ArrayList<String> locations = (ArrayList<String>) data.get("location");

                    for(int i = 0; i < locations.size();i++){
                        String[] loc = locations.get(i).split(",");
                        Double lati = Double.parseDouble(loc[0]);
                        Double longi = Double.parseDouble(loc[1]);
                        LatLng latLng = new LatLng(lati,longi);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(lati+","+longi));
                        if(i != locations.size()-1){
                            String[] locnext = locations.get(i+1).split(",");
                            Double latinext = Double.parseDouble(locnext[0]);
                            Double longinext = Double.parseDouble(locnext[1]);
                            LatLng latLngnext = new LatLng(latinext,longinext);
                            Polyline line = mMap.addPolyline(new PolylineOptions()
                                    .add(latLng, latLngnext)
                                    .width(15)
                                    .color(Color.RED));
                        }

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    }

                } else {
                }
            }
        });
    }
}