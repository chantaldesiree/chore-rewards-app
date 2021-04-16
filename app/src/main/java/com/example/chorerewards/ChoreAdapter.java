package com.example.chorerewards;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

//Allows a user to view a chore.
public class ChoreAdapter extends ArrayAdapter<Chore>
{
    private Context context;
    private ArrayList<Chore> choreList;

    public ChoreAdapter (Context context, ArrayList<Chore> choreList)
    {
        super(context, R.layout.chore_list_item, choreList);
        this.context = context;
        this.choreList = choreList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            LayoutInflater vi = LayoutInflater.from(context);
            view = vi.inflate(R.layout.chore_list_item, null);
        }

        final Chore chore = choreList.get(position);

        TextView tvChore = view.findViewById(R.id.tvChore);
        TextView tvChorePointValue = view.findViewById(R.id.tvChorePointValue);

        notifyDataSetChanged();

        ImageView ivCurrency = (ImageView) view.findViewById(R.id.ivCurrency);
        ivCurrency.setImageResource(R.drawable.ic_currency);
        ivCurrency.setColorFilter(ContextCompat.getColor(getContext(), R.color.purple_500), PorterDuff.Mode.MULTIPLY);

        tvChore.setText(chore.getName());
        tvChorePointValue.setText(String.valueOf(chore.getPointValue()));

        return view;
    }
}