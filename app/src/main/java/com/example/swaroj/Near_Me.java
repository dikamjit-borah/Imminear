package com.example.swaroj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Near_Me extends AppCompatActivity {

    TextView count, coor_nearme;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    Button refresh;

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
    String USER_ID;

    String test;
    String user_type;
   ArrayList<String> coor = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near__me);
        count = findViewById(R.id.textView_count_near);
        coor_nearme = findViewById(R.id.textView_coordinate_near);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        user_latitude = intent.getDoubleExtra("dash_lat", 0);
        user_longitude = intent.getDoubleExtra("dash_lon", 0);
        USER_ID = firebaseAuth.getCurrentUser().getUid();

        final DocumentReference documentReference = firestore.collection("USERS").document(USER_ID);
        final DocumentReference documentReference2 = firestore.collection("MIGRANTS").document(USER_ID);

       documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               if(documentSnapshot.exists())
               {
                   user_type = (documentSnapshot.getString("TYPE_"));
                   Toast.makeText(getApplicationContext(), "" + user_type, Toast.LENGTH_SHORT).show();
               }
           }
       });

        firestore.collection("MIGRANTS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        sin_lat = (double) document.get("latitude");
                        sin_lon = (double) document.get("longitude");
                        latitudes.add(sin_lat);
                        longitudes.add(sin_lon);
                        //test.concat(String.valueOf(sin_lat));
                       // coordinates.setText("" + sin_lon);
                        //Toast.makeText(getApplicationContext(), (sin_lat +" "+sin_lon), Toast.LENGTH_SHORT).show();
                        //coor.add((sin_lat +" "+sin_lon));
                    }
                }


            }
        });
        count.setText(coor.size() + " people near me");
        coor_nearme.setText("" + coor);
        count.setTextColor(Color.RED);

        refresh = findViewById(R.id.button_refresh_near);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Updating", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Something's wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

                //Toast.makeText(getApplicationContext(), user_latitude+", " + user_longitude, Toast.LENGTH_SHORT).show();
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

                                //test.concat(String.valueOf(sin_lat));
                                // coordinates.setText("" + sin_lon);
                                //Toast.makeText(getApplicationContext(), (sin_lat +" "+sin_lon), Toast.LENGTH_SHORT).show();
                                coor.add((sin_lat +" "+sin_lon));
                            }
                            //Toast.makeText(getApplicationContext(), latitudes + " ", Toast.LENGTH_SHORT).show();
                            is_near_me();
                        }


                    }
                });

                String distances = "";
                /*for(int i = 0; i< migrants_near.size(); i++)
                {
                    distances.concat(String.valueOf( String.format("%.2f", migrants_near.get(i))));
                }*/


            }
        });
    }

    private void is_near_me() {
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



            if(migrants_near.size() == 0)

                count.setText("No migrant people near me. I am the only one...");
            else {
                count.setText(migrants_near.size() + " people near me");
                coor_nearme.setText("at approximately" + migrants_near + "metres ");
            }
            count.setTextColor(Color.RED);
        }
    }
}
