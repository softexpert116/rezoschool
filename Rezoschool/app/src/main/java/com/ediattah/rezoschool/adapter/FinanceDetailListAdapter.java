package com.ediattah.rezoschool.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Transaction;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.BulkSMSActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FinanceDetailListAdapter extends BaseAdapter {
    ArrayList<Student> arrayList;
    public boolean flag_parent = false;
    Context context;

    public FinanceDetailListAdapter(Context _context, ArrayList<Student> _arrayList) {
        context = _context;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Student student = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_finance_detail, null);
        }
        final TextView txt_remain = view.findViewById(R.id.txt_remain);
        final TextView txt_paid = view.findViewById(R.id.txt_paid);
        final TextView txt_parent_name = view.findViewById(R.id.txt_parent_name);
        final TextView txt_parent_email = view.findViewById(R.id.txt_parent_email);
        final TextView txt_parent_phone = view.findViewById(R.id.txt_parent_phone);
        TextView txt_student_level = view.findViewById(R.id.txt_student_level);
        TextView txt_student_class = view.findViewById(R.id.txt_student_class);
        TextView txt_new = view.findViewById(R.id.txt_new);
        final ImageView img_parent = view.findViewById(R.id.img_parent);
        final ImageView img_student = view.findViewById(R.id.img_student);
        final TextView txt_student_name = view.findViewById(R.id.txt_student_name);
        ImageView img_chat = (ImageView)view.findViewById(R.id.img_chat);
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToChatPage(context, student.parent_id);
            }
        });
        final User[] sel_user = new User[1];
        ImageView img_sms = (ImageView)view.findViewById(R.id.img_sms);
        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BulkSMSActivity.class);
                intent.putExtra("USER", sel_user[0]);
                context.startActivity(intent);
            }
        });
        ImageView img_video = (ImageView)view.findViewById(R.id.img_video);
        img_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToStartVideoCallPage(sel_user[0], context);
            }
        });
        Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    txt_student_name.setText(user.name);
                    try {
                        Glide.with(context).load(user.photo).apply(new RequestOptions()
                                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_student);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Utils.mDatabase.child(Utils.tbl_user).child(student.parent_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    txt_parent_email.setText(user.email);
                    txt_parent_name.setText(user.name);
                    txt_parent_phone.setText(user.phone);
                    try {
                        Glide.with(context).load(user.photo).apply(new RequestOptions()
                                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_parent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sel_user[0] = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        txt_student_class.setText(student.class_name);
        String level = "";
        float fee = 0;
        for (Class _class:Utils.currentSchool.classes) {
            if (_class.name.equals(student.class_name)) {
                level = _class.level;
                break;
            }
        }
        for (Level le:Utils.currentSchool.levels) {
            if (le.name.equals(level)) {
                txt_student_level.setText(le.name + ", " + le.fee + "XOF/month");
                fee = Float.valueOf(le.fee);
                break;
            }
        }
        final float finalFee = fee;
        Utils.mDatabase.child(Utils.tbl_transaction).orderByChild("student_id").equalTo(student.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    float paid = 0;
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        Transaction transaction = datas.getValue(Transaction.class);
                        if (transaction.school_id.equals(Utils.currentSchool._id) && transaction.status.equals("100")) {
                            float amount = Float.valueOf(transaction.amount);
                            paid += amount;
                        }
                    }
                    try {
                        txt_paid.setText(String.valueOf(paid) + "XOF");
                        txt_remain.setText(String.valueOf(finalFee *12-paid) + "XOF");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (student.isNew) {
            txt_new.setText("NEW");
            txt_new.setBackgroundColor(Color.parseColor("#c3edb9"));
        } else {
            txt_new.setText("OLD");
            txt_new.setBackgroundColor(Color.parseColor("#e28e8e"));
        }


        return view;
    }
}
