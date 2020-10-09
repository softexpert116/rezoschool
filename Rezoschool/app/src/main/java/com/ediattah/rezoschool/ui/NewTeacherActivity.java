package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.TeacherListAdapter;

import java.util.ArrayList;

public class NewTeacherActivity extends AppCompatActivity {
    TeacherListAdapter teacherListAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_teacher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.add_new_teacher));
        App.hideKeyboard(this);

        listView = findViewById(R.id.listView);
//        teacherListAdapter = new TeacherListAdapter(this, arrayList);
//        listView.setAdapter(teacherListAdapter);
//
//        final EditText edit_search = findViewById(R.id.edit_search);
//        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    if (edit_search.getText().toString().trim().length() == 0) {
//                        arrayList = (ArrayList<UserModel>) App.array_teacher.clone();
//                    } else {
//                        arrayList.clear();
//                        for (UserModel model:App.array_teacher) {
//                            if (model.name.toLowerCase().contains(edit_search.getText().toString().toLowerCase().trim())) {
//                                arrayList.add(model);
//                            }
//                        }
//                    }
//                    teacherListAdapter = new TeacherListAdapter(NewTeacherActivity.this, arrayList);
//                    listView.setAdapter(teacherListAdapter);
//                    App.hideKeyboard(NewTeacherActivity.this);
//                    return true;
//                }
//                return false;
//            }
//        });
//        edit_search.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
////                if (edit_search.getText().toString().trim().length() == 0) {
////                    arrayList = (ArrayList<UserModel>) App.array_teacher.clone();
////                } else {
////                    arrayList.clear();
////                    for (UserModel model:App.array_teacher) {
////                        if (model.name.toLowerCase().contains(edit_search.getText().toString().toLowerCase().trim())) {
////                            arrayList.add(model);
////                        }
////                    }
////                }
////                teacherListAdapter = new TeacherListAdapter(NewTeacherActivity.this, arrayList);
////                listView.setAdapter(teacherListAdapter);
//            }
//        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}