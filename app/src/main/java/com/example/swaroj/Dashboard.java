package com.example.swaroj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dashboard extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String USER_ID;
    TextView name, phone, type;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);



        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        name = findViewById(R.id.textView_name_dash);
        phone = findViewById(R.id.textView_phone_dash);
        type = findViewById(R.id.textView_type_dash);



        USER_ID = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = firestore.collection("USERS").document(USER_ID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    name.setText(documentSnapshot.getString("NAME_"));
                    phone.setText(documentSnapshot.getString("EMAIL_"));
                    type.setText(documentSnapshot.getString("TYPE_"));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Profile data not found", Toast.LENGTH_SHORT);
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();
                }
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
                            //add to vulnerable list
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
        return super.onOptionsItemSelected(item);
    }
}
