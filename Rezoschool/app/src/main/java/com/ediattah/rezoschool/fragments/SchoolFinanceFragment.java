package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.Transaction;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ediattah.rezoschool.Model.TransactionModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.SchoolFinanceListAdapter;
import com.ediattah.rezoschool.ui.FinanceSettingActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolFinanceFragment extends Fragment {
    MainActivity activity;
    ListView listView;
    SchoolFinanceListAdapter schoolFinanceListAdapter;
    ArrayList<Transaction> arrayList = new ArrayList<>();
    LinearLayout ly_no_items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_school_finance, container, false);
        listView = v.findViewById(R.id.listView);
        ly_no_items = v.findViewById(R.id.ly_no_items);
        schoolFinanceListAdapter = new SchoolFinanceListAdapter(activity, arrayList);
        listView.setAdapter(schoolFinanceListAdapter);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FinanceSettingActivity.class);
                activity.startActivity(intent);
            }
        });
        read_transactions();
        return v;
    }
    void read_transactions() {
        Utils.mDatabase.child(Utils.tbl_transaction).orderByChild("school_id").equalTo(Utils.currentSchool._id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        Transaction transaction = datas.getValue(Transaction.class);
                        arrayList.add(transaction);
                    }
                }
                schoolFinanceListAdapter.notifyDataSetChanged();
                if (arrayList.size() == 0) {
                    ly_no_items.setVisibility(View.VISIBLE);
                } else {
                    ly_no_items.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}