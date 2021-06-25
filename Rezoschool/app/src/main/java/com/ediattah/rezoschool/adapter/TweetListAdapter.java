package com.ediattah.rezoschool.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.Comment;
import com.ediattah.rezoschool.Model.Report;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Model.VideoGroup;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.TweetsFragment;
import com.ediattah.rezoschool.ui.ImageViewerActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TweetListAdapter extends BaseAdapter {
    ArrayList<Tweet> arrayList;

    MainActivity activity;
    TweetsFragment fragment;

    public TweetListAdapter(MainActivity _activity, TweetsFragment _fragment, ArrayList<Tweet> _arrayList) {
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
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Tweet tweet = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            view = inflater.inflate(R.layout.cell_tweet, null);
        }
//        TextView txt_date = (TextView)view.findViewById(R.id.txt_date);
        TextView txt_description = (TextView)view.findViewById(R.id.txt_description);
        ImageView img_media = view.findViewById(R.id.img_media);
        TextView txt_like = view.findViewById(R.id.txt_like);
        TextView txt_dislike = view.findViewById(R.id.txt_dislike);

//        txt_date.setText(tweet.date);
        txt_description.setText(tweet.description);
        txt_like.setText(String.valueOf(tweet.like));
        txt_dislike.setText(String.valueOf(tweet.dislike));

        Glide.with(activity).load(tweet.media).apply(new RequestOptions()
                .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_media);
        img_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ImageViewerActivity.class);
                intent.putExtra("url", tweet.media);
                activity.startActivity(intent);
            }
        });

        ImageButton ibtn_like = view.findViewById(R.id.ibtn_like);
        ibtn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.mDatabase.child(Utils.tbl_tweet).child(tweet._id).child("like").setValue(tweet.like+1);
                Toast.makeText(activity, activity.getResources().getString(R.string.you_like_this_tweet), Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton ibtn_dislike = view.findViewById(R.id.ibtn_dislike);
        ibtn_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.mDatabase.child(Utils.tbl_tweet).child(tweet._id).child("dislike").setValue(tweet.dislike+1);
                Toast.makeText(activity, activity.getResources().getString(R.string.you_dislike_this_tweet), Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton ibtn_report = view.findViewById(R.id.ibtn_report);
        ibtn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReportDialog(tweet);
            }
        });

        return view;
    }

    public void openReportDialog(final Tweet tweet) {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = fragment.getLayoutInflater().inflate(R.layout.dialog_add_report, null);
        int width = (int)(fragment.getResources().getDisplayMetrics().widthPixels*0.85);
        int height = (int)(fragment.getResources().getDisplayMetrics().heightPixels*0.25);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        final EditText edit_report = (EditText)view.findViewById(R.id.edit_report);
        Button btn_report = (Button)view.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_report = edit_report.getText().toString().trim();
                if (txt_report.length() == 0) {
                    Utils.showAlert(activity, activity.getResources().getString(R.string.warning), activity.getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                Report report1 = new Report("", Utils.mUser.getUid(), tweet._id, txt_report, Utils.currentUser.firstname+" "+Utils.currentUser.lastname);
                Utils.mDatabase.child(Utils.tbl_report).orderByChild("uid").equalTo(Utils.mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            String report_id = "";
                            for (DataSnapshot datas:dataSnapshot.getChildren()) {
                                Report report = datas.getValue(Report.class);
                                report._id = datas.getKey();
                                if (report.tweet_id.equals(tweet._id)) {
                                    report_id = report._id;
                                    break;
                                }
                            }
                            if (report_id.length() > 0) {
                                Utils.mDatabase.child(Utils.tbl_report).child(report_id).child("content").setValue(txt_report);
                            } else {
                                Utils.mDatabase.child(Utils.tbl_report).push().setValue(report1);
                            }
                            dlg.dismiss();
                            Toast.makeText(activity, activity.getResources().getString(R.string.you_reported_successfully), Toast.LENGTH_SHORT).show();
                        } else {
                            Utils.mDatabase.child(Utils.tbl_report).push().setValue(report1);
                            dlg.dismiss();
                            Toast.makeText(activity, activity.getResources().getString(R.string.you_reported_successfully), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
