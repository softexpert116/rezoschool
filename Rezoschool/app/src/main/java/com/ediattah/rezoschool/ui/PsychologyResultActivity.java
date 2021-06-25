package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.PsychologyResult;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.PsychologyResultListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PsychologyResultActivity extends AppCompatActivity {
    Student student;
    User user;
    PsychologyResultListAdapter psychologyResultListAdapter;
    ArrayList<PsychologyResult> arrayList = new ArrayList<>();
    LinearLayout ly_no_items;
    ListView listView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psychology_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        student = (Student)getIntent().getSerializableExtra("STUDENT");
        user = (User)getIntent().getSerializableExtra("USER");
        setTitle(getResources().getString(R.string.psychology_result));

        ImageView img_photo = findViewById(R.id.img_photo);
        TextView txt_name = findViewById(R.id.txt_name);
        Glide.with(this).load(user.photo).apply(new RequestOptions()
                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
        txt_name.setText(user.name);

        listView = findViewById(R.id.listView);
        ly_no_items = findViewById(R.id.ly_no_items);
        psychologyResultListAdapter = new PsychologyResultListAdapter(this, arrayList);
        listView.setAdapter(psychologyResultListAdapter);
        loadPsychologyResults();
    }
    void loadPsychologyResults() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utils.mDatabase.child(Utils.tbl_psychology_result).orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.getValue() != null) {
                    arrayList.clear();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        PsychologyResult result = datas.getValue(PsychologyResult.class);
                        result._id = datas.getKey();
                        if (!result.uid.equals(student.uid)) {
                            continue;
                        }
                        if (result.timestamp < 0) {
                            result.timestamp *= (-1);
                        }
                        arrayList.add(result);
                    }

                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (arrayList.size() == 0) {
                            ly_no_items.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else {
                            ly_no_items.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            psychologyResultListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}