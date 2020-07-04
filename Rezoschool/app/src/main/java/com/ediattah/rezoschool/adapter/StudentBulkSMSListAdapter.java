package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;

import java.util.ArrayList;

public class StudentBulkSMSListAdapter extends BaseAdapter {
    ArrayList<Student> arrayList;

    Context context;

    public StudentBulkSMSListAdapter(Context _context, ArrayList<Student> _arrayList) {
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
        final Student model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_student_bulk_sms, null);
        }
        TextView txt_name = view.findViewById(R.id.txt_name);
        return view;
    }
}
