package com.example.chorerewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Allows a user to redeem a reward.
public class RedeemReward extends AppCompatActivity {

    Spinner spnFamilyMembers;
    ArrayList<String> familyMembers;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;

    TextView tvRewardToRedeem;
    TextView tvRewardPointValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_reward);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        Button createAccount = (Button) findViewById(R.id.btnChangeLTGSubmit);
        tvRewardToRedeem = (TextView) findViewById(R.id.tvRewardToRedeem);
        tvRewardPointValue = (TextView) findViewById(R.id.tvRewardPointValue);

        String rewardName = getIntent().getExtras().getString("reward");
        String pointValue = getIntent().getExtras().getString("pointValue");

        tvRewardToRedeem.setText(rewardName);
        tvRewardPointValue.setText(pointValue);

        ImageView ivCurrency = (ImageView) findViewById(R.id.ivCurrency);
        ivCurrency.setImageResource(R.drawable.ic_currency);
        ivCurrency.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500), PorterDuff.Mode.MULTIPLY);

        CollectionReference query = db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers");

        familyMembers = new ArrayList<String>();

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        familyMembers.add(document.getString("name"));

                        spnFamilyMembers = findViewById(R.id.spnFamilyMembers);
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnFamilyMembers.setAdapter(spinnerAdapter);

                        for (Object rewards : familyMembers) {
                            spinnerAdapter.add(rewards.toString());
                        }
                        spinnerAdapter.notifyDataSetChanged();

                    }
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String personRewardRedeemed = spnFamilyMembers.getSelectedItem().toString();

                db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").whereEqualTo("name", personRewardRedeemed).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document =  task.getResult().getDocuments().get(0);

                            DocumentReference member = db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").document(document.getId());

                            Long currentPoints = document.getLong("pointValue");
                            Long rewardPointValue = Long.valueOf(pointValue);
                            Long number = 0l;

                            if (currentPoints - rewardPointValue >= 0 )
                            {
                                number = currentPoints - rewardPointValue;

                                String rewardName = tvRewardToRedeem.getText().toString();

                                Map<String, Object> docData = new HashMap<>();
                                docData.put("rewardName", rewardName);
                                docData.put("rewardPointValue", rewardPointValue);

                                member.collection("rewards").document().set(docData);

                                member.update("pointValue", number)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RedeemReward.this, "Reward Redeemed!",
                                                        Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RedeemReward.this, MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error updating document", e);
                                            }
                                        });
                            }
                            else
                            {
                                Toast.makeText(RedeemReward.this, "Sorry, not enough points for this reward. :(",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RedeemReward.this, MainActivity.class));
                                finish();
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });
    }
}