package com.ediattah.rezoschool.adapter;

import android.app.Dialog;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.Model.Comment;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.TweetsFragment;
import com.ediattah.rezoschool.ui.ImageViewerActivity;
import com.ediattah.rezoschool.ui.LoginActivity;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
        final TextView txt_name = (TextView)view.findViewById(R.id.txt_name);
        TextView txt_date = (TextView)view.findViewById(R.id.txt_date);
        TextView txt_description = (TextView)view.findViewById(R.id.txt_description);
        final CircleImageView img_user = view.findViewById(R.id.img_user);
        ImageView img_media = view.findViewById(R.id.img_media);
        TextView txt_like = view.findViewById(R.id.txt_like);
        TextView txt_dislike = view.findViewById(R.id.txt_dislike);
        Utils.mDatabase.child(Utils.tbl_user).orderByKey().equalTo(tweet.uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            User user = childSnapshot.getValue(User.class);
                            txt_name.setText(user.name);
                            Glide.with(activity).load(user.photo).apply(new RequestOptions()
                                    .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        txt_date.setText(tweet.date);
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

        ImageButton ibtn_comment = view.findViewById(R.id.ibtn_comment);
        ibtn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog(tweet);
            }
        });
        ImageButton ibtn_like = view.findViewById(R.id.ibtn_like);
        ibtn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.mDatabase.child(Utils.tbl_tweet).child(tweet._id).child("like").setValue(tweet.like+1);
                Toast.makeText(activity, "You like this tweet!", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton ibtn_dislike = view.findViewById(R.id.ibtn_dislike);
        ibtn_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.mDatabase.child(Utils.tbl_tweet).child(tweet._id).child("dislike").setValue(tweet.dislike+1);
                Toast.makeText(activity, "You dislike this tweet!", Toast.LENGTH_SHORT).show();
            }
        });

        // ui thread for updating view!!!
        final View finalView = view;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                final LinearLayout ly_feedback= finalView.findViewById(R.id.ly_feedback);
                final LayoutInflater inflater = LayoutInflater.from(activity);
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
                                        Glide.with(activity).load(user.photo).apply(new RequestOptions()
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
        return view;
    }
    public void openAddDialog(final Tweet tweet) {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = fragment.getLayoutInflater().inflate(R.layout.dialog_add_comment, null);
        int width = (int)(fragment.getResources().getDisplayMetrics().widthPixels*0.85);
        int height = (int)(fragment.getResources().getDisplayMetrics().heightPixels*0.25);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
//        dlg.getWindow().setLayout(width, height);
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
                    Utils.showAlert(activity, "Warning", "Please fill in blank field");
                    return;
                }
                Comment comment = new Comment("", Utils.mUser.getUid(), txt_comment, Utils.getCurrentDateString());
                tweet.comments.add(comment);
                Utils.mDatabase.child(Utils.tbl_tweet).child(tweet._id).child("comments").setValue(tweet.comments);
                dlg.dismiss();
                Toast.makeText(activity, "You've committed successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
