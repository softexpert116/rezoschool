package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ediattah.rezoschool.Model.TransactionModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.SchoolFinanceFragment;

import java.util.ArrayList;

public class SchoolFinanceListAdapter extends BaseAdapter {
    ArrayList<TransactionModel> arrayList;

    Context context;
    SchoolFinanceFragment fragment;

    public SchoolFinanceListAdapter(Context _context, SchoolFinanceFragment _fragment, ArrayList<TransactionModel> _arrayList) {
        context = _context;
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
        final TransactionModel model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_school_finance, null);
        }
        TextView txt_student = view.findViewById(R.id.txt_student);
//        txt_student.setText(String.valueOf(model.student_id));
        TextView txt_method = view.findViewById(R.id.txt_method);
        txt_method.setText(model.method);
        TextView txt_amount = view.findViewById(R.id.txt_amount);
        txt_amount.setText(model.amount);
        TextView txt_date = view.findViewById(R.id.txt_date);
        txt_date.setText(model.date);

        return view;
    }
}
