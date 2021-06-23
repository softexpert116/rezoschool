package com.ediattah.rezoschool.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.PsychologyEditFragment;

import java.util.ArrayList;

public class PsychologySectionEditListAdapter extends BaseAdapter {
    public ArrayList<PsychologySection> arrayList;
    PsychologyEditFragment fragment;

    public PsychologySectionEditListAdapter(PsychologyEditFragment _fragment, ArrayList<PsychologySection> _arrayList) {
        fragment = _fragment;
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
        final PsychologySection model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
            view = inflater.inflate(R.layout.cell_psychology_section, null);
        }
        RelativeLayout ly_cover = view.findViewById(R.id.ly_cover);
        ly_cover.setBackground(fragment.getResources().getDrawable(R.color.white));
        TextView txt_name = view.findViewById(R.id.txt_name);
        TextView txt_description = view.findViewById(R.id.txt_description);
        TextView txt_score = view.findViewById(R.id.txt_score);
        txt_name.setText(model.name);
        txt_description.setText(model.description);
        txt_score.setText("Pass score: " + String.valueOf(model.score));
        ImageView img_edit = view.findViewById(R.id.img_edit);
        img_edit.setVisibility(View.VISIBLE);
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.openUpdateSectionNameDialog(model);
            }
        });

        return view;
    }
}
