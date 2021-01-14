package com.onurkaptan.trackingapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    boolean flag, flag2, flag3;
    ArrayList<ResObj> resObjArrayList;
    String userEmail;
    String supervisorNumber;
    Button addClientButton, deleteRestrictionButton, radiusButton;
    ArrayList<Marker> markers;
    ArrayList<Circle> circles;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    EditText radiusText;

    private float GEOFENCE_RADIUS = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();

        userEmail = intent.getStringExtra("email");
        supervisorNumber = intent.getStringExtra("supervisornumber");
        addClientButton = findViewById(R.id.addClientButton);
        addClientButton.setText("Add Restriction");
        deleteRestrictionButton = findViewById(R.id.deleteRestrictionButton);
        deleteRestrictionButton.setText("Delete Restriction");
        radiusButton = findViewById(R.id.enterRadiusButton);
        radiusButton.setText("Save Radius");
        radiusText = findViewById(R.id.radiusText);
        flag = true;
        flag2 = false;
        flag3 = false;
        markers = new ArrayList<>();
        markers.clear();
        circles = new ArrayList<>();
        circles.clear();
        resObjArrayList = new ArrayList<>();
        resObjArrayList.clear();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

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

        // Add a marker in Sydney and move the camera

        mMap.setOnMapLongClickListener(this);

        getAllRes();
        new Handler(getMainLooper()).postDelayed(new Runnable(){
            @Override
            public void run() {
                drawAllRes();
            }

        },2000);


    }


    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        Circle mapCircle = mMap.addCircle(circleOptions);
        circles.add(mapCircle);


    }
    private void addMarker(LatLng latLng) {

                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                markers.add(marker);

    }
    private void  deleteMarker(LatLng latLng){
        for (Marker marker : markers){
            String markerLat = String.format("%.3f", marker.getPosition().latitude);
            String markerLong = String.format("%.3f", marker.getPosition().longitude);
            String latLat = String.format("%.3f", latLng.latitude);
            String latLong = String.format("%.3f", latLng.longitude);
            if(markerLat.equals(latLat) && markerLong.equals(latLong)){
                for (ResObj resObj: resObjArrayList){
                        Float f1 = Float.parseFloat(resObj.getLatitude());
                        Float f2 = Float.parseFloat(resObj.getLongitude());
                        String resObjLat = String.format("%.3f", f1);
                        String resObjLng = String.format("%.3f", f2);
                    if(resObjLat.equals(latLat) && resObjLng.equals(latLong)){
                        marker.remove();
                        deleteCircle(latLng);
                       firebaseFirestore.collection(userEmail).document(resObj.getResID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                resObjArrayList.remove(resObj);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }else {
                        System.out.println(resObjArrayList.size());
                    }

                }

            }
        }
    }
    private void  deleteCircle(LatLng latLng){
        for (Circle circle : circles){
            String circleLat = String.format("%.3f", circle.getCenter().latitude);
            String circleLong = String.format("%.3f", circle.getCenter().longitude);
            String latLat = String.format("%.3f", latLng.latitude);
            String latLong = String.format("%.3f", latLng.longitude);
            if(circleLat.equals(latLat) && circleLong.equals(latLong)){
                circle.remove();
            }
        }
    }

    private void handleMapLongClickAdd(LatLng latLng) {
        String lat = String.valueOf(latLng.latitude);
        String longi = String.valueOf(latLng.longitude);
        String rad = String.valueOf(GEOFENCE_RADIUS);
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("latitude", lat);
        hashMap.put("longitude", longi);
        hashMap.put("radius", rad);
        hashMap.put("isDone", String.valueOf(-1));
        hashMap.put("supervisornumber", supervisorNumber);
        firebaseFirestore.collection(userEmail).add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                addMarker(latLng);
                addCircle(latLng, GEOFENCE_RADIUS);
                getAllRes();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void handleMapLongClickDelete(LatLng latLng) {

        deleteMarker(latLng);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if (flag && flag2){
            handleMapLongClickAdd(latLng);

        }else if(flag && flag3){
            handleMapLongClickDelete(latLng);
        }else {
            System.out.println(resObjArrayList.size());
        }

    }

    public void enterRadius(View view){
        try {
            System.out.println("Test1");
            String stringRadiusText = radiusText.getText().toString();
            System.out.println("Test2 "+ stringRadiusText);
            if (stringRadiusText != null){
                GEOFENCE_RADIUS = Float.parseFloat(stringRadiusText);
                System.out.println("Test3 " + GEOFENCE_RADIUS);
            }
        }catch (Exception e){

        }
    }


    public void addRestriction(View view){
        if(flag == true && flag2 == false && flag3 == false){
            Toast.makeText(this,"Add restrictions on long click", Toast.LENGTH_SHORT).show();
            addClientButton.setText("Click To Cancel Add");
            deleteRestrictionButton.setVisibility(View.INVISIBLE);
            flag2 = true;
        }else {
            Toast.makeText(this,"Cancelled Add", Toast.LENGTH_SHORT).show();
            addClientButton.setText("Add Restriction");
            deleteRestrictionButton.setVisibility(View.VISIBLE);
            flag2 = false;
        }
    }

    public void deleteRestriction(View view){
        if(flag == true && flag2 == false && flag3 == false){
            Toast.makeText(this,"Delete restrictions on long click", Toast.LENGTH_SHORT).show();
            deleteRestrictionButton.setText("Click To Cancel Delete");
            addClientButton.setVisibility(View.INVISIBLE);
            flag3 = true;
        }else {
            Toast.makeText(this,"Cancelled Delete", Toast.LENGTH_SHORT).show();
            deleteRestrictionButton.setText("Delete Restriction");
            addClientButton.setVisibility(View.VISIBLE);
            flag3 = false;
        }
    }


    public void getAllRes(){
        resObjArrayList.clear();
        firebaseFirestore.collection(userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                        Map<String,Object> data;
                        data = queryDocumentSnapshot.getData();
                        String lat =(String) data.get("latitude");
                        String longi =(String) data.get("longitude");
                        String radi =(String) data.get("radius");
                        String isDone = (String) data.get("isDone");
                        String supervisorNumber = (String) data.get("supervisornumber");
                        resObjArrayList.add(new ResObj(lat,longi,radi,isDone, queryDocumentSnapshot.getId(),supervisorNumber));


           /*           Float fLat = Float.parseFloat(lat);
                        Float fLong = Float.parseFloat(longi);
                        Float fRadi = Float.parseFloat(radi);
                        LatLng latLng = new LatLng(fLat,fLong);
                        addCircle(latLng,fRadi);
                        addMarker(latLng);

            */
              //          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }

                }
            }
        });


    }

    public void drawAllRes(){
        for (ResObj resObj : resObjArrayList){
            Float fLat = Float.parseFloat(resObj.getLatitude());
            Float fLong = Float.parseFloat(resObj.getLongitude());
            Float fRadi = Float.parseFloat(resObj.getRadius());
            LatLng latLng = new LatLng(fLat,fLong);
            addCircle(latLng,fRadi);
            addMarker(latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }

    }


}