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

public class StudentBulkSMSListAdapter extends BaseAdapter {
    ArrayList<Student> arrayList;
    ArrayList<String> array_sel_name;
    ArrayList<String> array_sel_phone;
    TextView textView;
    Context context;

    public StudentBulkSMSListAdapter(Context _context, TextView txtView, ArrayList<Student> _arrayList, ArrayList<String> _array_sel_name, ArrayList<String> _array_sel_phone) {
        context = _context;
        this.textView = txtView;
        this.arrayList = _arrayList;
        this.array_sel_name = _array_sel_name;
        this.array_sel_phone = _array_sel_phone;
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
        final Student student = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_student_bulk_sms, null);
        }
        final TextView txt_name = view.findViewById(R.id.txt_name);
        final ImageView img_photo = view.findViewById(R.id.img_photo);
        TextView txt_class = view.findViewById(R.id.txt_class);
        final TextView txt_phone = view.findViewById(R.id.txt_phone);
        TextView txt_new = view.findViewById(R.id.txt_new);
        final CustomCheckBox chk_student = view.findViewById(R.id.chk_student);

//        chk_student.setChecked(array_sel_phone.contains(txt_phone.getText().toString().trim()));
        chk_student.setChecked(false);
        chk_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txt_name.getText().toString().trim();
                String phone = txt_phone.getText().toString().trim();
                if (!array_sel_phone.contains(phone)) {
                    array_sel_phone.add(phone);
                    array_sel_name.add(name);
                } else {
                    array_sel_phone.remove(phone);
                    array_sel_name.remove(name);
                }
                textView.setText("");
                notifyDataSetChanged();
            }
        });

        txt_class.setText(student.class_name);
        if (student.isNew) {
            txt_new.setText(context.getResources().getString(R.string._new));
            txt_new.setBackgroundColor(Color.parseColor("#c3edb9"));
        } else {
            txt_new.setText(context.getResources().getString(R.string.old));
            txt_new.setBackgroundColor(Color.parseColor("#e28e8e"));
        }
        Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    User user = dataSnapshot.getValue(User.class);
                    String text = textView.getText().toString();
                    if (array_sel_phone.contains(user.phone)) {
                        chk_student.setChecked(true);
                        if (text.length() == 0) {
                            text += user.name;
                        } else {
                            text += "," + user.name;
                        }
                        textView.setText(text);
                    }
                    txt_name.setText(user.name);
                    txt_phone.setText(user.phone);
                    Glide.with(context).load(user.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
}
