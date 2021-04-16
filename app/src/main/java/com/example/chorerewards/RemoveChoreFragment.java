package com.example.chorerewards;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

//The custom dialog that allows a user to remove a reward.
public class RemoveChoreFragment extends DialogFragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();

    Spinner spnChores;
    RemoveChoreListener listener;
    ArrayList chores;
    FamilyMemberAdapter familyMemberAdapter;
    int chorePosition;

    public RemoveChoreFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_remove_chore, null);


        chores = new ArrayList<String>();

        CollectionReference query = db.collection("users").document(user.getEmail()).collection("chores");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        chores.add(document.getString("name"));

                        spnChores = view.findViewById(R.id.spnRemoveChore);
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnChores.setAdapter(spinnerAdapter);

                        for (Object rewards : chores) {
                            spinnerAdapter.add(rewards.toString());
                        }
                    }
                }
            }
        });

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        chorePosition = spnChores.getSelectedItemPosition();
                        Log.d("Selected", spnChores.getSelectedItem().toString());
                        listener.removeChore(chorePosition);
                    }
                })

                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        return builder.create();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_remove_chore, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (RemoveChoreListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "- must implement RemoveRewardListener");
        }
    }

    public interface RemoveChoreListener {
        void removeChore(int position);
    }
}