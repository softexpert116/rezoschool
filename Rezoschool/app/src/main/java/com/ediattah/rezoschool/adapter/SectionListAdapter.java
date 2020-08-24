package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.nileshpambhar.headerlistview.SectionAdapter;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;

public class SectionListAdapter extends SectionAdapter {
    Context context;
    public boolean isSchool = false;
    ArrayList<User> array_teacher, array_parent, array_student;
    ArrayList<User> array_sel;

    public SectionListAdapter(Context context, ArrayList<User> array_teacher, ArrayList<User> array_parent, ArrayList<User> array_student, ArrayList<User> _array_sel) {
        this.context = context;
        this.array_teacher = array_teacher;
        this.array_parent = array_parent;
        this.array_student = array_student;
        this.array_sel = _array_sel;
    }

    @Override
    public int numberOfSections() {
        return 3;
    }

    @Override
    public int numberOfRows(int section) {
        switch (section) {
            case 0:
                return array_teacher.size();
            case 1:
                return array_parent.size();
            case 2:
                return array_student.size();
        }
        return 0;
    }

    @Override
    public Object getRowItem(int section, int row) {
        return null;
    }

    @Override
    public boolean hasSectionHeaderView(int section) {
        switch (section) {
            case 0:
                if (array_teacher.size() == 0) {
                    return false;
                }
                break;
            case 1:
                if (array_parent.size() == 0) {
                    return false;
                }
                break;
            case 2:
                if (array_student.size() == 0) {
                    return false;
                }
                break;
        }
        return true;
    }

    @Override
    protected int getRowInSection(int position) {
        return super.getRowInSection(position);
    }

    @Override
    protected int getSection(int position) {
        return super.getSection(position);
    }

    @Override
    public View getRowView(int section, int row, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.cell_user_select, null);
        }
        User user = new User();
        switch (section) {
            case 0:
                user = array_teacher.get(row);
                break;
            case 1:
                user = array_parent.get(row);
                break;
            case 2:
                user = array_student.get(row);
                break;
        }
        final TextView txt_name = convertView.findViewById(R.id.txt_name);
        final ImageView img_photo = convertView.findViewById(R.id.img_photo);
        TextView txt_email = convertView.findViewById(R.id.txt_email);
        final TextView txt_phone = convertView.findViewById(R.id.txt_phone);
        final CustomCheckBox chk_student = convertView.findViewById(R.id.chk_student);
        boolean flag = false;
        for (User usr:array_sel) {
            if (usr._id.equals(user._id)) {
                flag = true;
                break;
            }
        }
        chk_student.setChecked(flag);

        User finalUser = user;
        chk_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flag_index = -1;
                for (int ii = 0; ii < array_sel.size(); ii++) {
                    User usr = array_sel.get(ii);
                    if (usr._id.equals(finalUser._id)) {
                        flag_index = ii;
                        break;
                    }
                }
                if (flag_index > -1) {
                    array_sel.remove(flag_index);
                } else {
                    array_sel.add(finalUser);
                }
                notifyDataSetChanged();
            }
        });

        txt_email.setText(user.email);
        txt_name.setText(user.name);
        txt_phone.setText(user.phone);
        Glide.with(context).load(user.photo).apply(new RequestOptions()
                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);

        return convertView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            convertView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
        }
        convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        String headerText = "Teacher";
        switch (section) {
            case 0:

                headerText = "Teacher";
                break;
            case 1:

                headerText = "Parent";
                break;
            case 2:

                headerText = "Student";
                break;
        }
        ((TextView) convertView).setText(headerText);
        ((TextView) convertView).setTextColor(context.getResources().getColor(R.color.white));
        return convertView;
    }

}
