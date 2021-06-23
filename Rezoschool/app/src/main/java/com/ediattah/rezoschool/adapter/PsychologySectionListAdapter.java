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

import com.ediattah.rezoschool.Model.Library;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.LibraryFragment;
import com.ediattah.rezoschool.fragments.PsychologyEditFragment;

import java.util.ArrayList;

public class PsychologySectionListAdapter extends BaseAdapter {
    public ArrayList<PsychologySection> arrayList;
    Context context;
    public int sel_index = 0;

    public PsychologySectionListAdapter(Context _context, ArrayList<PsychologySection> _arrayList) {
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
        final PsychologySection model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_psychology_section, null);
        }
        TextView txt_name = view.findViewById(R.id.txt_name);
        TextView txt_description = view.findViewById(R.id.txt_description);
        TextView txt_score = view.findViewById(R.id.txt_score);
        RelativeLayout ly_cover = view.findViewById(R.id.ly_cover);
        if (sel_index == i) {
            ly_cover.setBackground(context.getResources().getDrawable(R.color.colorAccent));
            txt_description.setTextColor(Color.parseColor("#ffffff"));
            txt_score.setTextColor(Color.parseColor("#ffffff"));
        } else {
            ly_cover.setBackground(context.getResources().getDrawable(R.color.white));
            txt_description.setTextColor(Color.parseColor("#888888"));
            txt_score.setTextColor(Color.parseColor("#888888"));
        }

        txt_name.setText(model.name);
        txt_description.setText(model.description);
        txt_score.setText("Pass score: " + String.valueOf(model.score));
        ImageView img_edit = view.findViewById(R.id.img_edit);
        img_edit.setVisibility(View.GONE);

        return view;
    }
}
