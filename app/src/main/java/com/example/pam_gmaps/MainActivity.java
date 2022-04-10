package com.example.pam_gmaps;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.bundle.NamedQueryOrBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener {

    private int ACCESS_LOCATION_REQUEST_CODE = 1001;
   private GoogleMap gMap;
    private Marker selectedMarker;
    private LatLng selectedPlace;
    double latitude,longitude;

    private  FirebaseFirestore db;
    private TextView txtOrderId, txtSelectedPlace,txt_CurrentLocation,txtlong,txtlat;
    private EditText editTextName;
    private Button btnEditOrder, btnOrder,btn_list;
    private boolean isNewOrder = true;

    FusedLocationProviderClient client;

    Button button_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtOrderId = findViewById(R.id.txt_orderId);
        txtSelectedPlace = findViewById(R.id.txt_selectedPlace);
        editTextName = findViewById(R.id.editTxt_name);
        btnEditOrder = findViewById(R.id.btn_editOrder);
        btnOrder = findViewById(R.id.btn_order);


        btn_list = findViewById(R.id.btn_data);

        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, List_Data.class));
            }
        });

        txt_CurrentLocation = findViewById(R.id.txt_currentLocation);
        db = FirebaseFirestore.getInstance();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
       supportMapFragment.getMapAsync(this);

        btnOrder.setOnClickListener(view -> { saveOrder(); });
        btnEditOrder.setOnClickListener(view -> { updateOrder(); });

        client = LocationServices.getFusedLocationProviderClient(this);
      //  mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


      //  textView_location = findViewById(R.id.text_location);
        button_location = findViewById(R.id.button_location);
        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION},44);
                }
            }

            @SuppressLint("MissingPermission")
            private void getLocation() {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location= task.getResult();
                    if (location!=null){
                        try {
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            txt_CurrentLocation.setText( addresses.get(0).getAddressLine(0));

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                    }
                }
            });
            }
        });





    }




    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            gMap.setMyLocationEnabled(true);

        }

        else{
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},ACCESS_LOCATION_REQUEST_CODE);

//            }else{
//                ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},ACCESS_LOCATION_REQUEST_CODE);
//            }
        }

        LatLng latLn= new LatLng(latitude,longitude);
        selectedPlace = latLn;
        selectedMarker = gMap.addMarker(new MarkerOptions().position(selectedPlace));
        gMap.setOnMapClickListener(this);
    }


//    @SuppressLint("MissingPermission")
//    private void enableuserlocation(){
//
//        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
//            return;
//        }
//
//        gMap.setMyLocationEnabled(true);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == ACCESS_LOCATION_REQUEST_CODE){
//            if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED){
//                enableuserlocation();
//            }else{
//
//            }
//        }
//    }







    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        selectedPlace = latLng;
        selectedMarker.setPosition(selectedPlace);
//        selectPM.setPosition(herePlace);
        gMap.animateCamera(CameraUpdateFactory.newLatLng(selectedPlace));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(selectedPlace.latitude, selectedPlace.longitude, 1);


            if (addresses != null  ) {
                Address place = addresses.get(0);

                StringBuilder street = new StringBuilder();

                for (int i=0; i <= place.getMaxAddressLineIndex(); i++) {
                    street.append(place.getAddressLine(i)).append("\n");

                }


                txtSelectedPlace.setText(street.toString());

            }
            else {
                Toast.makeText(this, "Could not find Address!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Error get Address!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOrder() {
        Map<String, Object> order = new HashMap<>();
        Map<String, Object> place = new HashMap<>();

        String name = editTextName.getText().toString();


        place.put("Asal", txt_CurrentLocation.getText().toString());
//        place.put("lat", txtlat.getText().toString());
//        place.put("lng", txtlong.getText().toString());

        place.put("Tujuan", txtSelectedPlace.getText().toString());
        place.put("lat", selectedPlace.latitude);
        place.put("lng", selectedPlace.longitude);


        order.put("name", name);
        order.put("createdDate", new Date());
        order.put("place", place);

        String orderId = txtOrderId.getText().toString();

        if (isNewOrder) {
            db.collection("orders")
                    .add(order)
                    .addOnSuccessListener(documentReference -> {
                        editTextName.setText("");
                        txtSelectedPlace.setText("Pilih tempat");
                   //     txt_CurrentLocation.setText("asal");
                        txtOrderId.setText(documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal tambah data order", Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            db.collection("orders").document(orderId)
                    .set(order)
                    .addOnSuccessListener(unused -> {
                        editTextName.setText("");
                        txtSelectedPlace.setText("");
                        txt_CurrentLocation.setText("");
                        txtOrderId.setText(orderId);

                        isNewOrder = true;
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal ubah data order", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateOrder() {
        isNewOrder = false;

        String orderId = txtOrderId.getText().toString();
        DocumentReference order = db.collection("orders").document(orderId);
        order.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.get("name").toString();
                    Map<String, Object> place = (HashMap<String, Object>) document.get("place");

                    editTextName.setText(name);
                    txtSelectedPlace.setText(place.get("address").toString());


                    LatLng resultPlace = new LatLng((double) place.get("lat"), (double) place.get("lng"));
                    selectedPlace = resultPlace;

                    selectedMarker.setPosition(selectedPlace);
                    gMap.animateCamera(CameraUpdateFactory.newLatLng(selectedPlace));
                }
                else {
                    isNewOrder = true;
                    Toast.makeText(this, "Document does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Unable to read the db!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}