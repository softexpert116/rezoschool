package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolLevelFeeListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FinanceSettingActivity extends AppCompatActivity {
    ArrayList<Level> arrayList = new ArrayList<>();
    ListView listView;
    SchoolLevelFeeListAdapter schoolLevelFeeListAdapter;
    EditText edit_fee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.fee_setting));

        edit_fee = findViewById(R.id.edit_fee);

        listView = findViewById(R.id.listView);
        schoolLevelFeeListAdapter = new SchoolLevelFeeListAdapter(this, arrayList);
        listView.setAdapter(schoolLevelFeeListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                schoolLevelFeeListAdapter.sel_index = i;
                schoolLevelFeeListAdapter.notifyDataSetChanged();
                Level level = arrayList.get(i);
                edit_fee.setText(level.fee);
            }
        });
        Button btn_set = findViewById(R.id.btn_set);
        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fee = edit_fee.getText().toString().trim();
                if (fee.length() == 0) {
                    return;
                }
                arrayList.get(schoolLevelFeeListAdapter.sel_index).fee = fee;
                Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("levels").setValue(arrayList);
                Toast.makeText(FinanceSettingActivity.this, getResources().getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
            }
        });
        level_update_listener();
    }
    void level_update_listener() {
        Utils.mDatabase.child(Utils.tbl_school).child(Utils.currentSchool._id).child("levels").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Utils.currentSchool.levels.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Level level = datas.getValue(Level.class);
                        Utils.currentSchool.levels.add(level);
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        schoolLevelFeeListAdapter.arrayList = Utils.currentSchool.levels;
                        arrayList = Utils.currentSchool.levels;
                        schoolLevelFeeListAdapter.notifyDataSetChanged();
                        edit_fee.setText(arrayList.get(schoolLevelFeeListAdapter.sel_index).fee);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}