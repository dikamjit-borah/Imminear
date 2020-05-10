package com.example.swaroj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity {
    Button login, register, reg_migrant, reg_local;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        onFirstRun();

        login = findViewById(R.id.button_login_home);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OTP_Login.class));
            }
        });

        register = findViewById(R.id.button_reg__home);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OTP_Register.class));
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();

        if(firebaseAuth.getCurrentUser()!=null)
        {
            DocumentReference documentReference = firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid());
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists())
                    {
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        finish();
                    }
                }
            });
        }


    }

    public void onFirstRun(){

        boolean ifFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("ifFirstRun", true);

        if(ifFirstRun)
        {
            String terms =
                    "Aenean vitae elementum odio. Cras eleifend lacus id nulla elementum, non aliquet neque condimentum. Integer sagittis maximus sapien, pretium vehicula lectus convallis et. Donec magna enim, condimentum sit amet cursus euismod, rhoncus ut elit. Nulla dolor nisl, hendrerit sed dapibus non, placerat sodales metus. Maecenas feugiat purus augue, pretium cursus sapien ultricies id. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam at porttitor purus. Donec id lorem eget odio gravida rhoncus.\n" +
                    "\n" +
                    "Donec tempor lacus eleifend, laoreet ipsum sed, semper tellus. Etiam efficitur risus metus, elementum aliquam ligula tincidunt a. In id condimentum metus. Vestibulum pharetra, dolor congue rhoncus ultricies, tellus turpis ultricies enim, sit amet dapibus ipsum purus in quam. Donec suscipit nisl et eros maximus, sed faucibus enim convallis. Etiam convallis velit id velit facilisis, ut hendrerit urna tincidunt. Nam porta sapien vel eleifend pellentesque. Nunc euismod eleifend dapibus. Donec mauris mauris, varius at eleifend nec, hendrerit ut libero.\n" +
                    "\n" +
                    "Suspendisse luctus sit amet lectus eget rhoncus. Donec in lorem non dolor blandit accumsan non ultricies neque. Interdum et malesuada fames ac ante ipsum primis in faucibus. Nulla tincidunt, dolor nec vestibulum maximus, lorem ante vulputate nunc, viverra scelerisque est odio id neque. Quisque lectus quam, luctus sit amet bibendum vel, facilisis vel orci. Vivamus porta nulla vitae mi posuere, scelerisque dignissim leo efficitur. Morbi aliquam tincidunt est sit amet faucibus. Donec vitae tortor sed ipsum rhoncus feugiat. Maecenas dictum ut nulla at tempor. Cras vitae iaculis lorem.";

            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            builder.setTitle("TERMS OF SERVICES");
            builder.setMessage(terms);
            builder.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("ifFirstRun", false).apply();

                }
            });
            builder.setNegativeButton("DO NOT AGREE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            });
            builder.show();
        }

    }
}
