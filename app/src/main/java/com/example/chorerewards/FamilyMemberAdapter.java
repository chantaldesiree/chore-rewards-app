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

//The adapter used to define the listview of family members.
public class FamilyMemberAdapter extends ArrayAdapter<FamilyMember>
{
    private Context context;
    private ArrayList<FamilyMember> familyMemberList;

    public FamilyMemberAdapter (Context context, ArrayList<FamilyMember> familyMemberList)
    {
        super(context, R.layout.family_list_item, familyMemberList);
        this.context = context;
        this.familyMemberList = familyMemberList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            LayoutInflater vi = LayoutInflater.from(context);
            view = vi.inflate(R.layout.family_list_item, null);
        }

        final FamilyMember familyMember = familyMemberList.get(position);

        TextView tvFamilyMember = view.findViewById(R.id.tvFamilyMember);
        TextView tvFamilyMemberPoints = view.findViewById(R.id.tvFamilyMemberPoints);

        notifyDataSetChanged();

        tvFamilyMember.setText(familyMember.getName());
        tvFamilyMemberPoints.setText(NumberFormat.getNumberInstance(Locale.US).format(familyMember.getPointValue()));

        ImageView ivCurrency = (ImageView) view.findViewById(R.id.ivCurrency);
        ivCurrency.setColorFilter(ContextCompat.getColor(getContext(), R.color.purple_500), PorterDuff.Mode.MULTIPLY);

        return view;
    }
}