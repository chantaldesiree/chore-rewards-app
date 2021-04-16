package com.example.chorerewards;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

//The custom dialog for adding a family member.
public class AddChoreFragment extends DialogFragment {

    EditText etAddChoreName;
    EditText etAddChorePointValue;
    AddChoreListener listener;

    public AddChoreFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_chore, null);


        etAddChoreName = view.findViewById(R.id.etAddChoreName);
        etAddChorePointValue = view.findViewById(R.id.etAddChorePointValue);

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String rewardName = etAddChoreName.getText().toString();
                        long rewardPointValue = Long.valueOf(etAddChorePointValue.getText().toString());
                        listener.addChore(rewardName, rewardPointValue);
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
        return inflater.inflate(R.layout.dialog_add_chore, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddChoreListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "- must implement AddFamilyMemberListener");
        }
    }

    public interface AddChoreListener {
        void addChore(String choreName, Long chorePointValue);
    }
}