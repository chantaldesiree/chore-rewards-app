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

//Allows a user to add a Family Member.
public class AddFamilyMemberAdapter extends ArrayAdapter<FamilyMember>
{
    private Context context;
    private ArrayList<FamilyMember> familyMemberList;

    public AddFamilyMemberAdapter (Context context, ArrayList<FamilyMember> familyMemberList)
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

        tvFamilyMember.setText(familyMember.getName());
        notifyDataSetChanged();

        return view;
    }
}