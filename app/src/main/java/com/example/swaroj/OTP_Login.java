package com.example.swaroj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class OTP_Login extends AppCompatActivity {
    Button get_otp, login;
    EditText input_phone, input_otp;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    String phone_str;

    String verificationID;
    PhoneAuthProvider.ForceResendingToken Token;

    Boolean authenticated = false;
    Boolean verifying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp__login);


        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        get_otp = findViewById(R.id.button_getOTP_otp_login);
        input_phone = findViewById(R.id.editText_phone_otp_login);
        input_otp = findViewById(R.id.editText_input_otp_otp_login);
        progressBar = findViewById(R.id.progressBar_otp_login);

        input_otp.setVisibility(View.INVISIBLE);
        login = findViewById(R.id.button_login_otp_login);
        login.setVisibility(View.INVISIBLE);

        get_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!input_phone.getText().toString().isEmpty() && input_phone.getText().toString().length()==10)
                {
                    phone_str = "+91" + input_phone.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    get_otp.setEnabled(false);
                    get_otp.setTextColor(Color.GRAY);
                    Toast.makeText(getApplicationContext(),"Sending OTP to " + phone_str, Toast.LENGTH_SHORT).show();
                    requestOTP(phone_str);

                }
                else
                {
                    input_phone.setError("Phone Number is blank or incorrect");
                    return;
                }



                // startActivity(new Intent(getApplicationContext(), Registration.class));
                //finish();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input_otp.getText().toString().isEmpty() || input_otp.getText().toString().length()!=6)
                {
                    input_otp.setError("OTP is invalid or empty");
                    return;
                }
                else
                {
                    String user_input_otp = input_otp.getText().toString();
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID,user_input_otp);
                    verifyOTP(phoneAuthCredential);
                }
            }
        });

    }

    private void verifyOTP(PhoneAuthCredential phoneAuthCredential) {

        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    authenticated = true;

                    DocumentReference documentReference = firestore.collection("USERS").document(firebaseAuth.getCurrentUser().getUid());
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists())
                            {
                                startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Profile does not exist", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Credentials invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestOTP(String phone_str) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_str, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationID = s;
                Token = forceResendingToken;

                Toast.makeText(getApplicationContext(),"OTP sent", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                input_otp.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);


            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                verifyOTP(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(),"Authentication unsuccessful \n" + e.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        });
    }
}
