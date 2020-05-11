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

            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            builder.setTitle("PRIVACY POLICY");
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
