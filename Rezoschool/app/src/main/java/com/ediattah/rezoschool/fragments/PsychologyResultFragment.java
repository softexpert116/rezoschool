package com.ediattah.rezoschool.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.PsychologyResult;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.StudentAcceptedListAdapter;
import com.ediattah.rezoschool.adapter.StudentWaitingListAdapter;
import com.ediattah.rezoschool.ui.LoginActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.PsychologyTestActivity;
import com.ediattah.rezoschool.ui.SplashActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

public class PsychologyResultFragment extends Fragment {
    MainActivity activity;
    MaterialSpinner spinner_area, spinner_school;
    ListView listView;
    ArrayList<String> array_school_name = new ArrayList<>();
    ArrayList<School> array_school = new ArrayList<>();
    ArrayList<Student> array_student = new ArrayList<>();
    ArrayList<PsychologySection> array_section = new ArrayList<>();
    ArrayList<String> arraySectionStr = new ArrayList<>();
    StudentAcceptedListAdapter studentAcceptedListAdapter;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_psychology_result, container, false);
        spinner_area = v.findViewById(R.id.spinner_area);
        spinner_school = v.findViewById(R.id.spinner_school);

        spinner_area.setItems(Utils.array_school_area);
        spinner_area.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                loadSchoolNames();
            }
        });
        spinner_school.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                loadStudents(position);
            }
        });
        listView = v.findViewById(R.id.listView);
        studentAcceptedListAdapter = new StudentAcceptedListAdapter(activity, array_student, false, null);
        listView.setAdapter(studentAcceptedListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDisplayDialog(array_student.get(position));
            }
        });

        loadSchoolNames();
        loadPsychologySections();
        return v;
    }
    void loadPsychologySections() {
        Utils.mDatabase.child(Utils.tbl_psychology_section).orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.getValue() != null) {
                    array_section.clear(); arraySectionStr.clear();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        PsychologySection psychologySection = datas.getValue(PsychologySection.class);
                        psychologySection._id = datas.getKey();
                        array_section.add(psychologySection);
                        arraySectionStr.add(psychologySection.name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    void loadSchoolNames() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String area = spinner_area.getText().toString();
        Utils.mDatabase.child(Utils.tbl_school).orderByChild("area").equalTo(area).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                array_school_name.clear(); array_school.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        array_school_name.add(school.number);
                        array_school.add(school);
                    }
                }
                spinner_school.setItems(array_school_name);
                if (array_school.size() > 0) {
                    listView.setVisibility(View.VISIBLE);
                    loadStudents(0);
                } else {
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void loadStudents(int school_index) {
        array_student.clear();
        for (Student student:array_school.get(school_index).students) {
            if (student.isAllow) {
                array_student.add(student);
            }
        }
        studentAcceptedListAdapter.notifyDataSetChanged();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                studentAcceptedListAdapter.notifyDataSetChanged();
            }
        }, 500);
    }
    PsychologySection sel_section;
    public void openDisplayDialog(Student student) {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_psychology_test_display, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
//        dlg.show();
        EditText fullname = dlg.findViewById(R.id.edit_name);
        EditText classname = dlg.findViewById(R.id.edit_class);
        EditText birth = dlg.findViewById(R.id.edit_birth);
        MaterialSpinner spinner_section = dlg.findViewById(R.id.spinner_section);
        TextView txt_score = dlg.findViewById(R.id.txt_score);
        TextView txt_result = dlg.findViewById(R.id.txt_result);
        Button btn_close = (Button)dlg.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        spinner_section.setItems(arraySectionStr);
        sel_section = array_section.get(0);
        spinner_section.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                sel_section = array_section.get(position);
                Utils.mDatabase.child(Utils.tbl_psychology_result).orderByChild("uid").equalTo(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            for(DataSnapshot datas: dataSnapshot.getChildren()){
                                PsychologyResult psychologyResult = datas.getValue(PsychologyResult.class);
                                psychologyResult._id = datas.getKey();
                                if (psychologyResult.section_name.equals(spinner_section.getText().toString())) {
                                    fullname.setText(psychologyResult.name);
                                    birth.setText(psychologyResult.birth);
                                    classname.setText(psychologyResult.className);
                                    txt_score.setText(String.valueOf(psychologyResult.score));
                                    String result = "fail";
                                    int color = activity.getResources().getColor(R.color.colorAccent);
                                    if (psychologyResult.score >= sel_section.score) {
                                        result = "success";
                                        color = activity.getResources().getColor(R.color.colorPrimary);
                                    }
                                    txt_result.setText(result);
                                    txt_result.setTextColor(color);
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(activity, "There is no psychology test result for this student.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        Utils.mDatabase.child(Utils.tbl_psychology_result).orderByChild("uid").equalTo(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    dlg.show();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        PsychologyResult psychologyResult = datas.getValue(PsychologyResult.class);
                        psychologyResult._id = datas.getKey();
                        if (psychologyResult.section_name.equals(spinner_section.getText().toString())) {
                            fullname.setText(psychologyResult.name);
                            birth.setText(psychologyResult.birth);
                            classname.setText(psychologyResult.className);
                            txt_score.setText(String.valueOf(psychologyResult.score));
                            String result = "fail";
                            int color = activity.getResources().getColor(R.color.colorAccent);
                            if (psychologyResult.score >= sel_section.score) {
                                result = "success";
                                color = activity.getResources().getColor(R.color.colorPrimary);
                            }
                            txt_result.setText(result);
                            txt_result.setTextColor(color);

                            break;
                        }
                    }
                } else {
                    Toast.makeText(activity, "There is no psychology test result for this student.", Toast.LENGTH_SHORT).show();
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