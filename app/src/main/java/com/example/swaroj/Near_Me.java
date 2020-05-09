package com.example.swaroj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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
    ArrayList<Double> migrants_near = new ArrayList<>();


    String test;
   ArrayList<String> coor = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near__me);
        count = findViewById(R.id.textView_count_near);
        coor_nearme = findViewById(R.id.textView_coordinate_near);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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
                        coor.add((sin_lat +" "+sin_lon));
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
                firestore.collection("MIGRANTS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            coor.clear();
                            for(QueryDocumentSnapshot document:task.getResult())
                            {
                                sin_lat = (double) document.get("latitude");
                                sin_lon = (double) document.get("longitude");
                                latitudes.add(sin_lat);
                                longitudes.add(sin_lon);

                                //test.concat(String.valueOf(sin_lat));
                                // coordinates.setText("" + sin_lon);
                                //Toast.makeText(getApplicationContext(), (sin_lat +" "+sin_lon), Toast.LENGTH_SHORT).show();
                                coor.add((sin_lat +" "+sin_lon));
                            }
                            is_near_me();
                        }


                    }
                });
                count.setText(coor.size() + " people near me");
                coor_nearme.setText("" + migrants_near);


                int i, j;

                count.setTextColor(Color.RED);

            }
        });
    }

    private void is_near_me() {
        int i, j;
        for (i = 0, j=0; i<latitudes.size()&& j<longitudes.size(); i++, j++)
        {
            latitude_distance = Math.toRadians(user_latitude - latitudes.get(i));
            longitude_distance = Math.toRadians(user_longitude - longitudes.get(j));

            double a = Math.pow(Math.sin(latitude_distance / 2), 2)
                    + Math.cos(user_latitude) * Math.cos(latitudes.get(i))
                    * Math.pow(Math.sin(longitude_distance / 2),2);

            double c = 2 * Math.asin(Math.sqrt(a));
            //height = e2 - e1;
            double dist = c * radius;
            dist = dist* 1000;//converts to meters

            if (dist > 500) {
                //do nothing

            } else if (dist < 500) {
                migrants_near.add(dist);
            }
        }
    }
}
