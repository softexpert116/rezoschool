package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Model.VideoGroup;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SectionListAdapter;
import com.ediattah.rezoschool.adapter.UserSelectListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nileshpambhar.headerlistview.HeaderListView;

import java.util.ArrayList;

public class NewVideoGroupActivity extends AppCompatActivity {
    ArrayList<User> array_teacher = new ArrayList<>();
    ArrayList<User> array_parent = new ArrayList<>();
    ArrayList<User> array_student = new ArrayList<>();
    ArrayList<User> array_sel = new ArrayList<>();
    SectionListAdapter sectionListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_video_group);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("New Video Group");
        App.hideKeyboard(this);
        HeaderListView listView = findViewById(R.id.listView);
        sectionListAdapter = new SectionListAdapter(this, array_teacher, array_parent, array_student, array_sel);
        listView.setAdapter(sectionListAdapter);
        Button btn_create = findViewById(R.id.btn_create);
        EditText edit_name = findViewById(R.id.edit_name);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit_name.getText().toString().trim();
                if (name.length() == 0) {
                    Utils.showAlert(NewVideoGroupActivity.this, "Warning", "Please input group name");
                    return;
                }
                if (array_sel.size() == 0) {
                    Utils.showAlert(NewVideoGroupActivity.this, "Warning", "Please select group members");
                    return;
                }

                VideoGroup videoGroup = new VideoGroup("", Utils.currentUser, array_sel, name, App.getTimestampString(), System.currentTimeMillis());
                Utils.mDatabase.child(Utils.tbl_group).orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            Utils.showAlert(NewVideoGroupActivity.this, "Warning", "The group name already exists!");
                        } else {
                            Utils.mDatabase.child(Utils.tbl_group).push().setValue(videoGroup);
                            Toast.makeText(NewVideoGroupActivity.this, "Successfully created!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        read_users();
    }

    void read_users() {
        array_teacher.clear(); array_parent.clear(); array_student.clear();
        if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
            for (Teacher teacher:Utils.currentSchool.teachers) {
                Utils.mDatabase.child(Utils.tbl_user).child(teacher.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            User user = dataSnapshot.getValue(User.class);
                            user._id = dataSnapshot.getKey();
                            array_teacher.add(user);
                        }
                        sectionListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            for (Student student:Utils.currentSchool.students) {
                Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            User user = dataSnapshot.getValue(User.class);
                            user._id = dataSnapshot.getKey();
                            array_student.add(user);
                        }
                        sectionListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Utils.mDatabase.child(Utils.tbl_parent_student).orderByChild("student_id").equalTo(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            for (DataSnapshot datas:dataSnapshot.getChildren()) {
                                String parent_id = datas.child("parent_id").getValue(String.class);
                                Utils.mDatabase.child(Utils.tbl_user).child(parent_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue()!=null) {
                                            User user = dataSnapshot.getValue(User.class);
                                            user._id = dataSnapshot.getKey();
                                            array_parent.add(user);
                                        }
                                        sectionListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } else {
            Utils.mDatabase.child(Utils.tbl_user).orderByChild(Utils.USER_TYPE).equalTo(Utils.currentUser.type).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null) {
                        for (DataSnapshot datas:dataSnapshot.getChildren()) {
                            User user = datas.getValue(User.class);
                            user._id = datas.getKey();
                            if (user._id.equals(Utils.mUser.getUid())) {
                                continue;
                            }
                            if (Utils.currentUser.type.equals(Utils.TEACHER)) {
                                array_teacher.add(user);
                            } else if (Utils.currentUser.type.equals(Utils.PARENT)) {
                                array_parent.add(user);
                            } else if (Utils.currentUser.type.equals(Utils.STUDENT)) {
                                array_student.add(user);
                            }
                        }
                    }
                    sectionListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}