package com.example.chorerewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Allows a user to create an account.
public class AccountCreation extends AppCompatActivity implements AddFamilyMemberFragment.AddFamilyMemberListener {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    EditText etAccountHolderFamilyName;
    String accountHolderFamilyName;
    String accountHolderEmail;

    Button createAccount;
    ArrayList<FamilyMember> familyMembers;
    AddFamilyMemberAdapter familyMemberAdapter;
    ListView lvFamilyMembers;
    FamilyMember familyMember;

    EditText etLTGNameAccountCreation;
    String longTermGoal;
    EditText etLTGPointValueAccountCreation;

    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        familyMembers = new ArrayList<FamilyMember>();
        familyMemberAdapter = new AddFamilyMemberAdapter(getApplicationContext(), familyMembers);

        Button addFamilyMember = (Button) findViewById(R.id.btnAddChore);
        lvFamilyMembers = (ListView) findViewById(R.id.lvFamilyMembers);
        lvFamilyMembers.setAdapter(familyMemberAdapter);

        etLTGNameAccountCreation = (EditText) findViewById(R.id.etLTGNameAccountCreation);


        addFamilyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DialogFragment newFragment = new AddFamilyMemberFragment();
                    newFragment.show(getSupportFragmentManager(), "missiles");
            }
        });

        createAccount = (Button) findViewById(R.id.btnChangeLTGSubmit);
        etAccountHolderFamilyName = (EditText) findViewById(R.id.tvRewardToRedeem);
        etLTGPointValueAccountCreation = (EditText) findViewById(R.id.etLTGPointValueAccountCreation);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                accountHolderFamilyName = etAccountHolderFamilyName.getText().toString();
                longTermGoal = etLTGNameAccountCreation.getText().toString();
                accountHolderEmail = mFirebaseUser.getEmail();
                long longTermGoalPointValue = Long.valueOf(etLTGPointValueAccountCreation.getText().toString());

                Map<String, Object> docData = new HashMap<>();
                docData.put("accountHolderFamilyName", accountHolderFamilyName);
                docData.put("accountHolderEmail", accountHolderEmail);
                docData.put("LTGPointValue", longTermGoalPointValue);
                docData.put("LTGProgressPointValue", 0);
                docData.put("totalPointValue", 0);
                docData.put("longTermGoal", longTermGoal);

                Toast.makeText(AccountCreation.this, "Account Created!",
                        Toast.LENGTH_SHORT).show();


                /*
                docData.put("booleanExample", true);
                docData.put("numberExample", 3.14159265);
                docData.put("dateExample", new Timestamp(new Date()));
                docData.put("listExample", Arrays.asList(1, 2, 3));
                docData.put("nullExample", null);

                Map<String, Object> nestedData = new HashMap<>();
                nestedData.put("a", 5);
                nestedData.put("b", true);

                docData.put("objectExample", nestedData);*/

                db.collection("users").document(mFirebaseUser.getEmail())
                        .set(docData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("CHANTAL", "DocumentSnapshot successfully written!");

                                for (FamilyMember familyMember : familyMembers) {

                                    Map<String, Object> docData = new HashMap<>();
                                    docData.put("name", familyMember.getName());
                                    docData.put("pointValue", familyMember.getPointValue());

                                    db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").document()
                                            .set(docData);
                                }
                                        db.collection("chores").document("1").collection("chore").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List chore = queryDocumentSnapshots.toObjects(Chore.class);

                                                for (Object element : chore) {
                                                    Chore c = (Chore)element;
                                                    Map<String, Object> documentData = new HashMap<>();
                                                    String choreName = c.getName();
                                                    documentData.put("name", choreName);
                                                    documentData.put("pointValue", 1);
                                                    db.collection("users").document(mFirebaseUser.getEmail()).collection("chores").document()
                                                            .set(documentData)
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
                                                    Log.d("LOG", c.getName());
                                                    Log.d("LOG", String.valueOf(c.getPointValue()));
                                                }
                                            }
                                        });
                                        db.collection("chores").document("7").collection("chore").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List chore = queryDocumentSnapshots.toObjects(Chore.class);

                                                for (Object element : chore) {
                                                    Chore c = (Chore)element;
                                                    Map<String, Object> documentData = new HashMap<>();
                                                    String choreName = c.getName();
                                                    documentData.put("name", choreName);
                                                    documentData.put("pointValue", 3);
                                                    db.collection("users").document(mFirebaseUser.getEmail()).collection("chores").document()
                                                            .set(documentData)
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
                                                    Log.d("LOG", c.getName());
                                                    Log.d("LOG", String.valueOf(c.getPointValue()));
                                                }
                                            }
                                        });
                                        db.collection("chores").document("30").collection("chore").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List chore = queryDocumentSnapshots.toObjects(Chore.class);

                                                for (Object element : chore) {
                                                    Chore c = (Chore)element;
                                                    Map<String, Object> documentData = new HashMap<>();
                                                    String choreName = c.getName();
                                                    documentData.put("name", choreName);
                                                    documentData.put("pointValue", 7);
                                                    db.collection("users").document(mFirebaseUser.getEmail()).collection("chores").document()
                                                            .set(documentData)
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
                                                    Log.d("LOG", c.getName());
                                                    Log.d("LOG", String.valueOf(c.getPointValue()));
                                                }
                                            }
                                        });
                                        db.collection("chores").document("90").collection("chore").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List chore = queryDocumentSnapshots.toObjects(Chore.class);

                                                for (Object element : chore) {
                                                    Chore c = (Chore)element;
                                                    Map<String, Object> documentData = new HashMap<>();
                                                    String choreName = c.getName();
                                                    documentData.put("name", choreName);
                                                    documentData.put("pointValue", 10);

                                                    db.collection("users").document(mFirebaseUser.getEmail()).collection("chores").document()
                                                            .set(documentData)
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
                                                    Log.d("LOG", c.getName());
                                                    Log.d("LOG", String.valueOf(c.getPointValue()));
                                                }
                                            }
                                        });

                                    }
                                });
                                startActivity(new Intent(AccountCreation.this, MainActivity.class));
                                finish();
                            }
                        });

            }

    @Override
    public void addFamilyMember(String familyMember) {

        FamilyMember newFamilyMember = new FamilyMember();
        newFamilyMember.setName(familyMember);

        familyMembers.add(newFamilyMember);

        ViewGroup.LayoutParams params = lvFamilyMembers.getLayoutParams();
        params.height = params.height + 110;
        lvFamilyMembers.setLayoutParams(params);

        for (FamilyMember s : familyMembers){
            Log.d("My array list content: ", s.getName());
        }

        Log.d("PARAMS", String.valueOf(lvFamilyMembers.getLayoutParams().height));
    }
}