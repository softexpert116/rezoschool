package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Exam;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.StudentDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.ChildListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.NewStudentActivity;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ParentStudentFragment extends Fragment {
    MainActivity activity;
    public Uri imgUri;
    public int sel_index = 0;
    ChildListAdapter childListAdapter;
    ArrayList<Student> array_student = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_parent_student, container, false);
        ListView listView = v.findViewById(R.id.listView);

        childListAdapter = new ChildListAdapter(activity, this, array_student);
        listView.setAdapter(childListAdapter);

        read_students();
        return v;
    }

    public void read_students() {
        Utils.mDatabase.child(Utils.tbl_school).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_student.clear();
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        for (Student student:school.students) {
                            if (student.parent_id.equals(Utils.mUser.getUid())) {
                                student.school_id = school._id;
                                array_student.add(student);
                            }
                        }
                    }
                }
                childListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        array_student.clear();
        for (Student student:Utils.currentSchool.students) {
            if (student.parent_id.equals(Utils.mUser.getUid())) {
                array_student.add(student);
            }
        }
        childListAdapter.notifyDataSetChanged();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                childListAdapter.notifyDataSetChanged();
//                Glide.with(EventCreateActivity.this).load(result.getUri()).centerCrop().placeholder(R.drawable.profile).dontAnimate().into(img_event);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(activity, getResources().getString(R.string.cropping_failed) + result.getError(), Toast.LENGTH_LONG).show();
            }
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