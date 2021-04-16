package com.example.chorerewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


//Shows the dashboard and all of the families information.
public class MainActivity extends AppCompatActivity implements RemoveRewardFragment.RemoveRewardListener, RemoveFamilyMemberFragment.RemoveFamilyMemberListener, AddFamilyMemberFragment.AddFamilyMemberListener, AddRewardFragment.AddRewardListener {

    private final int STANDARD_REQUEST_CODE = 0;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();

    private SignInButton mSignInButton;
    private GoogleSignInClient mSignInClient;

    ProgressBar progressBar;
    int progress;

    TextView tvLTGTotalPointsNumber;
    int totalPoints;

    ListView lvFamilyMembers;
    FamilyMember familyMember;
    ArrayList<FamilyMember> familyMembers;
    FamilyMemberAdapter familyMemberAdapter;

    ListView lvRewards;
    Reward reward;
    ArrayList<Reward> rewards;
    RewardAdapter rewardAdapter;

    Long totalPointValue;
    Long TPV;

    TextView tvLTGName;
    Button changeLTG;
    Boolean goalReached;

    Boolean dark;


    public static String PACKAGE_NAME;

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.recreate();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loads Shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Changes background color
        dark = prefs.getBoolean("DARK", false);
        int theme = dark ? Color.argb(225, 0, 0, 0) : Color.argb(100, 255, 255, 255);
        View layout = findViewById(R.id.layout);
        layout.setBackgroundColor(theme);




        goalReached = false;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db.setFirestoreSettings(settings);

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        Button addFamilyMember = (Button) findViewById(R.id.btnAddChore);
        Button removeFamilyMember = (Button) findViewById(R.id.btnRemoveChore);
        Button addReward = (Button) findViewById(R.id.btnAddReward);
        Button removeReward = (Button) findViewById(R.id.btnRemoveReward);

        familyMembers = new ArrayList<FamilyMember>();
        familyMemberAdapter = new FamilyMemberAdapter(this, familyMembers);

        lvFamilyMembers = findViewById(R.id.familyList);
        lvFamilyMembers.setAdapter(familyMemberAdapter);

        addFamilyMembers();
        addRewardsMembers();

        changeLTG = (Button) findViewById(R.id.btnChangeLTG);

        changeLTG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!goalReached)
                {
                    Intent intent = new Intent(MainActivity.this, ChangeLongTermGoal.class);
                    if (tvLTGName != null)
                    {
                        intent.putExtra("LTGname", tvLTGName.getText().toString());
                        intent.putExtra("tvLTGTotalPointsNumber", 1000);
                        intent.putExtra("tvStartingPoints", "0");
                    }
                    startActivity(intent);
                    finish();
                }
                else {
                    changeLTG.setBackgroundColor(getResources().getColor(R.color.purple_500));
                    changeLTG.setTextColor(Color.WHITE);
                    changeLTG.setText("Change Long Term Goal");
                    goalReached = false;

                    DocumentReference query = db.collection("users").document(user.getEmail());

                    query.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();

                            TPV = document.getLong("LTGProgressPointValue");
                            Long LTGPV = document.getLong("LTGPointValue");
                            Long newTPV = TPV - LTGPV;

                            Log.d("LTGPV", String.valueOf(LTGPV));
                            Log.d("TPV", String.valueOf(TPV));

                            progressBar = (ProgressBar) findViewById(R.id.progressBar);
                            progress = Math.toIntExact( LTGPV * 100 / TPV);
                            progressBar.setProgress(progress);

                            query.update("LTGProgressPointValue", newTPV);


                            finish();
                            startActivity(getIntent());
                        }
                    });

                }
            }
        });

        removeFamilyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new RemoveFamilyMemberFragment();
                newFragment.show(getSupportFragmentManager(), "something");
            }
        });

        addFamilyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddFamilyMemberFragment();
                newFragment.show(getSupportFragmentManager(), "missiles");
            }
        });

        addReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddRewardFragment();
                newFragment.show(getSupportFragmentManager(), "missiles");
            }
        });

        removeReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new RemoveRewardFragment();
                newFragment.show(getSupportFragmentManager(), "missiles");
            }
        });

        ImageView ivCurrency = (ImageView) findViewById(R.id.ivCurrency);
        ivCurrency.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.purple_500), PorterDuff.Mode.MULTIPLY);
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

    private void addRewardsMembers() {
        rewards = new ArrayList<Reward>();
        rewardAdapter = new RewardAdapter(this, rewards);

        lvRewards = findViewById(R.id.rewardList);
        lvRewards.setAdapter(rewardAdapter);

        db.collection("users").document(user.getEmail()).collection("rewards").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Log.d("GOT IN", "HERE");

                        reward = new Reward();
                        reward.setName(document.getString("name"));
                        reward.setPointValue(Integer.valueOf(document.get("pointValue").toString()));

                        //Log.d("pointValue", Integer.valueOf(document.get("pointValue").toString()));

                        rewards.add(reward);

                        lvRewards.setAdapter(rewardAdapter);

                        setListViewHeightBasedOnItems(lvRewards);

                        lvRewards.setOnItemClickListener(new ListView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Reward obj = (Reward) lvRewards.getAdapter().getItem(position);
                                String name = obj.getName();
                                String pointValue = String.valueOf(obj.getPointValue());


                                Intent intent = new Intent(MainActivity.this, RedeemReward.class);
                                intent.putExtra("reward", name);
                                intent.putExtra("pointValue", pointValue);
                                startActivity(intent);

                                db.collection("users").document(user.getEmail()).collection("rewards").whereEqualTo("name", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {

                                                //query.document(document.getId()).delete();
                                            }
                                        } else {
                                            Log.d("ERROR", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

    }




    @Override
    public void addFamilyMember(String familyMember) {

        FamilyMember newFamilyMember = new FamilyMember();
        newFamilyMember.setName(familyMember);
        familyMembers.add(newFamilyMember);

        Log.d("ADDFAMILYMEMBER", "HERE");
        addFMToDatabase(newFamilyMember);

        ViewGroup.LayoutParams params = lvFamilyMembers.getLayoutParams();
        params.height = params.height + 110;
        lvFamilyMembers.setLayoutParams(params);

        for (FamilyMember s : familyMembers){
            Log.d("My array list content: ", s.getName());
        }

        Log.d("PARAMS", String.valueOf(lvFamilyMembers.getLayoutParams().height));
    }

    public void addFMToDatabase(FamilyMember familyMember) {

        Log.d("ADDFMTODATABASE", "HERE");
        Map<String, Object> FM = new HashMap<>();
        FM.put("name", familyMember.getName());
        FM.put("pointValue", 0);

        db.collection("users").document(user.getEmail()).collection("familyMembers").document()
                .set(FM)
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

    public void addFamilyMembers() {

        db.collection("users").document(user.getEmail()).collection("familyMembers").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Log.d("GOT IN", "HERE");

                        familyMember = new FamilyMember();
                        familyMember.setName(document.getString("name"));
                        familyMember.setPointValue(Integer.valueOf(document.get("pointValue").toString()));

                        db.collection("users").document(mFirebaseUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    DocumentSnapshot document = task.getResult();

                                    Log.d("POINTS", String.valueOf(document.getLong("totalPointValue")));

                                    Long number = Long.valueOf(document.getLong("totalPointValue"))  + familyMember.getPointValue();

                                    tvLTGTotalPointsNumber = (TextView) findViewById(R.id.tvLTGTotalPointsNumber);
                                    tvLTGTotalPointsNumber.setText(NumberFormat.getNumberInstance(Locale.US).format(number) + " POINTS");

                                    tvLTGName = (TextView) findViewById(R.id.tvLTGName);
                                    tvLTGName.setText(document.getString("longTermGoal"));

                                    TextView tvLTGPoints = (TextView) findViewById(R.id.tvLTGPoints);
                                    tvLTGPoints.setText(String.valueOf(document.getLong("LTGPointValue")));

                                    //Set Progress Percent
                                    progressBar = (ProgressBar) findViewById(R.id.progressBar);
                                    progress = Math.toIntExact( document.getLong("LTGProgressPointValue") * 100 / document.getLong("LTGPointValue"));
                                    progressBar.setProgress(progress);

                                    Log.d("GOAL", String.valueOf(document.getLong("totalPointValue")));
                                    Log.d("GOAL2", String.valueOf(document.getLong("LTGPointValue")));

                                    if (progress >= 100)
                                    {
                                        changeLTG.setBackgroundColor(Color.RED);
                                        changeLTG.setTextColor(Color.WHITE);
                                        changeLTG.setText("You reached your goal!");
                                        goalReached = true;
                                    }

                                }

                            }
                        });

                        familyMembers.add(familyMember);

                        lvFamilyMembers.setAdapter(familyMemberAdapter);

                        setListViewHeightBasedOnItems(lvFamilyMembers);

                        lvFamilyMembers.setOnItemClickListener(new ListView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                FamilyMember fm = (FamilyMember) lvFamilyMembers.getAdapter().getItem(position);
                                String name = fm.getName();

                                Intent intent = new Intent(MainActivity.this, ChoresActivity.class);
                                intent.putExtra("name", name);
                                startActivity(intent);

                                db.collection("users").document(user.getEmail()).collection("familyMembers").whereEqualTo("name", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {

                                                //query.document(document.getId()).delete();
                                            }
                                        } else {
                                            Log.d("ERROR", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });


        lvFamilyMembers.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FamilyMember obj = (FamilyMember) lvFamilyMembers.getAdapter().getItem(position);
                String value = obj.getName();

                Intent intent = new Intent(MainActivity.this, ChoresActivity.class);
                MainActivity.this.startActivity(intent);

                //Log.d("what is it deleting", value);
                //Log.d("email", user.getEmail());

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {

            case R.id.item_sign_out:
                mFirebaseAuth.signOut();
                mSignInClient.signOut();

                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;

            case R.id.item_preference:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, STANDARD_REQUEST_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void addReward(String rewardName, long rewardPointValue) {

        Reward reward = new Reward();
        reward.setName(rewardName);
        reward.setPointValue(rewardPointValue);
        rewards.add(reward);

        Log.d("ADDREWARD", "HERE");
        addRewardToDatabase(reward);


        setListViewHeightBasedOnItems(lvRewards);

        for (Reward s : rewards){
            Log.d("My array list content: ", s.getName());
        }

        Log.d("PARAMS", String.valueOf(lvRewards.getLayoutParams().height));

    }

    public void addRewardToDatabase(Reward reward) {

        Log.d("ADDREWARDTODB", "HERE");
        Map<String, Object> R = new HashMap<>();
        R.put("name", reward.getName());
        R.put("pointValue", reward.getPointValue());

        db.collection("users").document(user.getEmail()).collection("rewards").document()
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
    public void removeFamilyMember(int position) {
        FamilyMember fm = familyMembers.get(position);
        Log.d("FM", fm.getName().toString());
        familyMemberAdapter.remove(fm);

        familyMemberAdapter.notifyDataSetChanged();

        Log.d("Removing", fm.getName());
        Map<String, Object> R = new HashMap<>();


        db.collection("users").document(user.getEmail()).collection("familyMembers").whereEqualTo("name", fm.getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        db.collection("users").document(user.getEmail()).collection("familyMembers").document(document.getId()).delete();
                    }
                } else {
                    Log.d("ERROR", "Error getting documents: ", task.getException());
                }
            }
        });

        setListViewHeightBasedOnItems(lvFamilyMembers);
    }

    public void removeReward(int position) {
        Reward r = rewards.get(position);
        Log.d("REWARD", r.getName().toString());
        rewardAdapter.remove(r);

        rewardAdapter.notifyDataSetChanged();

        Log.d("Removing", r.getName());
        Map<String, Object> R = new HashMap<>();


        db.collection("users").document(user.getEmail()).collection("rewards").whereEqualTo("name", r.getName()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        db.collection("users").document(user.getEmail()).collection("rewards").document(document.getId()).delete();
                    }
                } else {
                    Log.d("ERROR", "Error getting documents: ", task.getException());
                }
            }
        });

        setListViewHeightBasedOnItems(lvRewards);
    }
}