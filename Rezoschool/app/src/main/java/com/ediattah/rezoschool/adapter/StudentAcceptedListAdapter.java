package com.ediattah.rezoschool.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.PsychologySubmit;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.BulkSMSActivity;
import com.ediattah.rezoschool.ui.PsychologyResultActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import net.igenius.customcheckbox.CustomCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StudentAcceptedListAdapter extends BaseAdapter {
    public ArrayList<Student> arrayList;
    public ArrayList<Student> arraySel;
    boolean isSelectable = false;
    Context context;
    public boolean hideTools = false;
    public boolean isSubmitted = false;
    public boolean viewResult = false;

    public StudentAcceptedListAdapter(Context _context, ArrayList<Student> _arrayList, boolean isSelectable, ArrayList<Student> _arraySel) {
        context = _context;
        this.isSelectable = isSelectable;
        this.arrayList = _arrayList;
        this.arraySel = _arraySel;
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
            view = inflater.inflate(R.layout.cell_student_accepted, null);
        }
        LinearLayout ly_tool = view.findViewById(R.id.ly_tool);
        TextView txt_class = view.findViewById(R.id.txt_class);
        final TextView txt_school = view.findViewById(R.id.txt_school);
        final TextView txt_name = view.findViewById(R.id.txt_name);
        final TextView txt_new = view.findViewById(R.id.txt_new);
        final TextView txt_submitted = view.findViewById(R.id.txt_submitted);
        Button btn_result = view.findViewById(R.id.btn_result);
        final ImageView img_photo = (ImageView)view.findViewById(R.id.img_photo);
        ImageView img_chat = (ImageView)view.findViewById(R.id.img_chat);
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToChatPage(context, student.uid);
            }
        });
        ImageView img_sms = (ImageView)view.findViewById(R.id.img_sms);
        final User[] sel_user = new User[1];
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
        Button btn_parent = view.findViewById(R.id.btn_parent);
        btn_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dlg = new Dialog(context);
                Window window = dlg.getWindow();
                View view2 = ((Activity)context).getLayoutInflater().inflate(R.layout.dialog_view_items, null);
                int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.90);
                int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.4);
                view.setMinimumWidth(width);
                view.setMinimumHeight(height);
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dlg.setContentView(view2);
                window.setGravity(Gravity.CENTER);
                dlg.show();
                LinearLayout ly_item = view2.findViewById(R.id.ly_item);
                ly_item.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(context);
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
                                            View view1 = inflater.inflate(R.layout.cell_user, null);
                                            ImageView img_photo = view1.findViewById(R.id.img_photo);
                                            TextView txt_name = view1.findViewById(R.id.txt_name);
                                            TextView txt_email = view1.findViewById(R.id.txt_email);
                                            TextView txt_phone = view1.findViewById(R.id.txt_phone);
                                            Glide.with(context).load(user.photo).apply(new RequestOptions()
                                                    .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                                            txt_name.setText(user.name);
                                            txt_email.setText(user.email);
                                            txt_phone.setText(user.phone);
                                            ly_item.addView(view1);
                                        }
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
        });
        final RelativeLayout ly_status = view.findViewById(R.id.ly_status);
        Utils.mDatabase.child(Utils.tbl_user).child(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    User user = dataSnapshot.getValue(User.class);
                    user._id = dataSnapshot.getKey();
                    txt_name.setText(user.name);
                    Glide.with(context).load(user.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                    if (user.status == 0) {
                        ly_status.setBackground(context.getResources().getDrawable(R.drawable.status_offline));
                    } else {
                        ly_status.setBackground(context.getResources().getDrawable(R.drawable.status_online));
                    }
                    sel_user[0] = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Utils.mDatabase.child(Utils.tbl_school).child(student.school_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null) {
                    School school = dataSnapshot.getValue(School.class);
                    txt_school.setText(school.number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        txt_class.setText(student.class_name);
        if (student.isNew) {
            txt_new.setText("NEW");
            txt_new.setBackgroundColor(Color.parseColor("#c3edb9"));
        } else {
            txt_new.setText("OLD");
            txt_new.setBackgroundColor(Color.parseColor("#e28e8e"));
        }
        final CustomCheckBox chk_student = view.findViewById(R.id.chk_student);
        if (isSelectable) {
            chk_student.setVisibility(View.VISIBLE);
            chk_student.setChecked(arraySel.contains(student));
            chk_student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!arraySel.contains(student))
                        arraySel.add(student);
                    else
                        arraySel.remove(student);
                    notifyDataSetChanged();
                }
            });
        } else {
            chk_student.setVisibility(View.GONE);
        }
        if (hideTools) {
            btn_parent.setVisibility(View.GONE);
            ly_tool.setVisibility(View.GONE);

        }
        if (isSubmitted) {
            Utils.mDatabase.child(Utils.tbl_psychology_submit).orderByChild("student_id").equalTo(student.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null) {
                        PsychologySubmit submit = dataSnapshot.getValue(PsychologySubmit.class);
                        submit._id = dataSnapshot.getKey();
                        txt_submitted.setVisibility(View.VISIBLE);
                        chk_student.setVisibility(View.GONE);
                    } else {
                        txt_submitted.setVisibility(View.GONE);
                        chk_student.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }
            });
        }
        if (viewResult) {
            btn_result.setVisibility(View.VISIBLE);
            btn_result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PsychologyResultActivity.class);
                    intent.putExtra("STUDENT", student);
                    intent.putExtra("USER", sel_user[0]);
                    context.startActivity(intent);
                }
            });
        }
        return view;
    }
}
