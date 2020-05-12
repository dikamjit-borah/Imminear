package com.example.swaroj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String USER_ID, user_type;
    TextView name, phone, type;
    TextView coordinates_textview, safeornot_textview;
    int count = 0;
    double lat, lon;

    LocationManager locationManager;
    LocationListener locationListener;

    Button near_me, update_location;
    double sin_lat, sin_lon;
    ArrayList<Double> latitudes = new ArrayList<>();
    ArrayList<Double> longitudes = new ArrayList<>();

    double user_latitude = 0;
    double user_longitude = 0;
    double latitude_distance = 0;
    double longitude_distance = 0;
    final int radius = 6371;

    double a = 0;
    double c = 0;
    double dist = 0;
    ArrayList<Double> migrants_near = new ArrayList<>();


    private static final int PERMISSION_ID = 44;
    //FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);



        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        name = findViewById(R.id.textView_name_dash);
        phone = findViewById(R.id.textView_phone_dash);
        type = findViewById(R.id.textView_type_dash);
        coordinates_textview = findViewById(R.id.textView_coordinate_dash);
        safeornot_textview = findViewById(R.id.textView_safeornot_dash);

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Dashboard.this);
        //getLastLocation();

        onFirstRun();

        near_me = findViewById(R.id.imageButton1);
        near_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // coordinates_textview.setText("\n My Location \n" );

                onFirstRun();
                Intent intent = new Intent(getApplicationContext(),Near_Me.class);
                intent.putExtra("dash_lat", lat);
                intent.putExtra("dash_lon", lon);
               // intent.putExtra("dash_migrants_near", migrants_near);
                intent.putExtra("dash_count", migrants_near.size());
                startActivity(intent);
               // startActivity(new Intent(getApplicationContext(), Near_Me.class));
            }
        });

        USER_ID = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = firestore.collection("USERS").document(USER_ID);
        final DocumentReference documentReference2 = firestore.collection("MIGRANTS").document(USER_ID);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    name.setText(documentSnapshot.getString("NAME_"));
                    phone.setText(documentSnapshot.getString("EMAIL_"));
                    type.setText(documentSnapshot.getString("TYPE_"));
                    user_type = (documentSnapshot.getString("TYPE_"));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Profile data not found", Toast.LENGTH_SHORT);
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                }
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        else{
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    user_latitude = lat;
                    user_longitude = lon;
                    coordinates_textview.setText("My Location \n" + lat + ", " + lon);
                    try {
                        if(user_type.equals("MIGRANT"))
                        {
                            Map<String, Object> migrants_coordinates = new HashMap<>();
                            migrants_coordinates.put("latitude", user_latitude);
                            migrants_coordinates.put("longitude", user_longitude);
                            documentReference2.set(migrants_coordinates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {

                                        Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_SHORT).show();
                                        // startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                        //finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "Something's wrong ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Something's wrong "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }


        update_location = findViewById(R.id.imageButton2);
        update_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getLastLocation();
                try {
                if(user_type.equals("MIGRANT"))
                {
                    Map<String, Object> migrants_coordinates = new HashMap<>();
                    migrants_coordinates.put("latitude", user_latitude);
                    migrants_coordinates.put("longitude", user_longitude);
                    documentReference2.set(migrants_coordinates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                Toast.makeText(getApplicationContext(), "Location updated and inserted into database", Toast.LENGTH_SHORT).show();
                                // startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                //finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Something's wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Try again! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(), "Location updated", Toast.LENGTH_SHORT).show();
            }
        });

        isVulnerable();
    }

    private void isVulnerable() {

        String[] questions_array = new String[]{
                "Cough, Fever, Difficulty in breathing",
                "Travelled anywhere recently internationally in the last 14 days",
                "Interacted with someone tested COVID-19 positive",
                "are a healthcare worker"
        };
        final List<String> questions = Arrays.asList(questions_array);
        final boolean checked[] = new boolean[]{false, false, false, false, false};
        final List<Integer> itemsSelected = new ArrayList<>();

        final AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
        builder.setMultiChoiceItems(questions_array, checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checked[which] = isChecked;
                String current_item = questions.get(which);
                count++;

            }
        });
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (count < 2) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(Dashboard.this);
                    View builder1_view = getLayoutInflater().inflate(R.layout.custom_dialog2, null);
                    builder1.setView(builder1_view);
                    builder1.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder1.create().show();
                    Toast.makeText(getApplicationContext(), "Your infection risk is low. Stay at home", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(Dashboard.this);
                    View builder2_view = getLayoutInflater().inflate(R.layout.custom_dialog1, null);
                    builder2.setView(builder2_view);
                    builder2.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //add to migrant list
                        }
                    });
                    builder2.create().show();
                    Toast.makeText(getApplicationContext(), "TEST IMMEDIATELY", Toast.LENGTH_SHORT).show();

                }

            }
        });
        builder.setTitle("Which of the following is/are true?");
        builder.create().show();
    }

    private void is_near_me() {

        firestore.collection("MIGRANTS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    migrants_near.clear();
                    latitudes.clear();
                    longitudes.clear();
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        sin_lat = (double) document.get("latitude");
                        sin_lon = (double) document.get("longitude");
                        if(sin_lon==user_latitude && sin_lon==user_longitude)
                        {
                            continue;
                        }
                        else
                        {
                            latitudes.add(sin_lat);
                            longitudes.add(sin_lon);
                        }
                    }

                    is_near_me();
                }


            }
        });
        int i;
        for (i = 0; i<latitudes.size(); i++)
        {
            //System.out.println("For" + latitudes.get(i) + ", " + longitudes.get(i) + "\n");
            latitude_distance = (Math.toRadians(user_latitude - latitudes.get(i)));
            System.out.println(latitude_distance);
            longitude_distance = Math.toRadians(user_longitude - longitudes.get(i));
            System.out.println(longitude_distance);

            a = Math.abs(Math.pow(Math.sin(latitude_distance / 2), 2)
                    + Math.cos(user_latitude) * Math.cos(latitudes.get(i))
                    * Math.pow(Math.sin(longitude_distance / 2),2));
            System.out.println("a = " + a + "\n");

            c = 2 * Math.asin(Math.sqrt(a));
            System.out.println("c = " + c + "\n");
            //height = e2 - e1;
            dist = c * radius;
            dist = dist* 1000;//converts to meters

            System.out.println("dist = "+ dist + "\n");
            if (dist > 500) {
                //do nothing

            } else if (dist < 500) {
                if(dist!=0)
                    migrants_near.add(dist);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.custom_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout_menu)
        {
            firebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), Home.class));
            Toast.makeText(getApplicationContext(), "Successfully signed out", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(item.getItemId() == R.id.privacy_menu)
        {
            String terms =
                    "SWAROJ takes the responsibility of having your personal information very seriously. It uses " +
                            "your personal information for only improving the Service. It keeps your personal " +
                            "information as its own and keeps it confidential. By using the Service, you consent to the " +
                            "collection and use of information in accordance with this Policy. " +
                            " \n\n " +
                            "SWAROJ reserves the right, at its sole discretion, to modify or replace this Policy by posting " +
                            "the updated version on the Site. It is your responsibility to check this Policy periodically for " +
                            "changes. Your continued use of the Service following the posting of any changes to this " +
                            "Policy constitutes your acceptance of those changes. " +
                            " \n\n " +
                            "Acquiring of Information " +
                            "We may acquire the following information about you. " +
                            "Information (such as your name, telephone number) that you provide while booking on the " +
                            "application. " +
                            "Your log-in which is done using your mobile no which is in connection with the account " +
                            "sign-in process; " +
                            "Details of any requests or transactions made by you through the Service; " +
                            "Communications you send to us, for example to report a problem or to submit queries, " +
                            "concerns, or comments regarding the Service or its content; " +
                            "Information that you post to the application in the form of comments or contributions to " +
                            "discussions and IP addresses " +
                            " \n\n " +
                            "Uses of Your Personal Information " +
                            "We will use the personal information that you provide for: " +
                            "Identifying you when you book our service; " +
                            "Enable us to provide you with the services; " +
                            "Send you information we think you may find useful or which you have requested from us " +
                            " " +
                            "E-Mail &amp; Mobile No " +
                            "We try to keep emails/sms to a minimum and give you the freedom to opt out when we can. " +
                            "We will send you email/sms relating to your personal transactions. We will keep these " +
                            "emails/sms to a minimum. You will also receive certain email/sms notifications, for which " +
                            "you may opt-out. We may send you service-related announcements on rare occasions when " +
                            "it is necessary to do so. " +
                            " \n\n " +
                            "Third Party Services " +
                            "We never post anything to your accounts with Facebook, Twitter or any other third-party " +
                            "sites without your permission. Except for the purposes of providing the Services, we will not " +
                            "give your name or personal information to third parties. " +
                            " \n\n " +
                            "GPS Services:- " +
                            "We will taking access of the location of the users of both people who came from outside as " +
                            "well as of local people so that we will be getting a track within a range of 500 meters which " +
                            "will further give alert to local users. Moreover the exact location of people coming from " +
                            "outside will not be shared to local people keeping in touch with the security issues. Only " +
                            "alerts will be given to local people users. Moreover the name and other details of Migrant " +
                            "people will not be shared with any local user.";

            AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
            builder.setTitle("PRIVACY POLICY");
            builder.setMessage(terms);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            });

            builder.show();
        }
        else if(item.getItemId() == R.id.call_menu)
        {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:7002936200"));
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.acc_del_menu)
        {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:7002936200"));
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    public void onFirstRun(){

        boolean ifFirstRun = getSharedPreferences("PREFERENCE_dash", MODE_PRIVATE).getBoolean("ifFirstRun_dash", true);

        if(ifFirstRun)
        {
            SharedPreferences sp = getSharedPreferences("count_dash", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("my_int_key", 0);
            editor.apply();
            getSharedPreferences("PREFERENCE_dash", MODE_PRIVATE).edit().putBoolean("ifFirstRun_dash", false).apply();

        }
        else
        {
            SharedPreferences sp = getSharedPreferences("count_dash", Activity.MODE_PRIVATE);
            int myIntValue = sp.getInt("my_int_key", -1);

            if(myIntValue == 0) {
                safeornot_textview.setText("No migrant people currently nearby.\n Click near me to refresh.");
                safeornot_textview.setTextColor(Color.GREEN);
            }
            else if(myIntValue > 0 && myIntValue<4)
            {
                safeornot_textview.setText("Some migrant people currently nearby.\n Click near me to refresh.");
                safeornot_textview.setTextColor(Color.MAGENTA);
            }
            else
            {
                safeornot_textview.setText("Many migrant people currently nearby.\n Click near me to refresh.");
                safeornot_textview.setTextColor(Color.RED);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        onFirstRun();
    }
}
