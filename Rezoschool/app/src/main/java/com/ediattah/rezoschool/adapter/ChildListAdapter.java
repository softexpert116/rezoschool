package com.ediattah.rezoschool.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.ParentStudentFragment;
import com.ediattah.rezoschool.ui.MainActivity;

import java.util.ArrayList;

public class ChildListAdapter extends BaseAdapter {
    ArrayList<Student> arrayList;

    MainActivity activity;
    ParentStudentFragment fragment;

    public ChildListAdapter(MainActivity _activity, ParentStudentFragment _fragment, ArrayList<Student> _arrayList) {
        activity = _activity;
        fragment = _fragment;
        this.arrayList = _arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Student model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(R.layout.cell_parent_student, null);
        }
        TextView txt_name = view.findViewById(R.id.txt_name);
        TextView txt_school = view.findViewById(R.id.txt_school);
        ImageView img_pic = view.findViewById(R.id.img_pic);
        Button btn_inform = view.findViewById(R.id.btn_inform);
        Button btn_teacher = view.findViewById(R.id.btn_teacher);
        ImageView img_call = view.findViewById(R.id.img_call);
        ImageView img_sms = view.findViewById(R.id.img_sms);
        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.sel_index = i;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity, fragment);
            }
        });
        if (fragment.sel_index == i) {
            img_pic.setImageURI(fragment.imgUri);
        } else {

        }
        img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "call clicked", Toast.LENGTH_SHORT).show();
            }
        });
        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "sms clicked", Toast.LENGTH_SHORT).show();
            }
        });
        btn_inform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "inform clicked", Toast.LENGTH_SHORT).show();
            }
        });
        btn_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }
}
