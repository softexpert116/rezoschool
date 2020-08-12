package com.ediattah.rezoschool.adapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Absence;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.ui.BulkSMSActivity;
import com.ediattah.rezoschool.ui.NewPaymentActivity;
import com.ediattah.rezoschool.ui.ParentViewExamActivity;
import com.ediattah.rezoschool.ui.ParentViewTeacherActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.fragments.ParentStudentFragment;
import com.ediattah.rezoschool.ui.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

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
        final TextView txt_name = view.findViewById(R.id.txt_name);
        TextView txt_class = view.findViewById(R.id.txt_class);
        final TextView txt_level = view.findViewById(R.id.txt_level);
        final TextView txt_school = view.findViewById(R.id.txt_school);
        final ImageView img_photo = view.findViewById(R.id.img_photo);
        final ImageView img_pic = view.findViewById(R.id.img_pic);
        final Button btn_inform = view.findViewById(R.id.btn_inform);
        Button btn_teacher = view.findViewById(R.id.btn_teacher);
        Button btn_exam = view.findViewById(R.id.btn_exam);
        Button btn_pay = view.findViewById(R.id.btn_pay);
        ImageView img_school_photo = view.findViewById(R.id.img_school_photo);
        TextView txt_school_staff = view.findViewById(R.id.txt_school_staff);
        RelativeLayout ly_status = view.findViewById(R.id.ly_status);
        ImageView img_chat = view.findViewById(R.id.img_chat);
        ImageView img_sms = view.findViewById(R.id.img_sms);
        ImageView img_video = view.findViewById(R.id.img_video);
        img_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.sel_index = i;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity, fragment);
            }
        });
        final User[] sel_user = new User[1];
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToChatPage(activity, sel_user[0]._id);
            }
        });
        img_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.goToVideoCallPage(activity);
            }
        });
        img_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BulkSMSActivity.class);
                intent.putExtra("USER", sel_user[0]);
                activity.startActivity(intent);
            }
        });
        btn_inform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment.imgUri == null) {
                    Utils.showAlert(activity, "Warning", "Please upload a picture.");
                    return;
                }
                final ProgressDialog progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build();
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                final StorageReference file_refer = Utils.mStorage.child("absences/"+ts);

                file_refer.putFile(fragment.imgUri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads on complete
                        file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String downloadUrl = uri.toString();
                                Absence absence = new Absence("", Calendar.getInstance().getTime(), downloadUrl, model.school_id, model.uid);
                                Utils.mDatabase.child(Utils.tbl_absence).push().setValue(absence);
                                progressDialog.dismiss();
                                Toast.makeText(activity, "Successfully submitted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                });
            }
        });
        btn_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ParentViewTeacherActivity.class);
                intent.putExtra("OBJECT", model);
                activity.startActivity(intent);
            }
        });
        btn_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ParentViewExamActivity.class);
                intent.putExtra("OBJECT", model);
                activity.startActivity(intent);
            }
        });
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NewPaymentActivity.class);
                intent.putExtra("OBJECT", model);
                activity.startActivity(intent);
            }
        });
        txt_class.setText(model.class_name);
        Utils.mDatabase.child(Utils.tbl_user).child(model.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    txt_name.setText(user.name);
                    try {
                        Glide.with(activity).load(user.photo).apply(new RequestOptions()
                                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (fragment.imgUri != null && fragment.sel_index == i) {
            img_pic.setImageURI(fragment.imgUri);
        } else {
            Utils.mDatabase.child(Utils.tbl_absence).orderByChild("uid").equalTo(model.uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null) {
                        for (DataSnapshot datas:dataSnapshot.getChildren()) {
                            Absence absence = datas.getValue(Absence.class);
                            if (absence.school_id.equals(model.school_id)) {
                                String date_str = Utils.getDateString(absence.date);
                                String today_str = Utils.getDateString(Calendar.getInstance().getTime());
                                if (date_str.equals(today_str)) {
                                    try {
                                        Glide.with(activity).load(absence.url).apply(new RequestOptions()
                                                .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_pic);
                                        img_pic.setEnabled(false);
                                        btn_inform.setEnabled(false);
                                        btn_inform.setBackground(activity.getDrawable(R.drawable.btn_round_gray));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    return;
                                }
                            }
                        }
                    }
                    img_pic.setImageDrawable(activity.getDrawable(R.drawable.default_pic));
                    img_pic.setEnabled(true);
                    btn_inform.setEnabled(true);
                    btn_inform.setBackground(activity.getDrawable(R.drawable.btn_round));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Utils.mDatabase.child(Utils.tbl_school).child(model.school_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    School school = dataSnapshot.getValue(School.class);
                    String mlevel = "", mfee = "";
                    for (Class _class:school.classes) {
                        if (_class.name.equals(model.class_name)) {
                            mlevel = _class.level;
                            break;
                        }
                    }
                    for (Level _level:school.levels) {
                        if (_level.name.equals(mlevel)) {
                            mfee = _level.fee;
                            break;
                        }
                    }
                    txt_school.setText(school.number);
                    txt_level.setText(mlevel + ", " + mfee + "XOF/month");

                    Utils.mDatabase.child(Utils.tbl_user).child(school.uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
                                User user = dataSnapshot.getValue(User.class);
                                user._id = dataSnapshot.getKey();
                                txt_school_staff.setText(user.name);
                                try {
                                    Glide.with(activity).load(user.photo).apply(new RequestOptions()
                                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_school_photo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (user.status == 0) {
                                    ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_offline));
                                } else {
                                    ly_status.setBackground(activity.getResources().getDrawable(R.drawable.status_online));
                                }
                                sel_user[0] = user;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
}
