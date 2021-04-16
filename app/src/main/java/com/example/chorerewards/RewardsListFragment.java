package com.example.chorerewards;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


//Creates a list of rewards.
public class RewardsListFragment extends Fragment {

    ListView lvRewards;
    Reward reward1;
    Reward reward2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ArrayList<Reward> rewards = new ArrayList<Reward>();
        RewardAdapter rewardAdapter = new RewardAdapter(getActivity(), rewards);

        View v = inflater.inflate(R.layout.fragment_rewards_list, container, false);

        lvRewards = v.findViewById(R.id.rewardList);


        lvRewards.setAdapter(rewardAdapter);

        lvRewards.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Reward obj = (Reward) lvRewards.getAdapter().getItem(position);
                String value = obj.getName();

                Intent intent = new Intent(RewardsListFragment.this.getActivity(), RedeemReward.class);
                RewardsListFragment.this.startActivity(intent);

                //Log.d("what is it deleting", value);
                //Log.d("email", user.getEmail());

            }
        });

        return v;
    }
}