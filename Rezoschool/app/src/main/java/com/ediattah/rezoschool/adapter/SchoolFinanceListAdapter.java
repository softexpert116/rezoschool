package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.Transaction;
import com.ediattah.rezoschool.Model.TransactionModel;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.SchoolFinanceFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolFinanceListAdapter extends BaseAdapter {
    ArrayList<Transaction> arrayList;

    Context context;

    public SchoolFinanceListAdapter(Context _context, ArrayList<Transaction> _arrayList) {
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
        final Transaction model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_school_finance, null);
        }
        final TextView txt_student = view.findViewById(R.id.txt_student);
        Utils.mDatabase.child(Utils.tbl_user).child(model.student_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    txt_student.setText(user.name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TextView txt_method = view.findViewById(R.id.txt_method);
        txt_method.setText(model.method);
        TextView txt_amount = view.findViewById(R.id.txt_amount);
        txt_amount.setText(model.amount);
        TextView txt_date = view.findViewById(R.id.txt_date);
        txt_date.setText(model.date);
        TextView txt_transactionid = view.findViewById(R.id.txt_transactionid);
        txt_transactionid.setText(model.transactionid);
        TextView txt_status = view.findViewById(R.id.txt_status);
        if (model.status.equals("100")) {
            txt_status.setText("SUCCESS");
            txt_status.setBackgroundColor(Color.parseColor("#c3edb9"));
        } else {
            txt_status.setText("QUEUED");
            txt_status.setBackgroundColor(Color.parseColor("#e28e8e"));
        }
        return view;
    }
}
