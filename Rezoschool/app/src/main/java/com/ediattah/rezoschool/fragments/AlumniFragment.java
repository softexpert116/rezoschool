package com.ediattah.rezoschool.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import com.ediattah.rezoschool.Model.Library;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolListAdapter;
import com.ediattah.rezoschool.adapter.StudentAcceptedListAdapter;
import com.ediattah.rezoschool.ui.BulkSMSActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlumniFragment extends Fragment {
    MainActivity activity;
    ArrayList<Student> array_student_filtered = new ArrayList<>();
    ArrayList<Student> array_student_all = new ArrayList<>();
    ArrayList<User> array_user = new ArrayList<>();
    School sel_school;
    EditText edit_search_school;
    EditText edit_search_student;
    LinearLayout ly_no_items;
    StudentAcceptedListAdapter studentListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alumni, container, false);
        studentListAdapter = new StudentAcceptedListAdapter(activity,  array_student_filtered);
        ListView listView = (ListView)v.findViewById(R.id.listView);
        listView.setAdapter(studentListAdapter);
        ly_no_items = v.findViewById(R.id.ly_no_items);
        edit_search_school = v.findViewById(R.id.edit_search_school);
        edit_search_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddSchoolDialog();
            }
        });
        edit_search_student = v.findViewById(R.id.edit_search_student);
        edit_search_student.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edit_search_student.getText().toString().trim().length() == 0) {
                    array_student_filtered = new ArrayList<>(array_student_all);
                    studentListAdapter.arrayList = array_student_filtered;
                    studentListAdapter.notifyDataSetChanged();
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

        Button btn_bulk = (Button)v.findViewById(R.id.btn_bulk_sms);
        btn_bulk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BulkSMSActivity.class);
                activity.startActivity(intent);
            }
        });
        return v;
    }
    void filter_list() {
        String name_filter = edit_search_student.getText().toString().trim();
        array_student_filtered.clear();
        for (int i = 0; i < array_user.size(); i++) {
            User user = array_user.get(i);
            if (user.name.toLowerCase().contains(name_filter.toLowerCase())) {
                array_student_filtered.add(array_student_all.get(i));
            }
        }
        if (array_student_filtered.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
        studentListAdapter.arrayList = array_student_filtered;
        studentListAdapter.notifyDataSetChanged();
    }
    public void openAddSchoolDialog() {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_item, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText("Choose School");
        ListView listView = dlg.findViewById(R.id.listView);
        final ArrayList<School> array_all_school = new ArrayList<>();
        final SchoolListAdapter schoolAdapter = new SchoolListAdapter(activity, array_all_school);
        schoolAdapter.flag_view = true;
        listView.setAdapter(schoolAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                schoolAdapter.sel_index = i;
                schoolAdapter.notifyDataSetChanged();
            }
        });
        final Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setText("Choose");
        Utils.mDatabase.child(Utils.tbl_school).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    array_all_school.clear();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        array_all_school.add(school);
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        schoolAdapter.arrayList = array_all_school;
                        schoolAdapter.notifyDataSetChanged();
                        if (array_all_school.size() == 0) {
                            btn_choose.setEnabled(false);
                        } else {
                            btn_choose.setEnabled(true);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sel_school = array_all_school.get(schoolAdapter.sel_index);
                edit_search_school.setText(sel_school.number);
                dlg.dismiss();
                read_students();
            }
        });
        dlg.show();
    }
    public void read_students() {
        Utils.mDatabase.child(Utils.tbl_student).orderByChild("school_id").equalTo(sel_school._id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_student_all.clear();array_student_filtered.clear();
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        final Student student = datas.getValue(Student.class);
                        if (student.uid.equals(Utils.mUser.getUid())) {
                            continue;
                        }
                        array_student_all.add(student);
                    }
                    array_student_filtered = new ArrayList<>(array_student_all);
                }
                studentListAdapter.arrayList = array_student_filtered;
                studentListAdapter.notifyDataSetChanged();
                if (array_student_filtered.size() == 0) {
                    ly_no_items.setVisibility(View.VISIBLE);
                } else {
                    ly_no_items.setVisibility(View.GONE);
                }
                read_student_user();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void read_student_user() {
        array_user.clear();
        for (Student student:array_student_all) {
            Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null) {
                        User user = dataSnapshot.getValue(User.class);
                        array_user.add(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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