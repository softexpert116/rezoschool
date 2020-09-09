package com.ediattah.rezoschool.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Absence;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Model.VideoGroup;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.VideoFragment;
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

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoGroupListAdapter extends BaseAdapter {
    ArrayList<VideoGroup> arrayList;
    Context context;
    VideoFragment fragment;
    public VideoGroupListAdapter(Context _context, VideoFragment _fragment, ArrayList<VideoGroup> _arrayList) {
        context = _context;
        this.arrayList = _arrayList;
        this.fragment = _fragment;
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
        final VideoGroup model = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_video_group, null);
        }
        final TextView txt_creator = view.findViewById(R.id.txt_creator);
        final TextView txt_name = view.findViewById(R.id.txt_name);
        txt_name.setText(model.name);
        ImageView img_creator = view.findViewById(R.id.img_creator);
        Utils.mDatabase.child(Utils.tbl_user).child(model.creator_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    txt_creator.setText(user.name);
                    Glide.with(context).load(user.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_creator);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        LinearLayout ly_members = view.findViewById(R.id.ly_members);
        ly_members.removeAllViews();
        for (String uid:model.member_ids) {
            CircleImageView img = new CircleImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(50, 50);
            lp.setMargins(5, 0, 0, 0);
            img.setLayoutParams(lp);
            ly_members.addView(img);
            Utils.mDatabase.child(Utils.tbl_user).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()!=null) {
                        User user = dataSnapshot.getValue(User.class);
                        Glide.with(context).load(user.photo).apply(new RequestOptions()
                                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Button btn_call = (Button)view.findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (fragment.flag_my) {
                    builder.setMessage("Are you going to start a group video call?");
                } else {
                    builder.setMessage("Are you going to join a group video call?");
                }
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if (fragment.flag_my) {
                            App.goToStartG_VideoCallPage(model, context);
                        } else {
                            App.goToJoinVideoCall(model.name, context);
                            ArrayList<String> array_video_group = App.readPreference_array_String(App.NewVideoGroup);
                            String vtoken = model.creator_id + " " + model.room;
                            if (array_video_group.contains(vtoken)) {
                                array_video_group.remove(vtoken);
                                App.setPreference_array_String(App.NewVideoGroup, array_video_group);
                            }
                        }
                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        Button btn_remove = (Button)view.findViewById(R.id.btn_remove);
        if (fragment.flag_my) {
            btn_remove.setVisibility(View.VISIBLE);
            btn_call.setText("Start call");
            btn_call.setEnabled(true);
            btn_call.setBackground(context.getDrawable(R.drawable.btn_round));
        } else {
            btn_remove.setVisibility(View.GONE);
            btn_call.setText("Join call");
            ArrayList<String> array_video_group = App.readPreference_array_String(App.NewVideoGroup);
            String vtoken = model.creator_id + " " + model.room;
            if (array_video_group.contains(vtoken)) {
                btn_call.setEnabled(true);
                btn_call.setBackground(context.getDrawable(R.drawable.btn_round));
            } else {
                btn_call.setEnabled(false);
                btn_call.setBackground(context.getDrawable(R.drawable.btn_round_gray));
            }
        }
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you going to remove this item?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Utils.mDatabase.child(Utils.tbl_group).child(model._id).setValue(null);
                        Toast.makeText(context, "Successfully removed", Toast.LENGTH_SHORT).show();
                        fragment.read_videoGroup();
                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return view;
    }
}
