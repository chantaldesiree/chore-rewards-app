package com.example.chorerewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

import java.util.ArrayList;

public class PersonalRewards extends AppCompatActivity {

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

    PersonalRewardAdapter personalRewardsAdapter;

    ArrayList<Reward> personalRewards;

    private ListView lvPersonalRewards;

    Button points;

    Reward reward;

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
        setContentView(R.layout.activity_personal_rewards);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);

        String fmName = getIntent().getExtras().getString("name");

        TextView tvRewardsRedeem = findViewById(R.id.tvRewardsRedeem);
        tvRewardsRedeem.setText(fmName + "'s Rewards");

        lvPersonalRewards = (ListView) findViewById(R.id.lvPersonalRewards);

        personalRewards = new ArrayList<>();

        db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").whereEqualTo("name", fmName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      DocumentSnapshot document =  task.getResult().getDocuments().get(0);
                      Log.d("GOT IN", "HERE");

                      db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").document(document.getId()).collection("rewards").orderBy("rewardName").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                          @Override
                          public void onComplete(@NonNull Task<QuerySnapshot> task) {
                              for (QueryDocumentSnapshot document : task.getResult()) {

                                  personalRewardsAdapter = new PersonalRewardAdapter(getApplicationContext(), personalRewards);

                                  reward = new Reward();
                                  reward.setName(document.getString("rewardName"));
                                  reward.setPointValue(document.getLong("rewardPointValue"));

                                  Long rewardPointValue = reward.getPointValue();

                                  personalRewards.add(reward);

                                  lvPersonalRewards.setAdapter(personalRewardsAdapter);

                                  setListViewHeightBasedOnItems(lvPersonalRewards);

                                  lvPersonalRewards.setOnItemClickListener(new ListView.OnItemClickListener() {
                                      @Override
                                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                          Reward obj = (Reward) lvPersonalRewards.getAdapter().getItem(position);
                                          String value = obj.getName();

                                          db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").whereEqualTo("name", fmName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                              @Override
                                              public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                  DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                                                  Log.d(TAG, document.getString("name") + " selected");

                                                  db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").document(document.getId()).collection("rewards").whereEqualTo("rewardName", value).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                      @Override
                                                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                          DocumentSnapshot newdocument = queryDocumentSnapshots.getDocuments().get(0);

                                                          db.collection("users").document(mFirebaseUser.getEmail()).collection("familyMembers").document(document.getId()).collection("rewards").document(newdocument.getId()).delete()
                                                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                      @Override
                                                                      public void onSuccess(Void aVoid) {
                                                                          Log.d(TAG, document.getString("rewardName") + " deleted!");

                                                                          Toast.makeText(PersonalRewards.this, "Reward Redeemed!",
                                                                                  Toast.LENGTH_SHORT).show();
                                                                          Intent intent = new Intent(PersonalRewards.this, MainActivity.class);
                                                                          startActivity(intent);
                                                                      }
                                                                  })
                                                                  .addOnFailureListener(new OnFailureListener() {
                                                                      @Override
                                                                      public void onFailure(@NonNull Exception e) {
                                                                          Log.w(TAG, "Error deleting document", e);
                                                                      }
                                                                  });
                                                      }

                                                  });
                                              }
                                          }
                                          );

                                      }
                                  });
                              }

                          }
              });
          }
              }}
        );

        Button home = (Button) findViewById(R.id.btnHome);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalRewards.this, MainActivity.class));
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
}