package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;

public class UserSelectListAdapter extends BaseAdapter {
    ArrayList<User> arrayList;
    ArrayList<User> array_sel;
    Context context;

    public UserSelectListAdapter(Context _context, ArrayList<User> _arrayList, ArrayList<User> _array_sel) {
        context = _context;
        this.arrayList = _arrayList;
        this.array_sel = _array_sel;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        final User user = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_user_select, null);
        }
        final TextView txt_name = view.findViewById(R.id.txt_name);
        final ImageView img_photo = view.findViewById(R.id.img_photo);
        TextView txt_email = view.findViewById(R.id.txt_email);
        final TextView txt_phone = view.findViewById(R.id.txt_phone);
        final CustomCheckBox chk_student = view.findViewById(R.id.chk_student);
        boolean flag = false;
        for (User usr:array_sel) {
            if (usr._id.equals(user._id)) {
                flag = true;
                break;
            }
        }
        chk_student.setChecked(flag);

        chk_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flag_index = -1;
                for (int ii = 0; ii < array_sel.size(); ii++) {
                    User usr = array_sel.get(ii);
                    if (usr._id.equals(user._id)) {
                        flag_index = ii;
                        break;
                    }
                }
                if (flag_index > -1) {
                    array_sel.remove(flag_index);
                } else {
                    array_sel.add(user);
                }
                notifyDataSetChanged();
            }
        });

        txt_email.setText(user.email);
        txt_name.setText(user.name);
        txt_phone.setText(user.phone);
        Glide.with(context).load(user.photo).apply(new RequestOptions()
                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);

        return view;
    }
}
