package com.example.swaroj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registration_migrant extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseDatabase firebaseDatabase;

    EditText name, email, address, lastloc;
    Button submit;

    String name_string, email_string, address_string, lastloc_string;
    String USER_ID;

    double lat, lon, alt;



    private static final int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_migrant);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Registration_migrant.this);
       // getLastLocation();

        name = findViewById(R.id.editText_name_reg_mig);
        email = findViewById(R.id.editText_email_reg_mig);
        address = findViewById(R.id.editText_address_reg_mig);
        lastloc = findViewById(R.id.editText_lastLoc_reg_mig);
        submit = findViewById(R.id.button_submit_reg_mig);
        address.setFocusable(false);
        address.setClickable(true);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
                Toast.makeText(getApplicationContext(), "Get current location", Toast.LENGTH_SHORT).show();
            }
        });
        USER_ID = firebaseAuth.getCurrentUser().getUid();

        final DocumentReference documentReference = firestore.collection("USERS").document(USER_ID);
        final DocumentReference documentReference2 = firestore.collection("MIGRANTS").document(USER_ID);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_string = name.getText().toString();
                email_string = email.getText().toString();
                address_string = address.getText().toString();
                lastloc_string = lastloc.getText().toString();
                if(name_string.isEmpty() || email_string.isEmpty() || address_string.isEmpty() || lastloc_string.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Provide complete details", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
//                    DatabaseReference ref = firebaseDatabase.getReference("Coordinates\n"+ USER_ID);
//                    coordinates coordinates = new coordinates(lat,lon,alt);
//                    ref.setValue(name);

                    Map<String, Object> migrants = new HashMap<>();
                    migrants.put("NAME_", name_string);
                    migrants.put("EMAIL_", email_string);
                    migrants.put("ADDRESS_", address_string);
                    migrants.put("LAST_LOCATION_", lastloc_string);
                    migrants.put("TYPE_", "MIGRANT");

                    Map<String, Object> migrants_coordinates = new HashMap<>();
                    migrants_coordinates.put("latitude", lat);
                    migrants_coordinates.put("longitude", lon);
                    documentReference2.set(migrants_coordinates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
                                // startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                //finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Account could not be created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    documentReference.set(migrants).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                //Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Account could not be created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                    address.setText(location.getLatitude()+", "+location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
            address.setText(mLastLocation.getLatitude()+", "+mLastLocation.getLongitude()+"");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }



}
