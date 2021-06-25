package com.ediattah.rezoschool.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.PsychologyResult;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.PsychologySubmit;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.StudentAcceptedListAdapter;
import com.ediattah.rezoschool.ui.ChatActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PsychologyRunFragment extends Fragment {
    MainActivity activity;
    MaterialSpinner spinner_area, spinner_school;
    ListView listView;
    ArrayList<String> array_school_name = new ArrayList<>();
    ArrayList<School> array_school = new ArrayList<>();
    ArrayList<Student> array_student = new ArrayList<>();
    ArrayList<Student> array_student_filtered = new ArrayList<>();
    ArrayList<Student> array_student_sel = new ArrayList<>();
    ArrayList<User> array_user = new ArrayList<>();
    ArrayList<PsychologySection> array_section = new ArrayList<>();
    ArrayList<String> arraySectionStr = new ArrayList<>();
    StudentAcceptedListAdapter studentAcceptedListAdapter;
    ProgressDialog progressDialog;
    EditText edit_search_student;
    LinearLayout ly_no_items;
    Button btn_submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_psychology_run, container, false);
        spinner_area = v.findViewById(R.id.spinner_area);
        spinner_school = v.findViewById(R.id.spinner_school);
        btn_submit = v.findViewById(R.id.btn_submit);
        ly_no_items = v.findViewById(R.id.ly_no_items);
        edit_search_student = v.findViewById(R.id.edit_search_student);
        edit_search_student.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edit_search_student.getText().toString().trim().length() == 0) {
                    array_student_filtered = new ArrayList<>(array_student);
                    studentAcceptedListAdapter.arrayList = array_student_filtered;
                    studentAcceptedListAdapter.notifyDataSetChanged();
                    if (array_student_filtered.size() == 0) {
                        ly_no_items.setVisibility(View.VISIBLE);
                    } else {
                        ly_no_items.setVisibility(View.GONE);
                    }
                } else {
                    filter_list();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        studentAcceptedListAdapter = new StudentAcceptedListAdapter(activity, array_student_filtered, true, array_student_sel);
        studentAcceptedListAdapter.hideTools = true;
        studentAcceptedListAdapter.isSubmitted = true;
        listView.setAdapter(studentAcceptedListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                openDisplayDialog(array_student.get(position));
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (array_student_sel.size() == 0) {
                    return;
                }
                ArrayList<Student> arrayList = new ArrayList<>(array_student_sel);
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Submitting...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                submitTest();
            }
        });
        loadSchoolNames();
        loadPsychologySections();
        return v;
    }
    void submitTest() {
        Student student = array_student_sel.get(0);
        Utils.mDatabase.child(Utils.tbl_psychology_submit).orderByChild("student_id").equalTo(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    PsychologySubmit submit = dataSnapshot.getValue(PsychologySubmit.class);
                    submit._id = dataSnapshot.getKey();
                } else {
                    PsychologySubmit submit = new PsychologySubmit("", Utils.currentUser._id, student.uid);
                    Utils.mDatabase.child(Utils.tbl_psychology_submit).push().setValue(submit);
                    for (User user:array_user) {
                        if (user._id.equals(student.uid)) {
                            App.sendPushMessage(user.token, "Psychology Test Required", "You are required of psychology test.", "", "", activity, App.PUSH_TEST, Utils.mUser.getUid());
                            break;
                        }
                    }
                }
                array_student_sel.remove(0);
                if (array_student_sel.size() == 0) {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Successfully submitted", Toast.LENGTH_SHORT).show();
                    studentAcceptedListAdapter.notifyDataSetChanged();
                } else {
                    submitTest();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }
    void filter_list() {
        String name_filter = edit_search_student.getText().toString().trim();
        array_student_filtered.clear();
        for (int i = 0; i < array_user.size(); i++) {
            User user = array_user.get(i);
            if (user.name.toLowerCase().contains(name_filter.toLowerCase())) {
                array_student_filtered.add(array_student.get(i));
            }
        }
        if (array_student_filtered.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
        studentAcceptedListAdapter.arrayList = array_student_filtered;
        studentAcceptedListAdapter.notifyDataSetChanged();
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
                    ly_no_items.setVisibility(View.GONE);
                    loadStudents(0);
                } else {
                    listView.setVisibility(View.GONE);
                    ly_no_items.setVisibility(View.VISIBLE);
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
        array_student_filtered = new ArrayList<>(array_student);
        studentAcceptedListAdapter.arrayList = array_student_filtered;
        studentAcceptedListAdapter.notifyDataSetChanged();
        if (array_student_filtered.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
        read_student_user();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                studentAcceptedListAdapter.notifyDataSetChanged();
            }
        }, 500);
    }
    void read_student_user() {
        array_user.clear(); array_student_sel.clear();
        for (Student student:array_student) {
            Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null) {
                        User user = dataSnapshot.getValue(User.class);
                        user._id = dataSnapshot.getKey();
                        array_user.add(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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