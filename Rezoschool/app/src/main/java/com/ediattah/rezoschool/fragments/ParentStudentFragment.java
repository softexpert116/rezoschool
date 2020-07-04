package com.ediattah.rezoschool.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
        array_student.add(new Student("1", "1", true));
        array_student.add(new Student("1", "1", false));

        childListAdapter = new ChildListAdapter(activity, this, array_student);
        listView.setAdapter(childListAdapter);
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewStudentActivity.class);
                activity.startActivity(intent);
            }
        });
        return v;
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
                Toast.makeText(activity, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
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