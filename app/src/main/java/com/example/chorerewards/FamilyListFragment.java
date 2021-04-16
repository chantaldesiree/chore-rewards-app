package com.example.chorerewards;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Creates a list of family members.
public class FamilyListFragment extends Fragment {

    ListView lvFamilyMembers;
    FamilyMember familyMember;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ArrayList<FamilyMember> familyMembers = new ArrayList<FamilyMember>();
        FamilyMemberAdapter familyMemberAdapter = new FamilyMemberAdapter(getActivity(), familyMembers);

        View v = inflater.inflate(R.layout.fragment_family_list, container, false);

        lvFamilyMembers = v.findViewById(R.id.familyList);

        lvFamilyMembers.setAdapter(familyMemberAdapter);

        lvFamilyMembers.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FamilyMember obj = (FamilyMember) lvFamilyMembers.getAdapter().getItem(position);
                String value = obj.getName();

                Intent intent = new Intent(FamilyListFragment.this.getActivity(), ChoresActivity.class);
                FamilyListFragment.this.startActivity(intent);

                //Log.d("what is it deleting", value);
                //Log.d("email", user.getEmail());

            }
        });

        return v;
    }

}