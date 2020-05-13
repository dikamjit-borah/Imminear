package com.example.swaroj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String prev_name, prev_email, prev_type;
    EditText p_name, p_email;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        p_name = findViewById(R.id.editText_new_name_set);
        p_email = findViewById(R.id.editText_new_email_set);
        update = findViewById(R.id.button_update_set);
        String USER_ID= firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = firebaseFirestore.collection("USERS").document(USER_ID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                prev_name = (documentSnapshot.getString("NAME_"));
                prev_email = (documentSnapshot.getString("EMAIL_"));
                prev_type = (documentSnapshot.getString("TYPE_"));

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p_name.getText().toString().isEmpty() || p_email.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Map<String, Object> new_details = new HashMap<>();
                    new_details.put("NAME_", p_name.getText().toString());
                    new_details.put("EMAIL_", p_email.getText().toString());
                    new_details.put("TYPE_", prev_type);

                    documentReference.set(new_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "Details updated. Restart app.", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Details could not updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



    }
}
