package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.Comment;
import com.ediattah.rezoschool.Model.Report;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class TweetDetailActivity extends AppCompatActivity {
    TextView txt_like, txt_dislike;
    Tweet tweet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Tweet Detail");
        tweet = (Tweet) getIntent().getSerializableExtra("tweet");
        final TextView txt_name = findViewById(R.id.txt_name);
        TextView txt_date = findViewById(R.id.txt_date);
        TextView txt_description = findViewById(R.id.txt_description);
        txt_date.setText(tweet.date);
        txt_description.setText(tweet.description);
        final CircleImageView img_user = findViewById(R.id.img_user);
        ImageView img_media = findViewById(R.id.img_media);
        txt_like = findViewById(R.id.txt_like);
        txt_dislike = findViewById(R.id.txt_dislike);
        Utils.mDatabase.child(Utils.tbl_user).orderByKey().equalTo(tweet.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            User user = childSnapshot.getValue(User.class);
                            txt_name.setText(user.name);
                            Glide.with(TweetDetailActivity.this).load(user.photo).apply(new RequestOptions()
                                    .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });
        txt_like.setText(String.valueOf(tweet.like));
        txt_dislike.setText(String.valueOf(tweet.dislike));


        Glide.with(this).load(tweet.media).apply(new RequestOptions()
                .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_media);
        img_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TweetDetailActivity.this, ImageViewerActivity.class);
                intent.putExtra("url", tweet.media);
                startActivity(intent);
            }
        });

        ImageButton ibtn_comment = findViewById(R.id.ibtn_comment);
        ibtn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCommentDialog(tweet);
            }
        });
        ImageButton ibtn_like = findViewById(R.id.ibtn_like);
        ibtn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.mDatabase.child(Utils.tbl_tweet).child(tweet._id).child("like").setValue(tweet.like+1);
                tweet.like += 1;
                txt_like.setText(String.valueOf(tweet.like));
                Toast.makeText(TweetDetailActivity.this, getResources().getString(R.string.you_like_this_tweet), Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton ibtn_dislike = findViewById(R.id.ibtn_dislike);
        ibtn_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.mDatabase.child(Utils.tbl_tweet).child(tweet._id).child("dislike").setValue(tweet.dislike+1);
                tweet.dislike += 1;
                txt_dislike.setText(String.valueOf(tweet.dislike));
                Toast.makeText(TweetDetailActivity.this, getResources().getString(R.string.you_dislike_this_tweet), Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton ibtn_report = findViewById(R.id.ibtn_report);
        ibtn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReportDialog(tweet);
            }
        });

        // ui thread for updating view!!!
        comment_update();
    }
    void comment_update() {




        runOnUiThread(new Runnable() {
            public void run() {
                final LinearLayout ly_feedback= findViewById(R.id.ly_feedback);
                final LayoutInflater inflater = LayoutInflater.from(TweetDetailActivity.this);
                ly_feedback.removeAllViews();
                for(Comment comment: tweet.comments) {
                    View view1 = inflater.inflate(R.layout.cell_tweet_comment, null);
                    TextView txt_comment = (TextView)view1.findViewById(R.id.txt_comment);
                    TextView txt_date = (TextView)view1.findViewById(R.id.txt_date);
                    final TextView txt_name = (TextView)view1.findViewById(R.id.txt_name);
                    final ImageView img_publisher = (ImageView)view1.findViewById(R.id.img_publisher);
                    txt_comment.setText(comment.comment);
                    txt_date.setText(comment.date);

                    Utils.mDatabase.child(Utils.tbl_user).orderByKey().equalTo(comment.uid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                        User user = childSnapshot.getValue(User.class);
                                        txt_name.setText(user.name);
                                        Glide.with(TweetDetailActivity.this).load(user.photo).apply(new RequestOptions()
                                                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_publisher);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w( "loadPost:onCancelled", databaseError.toException());
                                }
                            });
                    ly_feedback.addView(view1);
                }

            }
        });
    }
    public void openCommentDialog(final Tweet tweet) {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_add_comment, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.85);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.25);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        final EditText edit_comment = (EditText)view.findViewById(R.id.edit_comment);
        Button btn_add = (Button)view.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_comment = edit_comment.getText().toString().trim();
                if (txt_comment.length() == 0) {
                    Utils.showAlert(TweetDetailActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                Comment comment = new Comment("", Utils.mUser.getUid(), txt_comment, Utils.getCurrentDateString());
                tweet.comments.add(comment);
                Utils.mDatabase.child(Utils.tbl_tweet).child(tweet._id).child("comments").setValue(tweet.comments);
                dlg.dismiss();
                comment_update();
                Toast.makeText(TweetDetailActivity.this, getResources().getString(R.string.you_committed_successfully), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openReportDialog(final Tweet tweet) {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_add_report, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.85);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.25);
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
                    Utils.showAlert(TweetDetailActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                Report report1 = new Report("", Utils.mUser.getUid(), tweet._id, txt_report, Utils.currentUser.name);
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
                            Toast.makeText(TweetDetailActivity.this, getResources().getString(R.string.you_reported_successfully), Toast.LENGTH_SHORT).show();
                        } else {
                            Utils.mDatabase.child(Utils.tbl_report).push().setValue(report1);
                            dlg.dismiss();
                            Toast.makeText(TweetDetailActivity.this, getResources().getString(R.string.you_reported_successfully), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}