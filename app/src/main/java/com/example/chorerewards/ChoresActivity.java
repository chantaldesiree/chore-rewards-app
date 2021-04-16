package com.example.chorerewards;

import android.app.Person;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//A specific family member's chores.
public class ChoresActivity extends AppCompatActivity implements AddChoreFragment.AddChoreListener, RemoveChoreFragment.RemoveChoreListener {

    private final int STANDARD_REQUEST_CODE = 0;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    ChoreAdapter dailyAdapter;
    ChoreAdapter weeklyAdapter;
    ChoreAdapter monthlyAdapter;
    ChoreAdapter seasonalAdapter;

    ArrayList<Chore> daily;
    ArrayList<Chore> weekly;
    ArrayList<Chore> monthly;
    ArrayList<Chore> seasonally;

    private ListView lvDaily;
    private ListView lvWeekly;
    private ListView lvMonthly;
    private ListView lvSeasonally;

    Button points;

    Chore chore;

    @Override
    public void onBackPressed() {
        refreshActivity();
        super.onBackPressed();
    }

    public void refreshActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chores);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        String fmName = getIntent().getExtras().getString("name");
        TextView fmChores = findViewById(R.id.tvChores);
        fmChores.setText(fmName + "'s Chores");

        Button btnPersonalRewards = (Button) findViewById(R.id.btnPersonalRewards);

        btnPersonalRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoresActivity.this, PersonalRewards.class);
                intent.putExtra("name", fmName);
                startActivity(intent);
            }
        });


        Button addChore = (Button) findViewById(R.id.btnAddChore);
        addChore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddChoreFragment();
                newFragment.show(getSupportFragmentManager(), "missiles");
            }
        });

        Button btnRemoveChore = (Button) findViewById(R.id.btnRemoveChore);
        btnRemoveChore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new RemoveChoreFragment();
                newFragment.show(getSupportFragmentManager(), "missiles");
            }
        });

        lvDaily = (ListView) findViewById(R.id.lvDaily);

        daily = new ArrayList<>();

        db.collection("users").document(mFirebaseUser.getEmail()).collection("chores").orderBy("pointValue", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Log.d("GOT IN", "HERE");
                        dailyAdapter = new ChoreAdapter(getApplicationContext(), daily);

                        chore = new Chore();
                        chore.setName(document.getString("name"));
                        chore.setPointValue(document.getLong("pointValue"));

                        Long dailyChorePointValue = chore.getPointValue();

                        daily.add(chore);

                        lvDaily.setAdapter(dailyAdapter);

                        setListViewHeightBasedOnItems(lvDaily);

                        lvDaily.setOnItemClickListener(new ListView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Chore obj = (Chore) lvDaily.getAdapter().getItem(position);
                                String value = obj.getName();

                                db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").whereEqualTo("name", fmName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document =  task.getResult().getDocuments().get(0);

                                                DocumentReference member = db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").document(document.getId());
                                                Long currentPoints = document.getLong("pointValue");

                                                Long number = obj.getPointValue() + currentPoints;

                                                member.update("pointValue", number)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                Toast.makeText(ChoresActivity.this, obj.getName() + " Completed!",
                                                                        Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ChoresActivity.this, MainActivity.class);
                                                                startActivity(intent);

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error updating document", e);
                                                            }
                                                        });

                                                final Long[] totalPoints = new Long[1];
                                                final Long[] totalProgressPoints = new Long[1];

                                                db.collection("users").document(mFirebaseUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            DocumentSnapshot document = task.getResult();

                                                            totalPoints[0] = document.getLong("totalPointValue");
                                                            totalProgressPoints[0] = document.getLong("LTGProgressPointValue");

                                                            DocumentReference totalPoint = db.collection("users").document(mFirebaseUser.getEmail());

                                                            totalPoint.update("totalPointValue", totalPoints[0] + obj.getPointValue())
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "Error updating document", e);
                                                                        }
                                                                    });

                                                            totalPoint.update("LTGProgressPointValue", totalProgressPoints[0] + obj.getPointValue())
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w(TAG, "Error updating document", e);
                                                                        }
                                                                    });
                                                        }

                                                    }
                                                });

                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                            }
                        });

                    }
                }
            }
        });

        Button home = (Button) findViewById(R.id.btnHome);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChoresActivity.this, MainActivity.class));
                finish();
            }
        });


    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            //setDynamicHeight(listView);
            return true;

        } else {
            return false;
        }

    }

    @Override
    public void addChore(String rewardName, Long chorePointValue) {

        Chore chore = new Chore();
        chore.setName(rewardName);
        chore.setPointValue(chorePointValue);
        daily.add(chore);

        Log.d("ADDREWARD", "HERE");
        addChoreToDatabase(chore);

        setListViewHeightBasedOnItems(lvDaily);

        Log.d("PARAMS", String.valueOf(lvDaily.getLayoutParams().height));

    }

    public void addChoreToDatabase(Chore chore) {

        Log.d("ADDREWARDTODB", "HERE");
        Map<String, Object> R = new HashMap<>();
        R.put("name", chore.getName());
        R.put("pointValue", chore.getPointValue());

        db.collection("users").document(user.getEmail()).collection("chores").document()
                .set(R)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SUCCESS", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Error writing document", e);
                    }
                });
    }

    @Override
    public void removeChore(int position) {
        Chore c = daily.get(position);
        daily.remove(c);
        dailyAdapter.notifyDataSetChanged();

        db.collection("users").document(user.getEmail()).collection("chores").whereEqualTo("name", c.getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        db.collection("users").document(user.getEmail()).collection("chores").document(document.getId()).delete();
                    }
                } else {
                    Log.d("ERROR", "Error getting documents: ", task.getException());
                }
            }
        });

        Log.d("Removing", c.getName());

        setListViewHeightBasedOnItems(lvDaily);
    }
}