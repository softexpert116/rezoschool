package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ediattah.rezoschool.Model.PsychologyResult;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PsychologyResultListAdapter extends BaseAdapter {
    public ArrayList<PsychologyResult> arrayList;
    Context context;

    public PsychologyResultListAdapter(Context _context, ArrayList<PsychologyResult> _arrayList) {
        context = _context;
        this.arrayList = _arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final PsychologyResult model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_psychology_test, null);
        }
        TextView txt_section_name = view.findViewById(R.id.txt_section_name);
        TextView txt_section_description = view.findViewById(R.id.txt_section_description);
        TextView txt_score = view.findViewById(R.id.txt_score);
        TextView txt_result = view.findViewById(R.id.txt_result);
        TextView txt_date = view.findViewById(R.id.txt_date);

        txt_section_name.setText(model.section_name);
        txt_score.setText(String.valueOf(model.score));
        txt_date.setText(Utils.getDateStringFromTimestamp(model.timestamp * 1000));
        Utils.mDatabase.child(Utils.tbl_psychology_section).child(model.section_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    PsychologySection section = dataSnapshot.getValue(PsychologySection.class);
                    section._id = dataSnapshot.getKey();
                    txt_section_description.setText(section.description);
                    String result = "fail";
                    int color = context.getResources().getColor(R.color.colorAccent);
                    if (model.score >= section.score) {
                        result = "success";
                        color = context.getResources().getColor(R.color.colorPrimary);
                    }
                    txt_result.setText(result);
                    txt_result.setTextColor(color);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
