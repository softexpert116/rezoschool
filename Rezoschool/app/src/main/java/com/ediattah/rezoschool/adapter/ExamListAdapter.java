package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.Exam;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;

import java.util.ArrayList;

public class ExamListAdapter extends BaseAdapter {
    ArrayList<Exam> arrayList;
    Context context;

    public ExamListAdapter(Context _context, ArrayList<Exam> _arrayList) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Exam exam = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_exam, null);
        }
        TextView txt_result = view.findViewById(R.id.txt_result);
        TextView txt_date = view.findViewById(R.id.txt_date);
        txt_result.setText(exam.result);
        txt_date.setText(Utils.getDateString(exam.date));
        return view;
    }
}
