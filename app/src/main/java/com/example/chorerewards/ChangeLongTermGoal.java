package com.example.chorerewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

//Gives the user the ability to change their long term goal
public class ChangeLongTermGoal extends AppCompatActivity {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_long_term_goal);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        String LTGName = getIntent().getExtras().getString("LTGname");
        String LTGPointValue = getIntent().getExtras().getString("tvLTGTotalPointsNumber");
        String LTGProgressPoints = getIntent().getExtras().getString("tvLTGProgressPoints");

        TextView etLTGName = (TextView) findViewById(R.id.etLTGNameChangeLTG);
        etLTGName.setHint(LTGName);
        TextView etPointsRequired = (TextView) findViewById(R.id.etPointsRequired);
        etPointsRequired.setHint(LTGPointValue);
        TextView etStartingPoints = (TextView) findViewById(R.id.etStartingPoints);
        etStartingPoints.setHint(LTGProgressPoints);

        Button btnChangeLTGSubmit = (Button) findViewById(R.id.btnChangeLTGSubmit);

        btnChangeLTGSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChangeLongTermGoal.this, "Long Term Goal Changed!",
                        Toast.LENGTH_SHORT).show();

                String newLTGName = etLTGName.getText().toString();
                Long pointsRequired = 1l;
                Long startingPoints = 1l;

                if (etPointsRequired.getText().length() != 0)
                {
                    pointsRequired = Long.valueOf(etPointsRequired.getText().toString());
                }

                if (etStartingPoints.getText().length() != 0)
                {
                    startingPoints = Long.valueOf(etStartingPoints.getText().toString());
                }

                db.collection("users").document(mFirebaseUser.getEmail())
                        .update("longTermGoal", newLTGName,
                                "LTGPointValue", pointsRequired,
                                "LTGProgressPointValue", startingPoints)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("CHANTAL", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("CHANTAL", "Error writing document", e);
                            }
                        });

                Intent intent = new Intent(new Intent(ChangeLongTermGoal.this, MainActivity.class));
                startActivity(intent);
                finish();
            }
        });
    }
}