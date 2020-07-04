package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ediattah.rezoschool.Model.TransactionModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.SchoolFinanceListAdapter;
import com.ediattah.rezoschool.ui.FinanceSettingActivity;
import com.ediattah.rezoschool.ui.MainActivity;

import java.util.ArrayList;

public class SchoolFinanceFragment extends Fragment {
    MainActivity activity;
    ListView listView;
    SchoolFinanceListAdapter schoolFinanceListAdapter;
    ArrayList<TransactionModel> arrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_school_finance, container, false);
        listView = v.findViewById(R.id.listView);

        arrayList.add(new TransactionModel(1, 1, "$30", "02/05/2019 3:10 PM", "Mobile Money"));
        arrayList.add(new TransactionModel(2, 1, "$50", "02/21/2019 3:10 PM", "Mobile Money"));

        schoolFinanceListAdapter = new SchoolFinanceListAdapter(activity, this, arrayList);
        listView.setAdapter(schoolFinanceListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(activity, "Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FinanceSettingActivity.class);
                activity.startActivity(intent);
            }
        });
        return v;
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