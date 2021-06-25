package com.ediattah.rezoschool.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.Model.PsychologyResult;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.QA;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.PsychologySectionListAdapter;
import com.ediattah.rezoschool.adapter.SchoolLevelListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewLevelActivity;
import com.ediattah.rezoschool.ui.PsychologyTestActivity;
import com.ediattah.rezoschool.ui.StudentDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class PsychologyTestFragment extends Fragment {
    MainActivity activity;
    ArrayList<PsychologySection> arrayList = new ArrayList<>();
    PsychologySectionListAdapter psychologySectionListAdapter;
    LinearLayout ly_no_items;
    Button btn_start;
    ListView listView;
    PsychologySection sel_section;
    ProgressDialog progressDialog;
    int score = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_psychological_test, container, false);
        psychologySectionListAdapter = new PsychologySectionListAdapter(activity, arrayList);
        listView = v.findViewById(R.id.listView);
        ly_no_items = v.findViewById(R.id.ly_no_items);
        btn_start = v.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmDialog();
            }
        });
        listView.setAdapter(psychologySectionListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                psychologySectionListAdapter.sel_index = position;
                psychologySectionListAdapter.notifyDataSetChanged();
                sel_section = arrayList.get(position);

            }
        });
        loadPsychologySections();

        return v;
    }
    void loadPsychologySections() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utils.mDatabase.child(Utils.tbl_psychology_section).orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.getValue() != null) {
                    arrayList.clear();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        PsychologySection psychologySection = datas.getValue(PsychologySection.class);
                        psychologySection._id = datas.getKey();
                        arrayList.add(psychologySection);
                    }
                    sel_section = arrayList.get(0);
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (arrayList.size() == 0) {
                            ly_no_items.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                            btn_start.setEnabled(false);
                        } else {
                            ly_no_items.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            psychologySectionListAdapter.notifyDataSetChanged();
                            btn_start.setEnabled(true);
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
    public void openConfirmDialog() {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_psychology_test, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        EditText firstname = dlg.findViewById(R.id.edit_firstname);
        EditText lastname = dlg.findViewById(R.id.edit_lastname);
        EditText classname = dlg.findViewById(R.id.edit_class);
        classname.setText(Utils.currentClass.name);
        EditText birth = dlg.findViewById(R.id.edit_birth);
        Button btn_confirm = (Button)dlg.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstname.getText().toString().trim();
                String lastName = lastname.getText().toString().trim();
                String className = classname.getText().toString().trim();
                String birthStr = birth.getText().toString().trim();
                if (firstName.length()*lastName.length()*className.length()*birthStr.length() == 0) {
                    Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                dlg.dismiss();
                Intent intent = new Intent(activity, PsychologyTestActivity.class);
                intent.putExtra("OBJECT", sel_section);
                PsychologyResult psychologyResult = new PsychologyResult("", Utils.currentSchool._id, Utils.currentUser._id, sel_section._id, sel_section.name, firstName + " " + lastName, birthStr, className, 0, (-1)*Utils.getTimestamp());
                intent.putExtra("RESULT", psychologyResult);
                startActivityForResult(intent, 0);
            }
        });
        dlg.show();
    }
    public void openScoreDialog() {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_psychology_test_result, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.3);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        dlg.setCancelable(false);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        TextView txt_score = dlg.findViewById(R.id.txt_score);
        txt_score.setText(String.valueOf(score));
        TextView txt_result = dlg.findViewById(R.id.txt_result);
        String result = "fail";
        int color = activity.getResources().getColor(R.color.colorAccent);
        if (score >= sel_section.score) {
            result = "success";
            color = activity.getResources().getColor(R.color.colorPrimary);
        }
        txt_result.setText(result);
        txt_result.setTextColor(color);
        Button btn_close = (Button)dlg.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                activity.goFirstFragment();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                score = data.getIntExtra("score", 0);
            }
            openScoreDialog();
        }
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