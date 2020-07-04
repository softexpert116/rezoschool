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
import com.ediattah.rezoschool.Model.CallbackModel;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.SchoolCallbackListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewSchoolCallbackActivity;

import java.util.ArrayList;

public class SchoolCallbackFragment extends Fragment {
    MainActivity activity;
    ListView listView;
    SchoolCallbackListAdapter schoolCallbackListAdapter;
    ArrayList<CallbackModel> arrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_school_callback, container, false);
        listView = v.findViewById(R.id.listView);

        arrayList.add(new CallbackModel(1, 1, "12345678", "call back message1"));
        arrayList.add(new CallbackModel(2, 1, "12343278", "call back message2"));

        schoolCallbackListAdapter = new SchoolCallbackListAdapter(activity, this, arrayList);
        listView.setAdapter(schoolCallbackListAdapter);
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
                Intent intent = new Intent(activity, NewSchoolCallbackActivity.class);
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