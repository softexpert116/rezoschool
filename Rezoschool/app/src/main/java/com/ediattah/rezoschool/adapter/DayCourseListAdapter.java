package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.DayCourseModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.SchoolClassFragment;

import java.util.ArrayList;

public class DayCourseListAdapter extends BaseAdapter {
    ArrayList<DayCourseModel> arrayList;

    Context context;
    SchoolClassFragment fragment;

    public DayCourseListAdapter(Context _context, ArrayList<DayCourseModel> _arrayList) {
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
        final DayCourseModel model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_day_course, null);
        }
        TextView txt_course = view.findViewById(R.id.txt_course);
        TextView txt_time = view.findViewById(R.id.txt_time);

        txt_course.setText(model.course.name);
        txt_time.setText(model.time);
        return view;
    }
}
