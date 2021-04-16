package com.example.chorerewards;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

//Allows the user to see a list of rewards.
public class RewardAdapter extends ArrayAdapter<Reward>
{
    private Context context;
    private ArrayList<Reward> rewardList;

    public RewardAdapter (Context context, ArrayList<Reward> rewardList)
    {
        super(context, R.layout.reward_list_item, rewardList);
        this.context = context;
        this.rewardList = rewardList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            LayoutInflater vi = LayoutInflater.from(context);
            view = vi.inflate(R.layout.reward_list_item, null);
        }

        final Reward reward = rewardList.get(position);

        TextView tvReward = view.findViewById(R.id.tvReward);
        TextView tvRewardPoints = view.findViewById(R.id.tvRewardPoints);

        notifyDataSetChanged();

        tvReward.setText(reward.getName());
        tvRewardPoints.setText(NumberFormat.getNumberInstance(Locale.US).format(reward.getPointValue()));

        ImageView ivCurrency = (ImageView) view.findViewById(R.id.ivCurrency);
        ivCurrency.setColorFilter(ContextCompat.getColor(getContext(), R.color.purple_500), PorterDuff.Mode.MULTIPLY);

        return view;
    }
}