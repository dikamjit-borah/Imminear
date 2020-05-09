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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTP_Register extends AppCompatActivity {

    Button get_otp, reg_migrant, reg_local;
    EditText input_phone, input_otp;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    String phone_str;

    String verificationID;
    PhoneAuthProvider.ForceResendingToken Token;

    Boolean authenticated = false;
    Boolean verifying = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp__register);


        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar_otp_reg);
        get_otp = findViewById(R.id.button_getOTP_otp_reg);
        reg_migrant = findViewById(R.id.button_reg_migrant_otp_reg);
        reg_local = findViewById(R.id.button_reg_local_otp_reg);
        input_phone = findViewById(R.id.editText_phone_otp_reg);
        input_otp = findViewById(R.id.editText_input_otp_otp_reg);

        input_otp.setVisibility(View.INVISIBLE);
        reg_migrant.setVisibility(View.INVISIBLE);
        reg_local.setVisibility(View.INVISIBLE);

        get_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!input_phone.getText().toString().isEmpty() && input_phone.getText().toString().length()==10)
                {
                    if(!verifying) {
                        get_otp.setEnabled(false);
                        get_otp.setTextColor(Color.GRAY);
                        progressBar.setVisibility(View.VISIBLE);
                        phone_str = "+91" + input_phone.getText().toString();
                        Toast.makeText(getApplicationContext(), "Sending OTP to " + phone_str, Toast.LENGTH_SHORT).show();
                        requestOTP(phone_str);
                    }
                    else
                    {
                        String user_otp_input = input_otp.getText().toString();
                        if(user_otp_input.isEmpty())
                        {
                            input_otp.setError("Enter valid OTP");
                        }
                        else
                        {
                            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID, user_otp_input);
                            verifyOTP(phoneAuthCredential);
                        }

                    }
                }
                else
                {

                    input_phone.setError("Phone Number is blank or incorrect");
                    return;
                }

            }
        });

        reg_migrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(authenticated)
                {
                    startActivity(new Intent(getApplicationContext(), Registration_migrant.class));
                    finish();
                }

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

        reg_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(authenticated)
                {
                    startActivity(new Intent(getApplicationContext(), Registration.class));
                    finish();
                }

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
                    progressBar.setVisibility(View.INVISIBLE);
                    input_otp.setVisibility(View.VISIBLE);
                    reg_migrant.setVisibility(View.VISIBLE);
                    reg_local.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(),"Authentication successful", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Authentication unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*    private void make_elements_visible() {
        progressBar.setVisibility(View.INVISIBLE);
        input_otp.setVisibility(View.VISIBLE);
        reg_migrant.setVisibility(View.VISIBLE);
        reg_local.setVisibility(View.VISIBLE);
        get_otp.setEnabled(false);
        get_otp.setTextColor(Color.DKGRAY);
    }*/

    private void requestOTP(String phone_str)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_str, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verifying = true;
                progressBar.setVisibility(View.INVISIBLE);
                input_otp.setVisibility(View.VISIBLE);
                reg_migrant.setVisibility(View.VISIBLE);
                reg_local.setVisibility(View.VISIBLE);

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

            }
        });
    }
}
