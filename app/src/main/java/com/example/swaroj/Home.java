package com.example.swaroj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.text.Html;
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

            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            builder.setTitle("PRIVACY POLICY");
            builder.setMessage( Html.fromHtml(getString(R.string.pp1) +"<br>" + "<b>"+ getString(R.string.aqquire)+ "<br>" +"</b>" + getString(R.string.pp2)+ "<br>" +"<b>" + getString(R.string.personal)+"</b>" + "<br>" + getString(R.string.pp3)));

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
