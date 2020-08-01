package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.ChatRoom;
import com.ediattah.rezoschool.Model.Comment;
import com.ediattah.rezoschool.Model.Message;
import com.ediattah.rezoschool.Model.Tweet;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ChatListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {
    String roomId;
    User user;
    ListView listView;
    ChatListAdapter chatListAdapter;
    ArrayList<Message> arrayList = new ArrayList<>();
    private Uri imgUri;
    String user_id;
    TextView txt_title;
    LinearLayout ly_status;
    ImageView img_photo;
    TextView txt_typing;
    boolean flag_typing = false;
    int count = 0;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Chat");
        App.hideKeyboard(this);
        txt_title = findViewById(R.id.txt_title);
        txt_typing = findViewById(R.id.txt_typing);
        img_photo = findViewById(R.id.img_photo);
        ly_status = findViewById(R.id.ly_status);
        roomId = getIntent().getStringExtra("roomId");
        user_id = Utils.getChatUserId(roomId);
//        load_user(user_id);
        listView = findViewById(R.id.listView);
        chatListAdapter = new ChatListAdapter(this, arrayList);
        chatListAdapter.roomId = roomId;
        listView.setAdapter(chatListAdapter);

        Button btn_file = findViewById(R.id.btn_file);
        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ChatActivity.this);
            }
        });
        Button btn_send = findViewById(R.id.btn_send);
        final EditText edit_message = findViewById(R.id.edit_message);
        flag_typing = false;
        edit_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("isTyping").child(Utils.mUser.getUid()).setValue(true);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("isTyping").child(Utils.mUser.getUid()).setValue(false);
                    }
                }, 3000);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = edit_message.getText().toString().trim();
                if (msg.length() == 0) {
                    return;
                }
                Message message = new Message("", Utils.mUser.getUid(), user_id, msg, "", System.currentTimeMillis(), false);
                Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("messages").push().setValue(message);
                edit_message.setText("");
            }
        });
        load_user(user_id);
        readMessages();
        watchTypingEvent();
    }
    void watchTypingEvent() {
        Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("isTyping").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    boolean isTyping = Boolean.valueOf(dataSnapshot.getValue().toString());
                    if (isTyping) {
                        txt_typing.setVisibility(View.VISIBLE);
                    } else {
                        txt_typing.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void readMessages() {
        arrayList.clear();
        Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue()!=null) {
                    Message message = dataSnapshot.getValue(Message.class);
                    message._id = dataSnapshot.getKey();
                    arrayList.add(message);
                    chatListAdapter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(arrayList.size()-1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue()!=null) {
                    Message message = dataSnapshot.getValue(Message.class);
                    message._id = dataSnapshot.getKey();
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (message._id.equals(arrayList.get(i)._id)) {
                            arrayList.set(i, message);
                            break;
                        }
                    }
//                    arrayList.add(message);
                    chatListAdapter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(arrayList.size()-1);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    Message message = dataSnapshot.getValue(Message.class);
                    message._id = dataSnapshot.getKey();
                    for (int i = 0; i < arrayList.size(); i++) {
                        Message message1 = arrayList.get(i);
                        if (message1._id.equals(message._id)) {
                            arrayList.remove(i);
                            chatListAdapter.notifyDataSetChanged();
                            listView.smoothScrollToPosition(i-1);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void load_user(String user_id) {
        Utils.mDatabase.child(Utils.tbl_user).child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    user = dataSnapshot.getValue(User.class);
                    txt_title.setText("Chat With " + user.name);
                    Glide.with(ChatActivity.this).load(user.photo).apply(new RequestOptions()
                            .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                    if (user.status == 0) {
                        ly_status.setBackground(getResources().getDrawable(R.drawable.status_offline));
                    } else {
                        ly_status.setBackground(getResources().getDrawable(R.drawable.status_online));
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
//                imgUri = result.getUri();
                uploadPictureToFirebase(result.getUri());
//                Glide.with(EventCreateActivity.this).load(result.getUri()).centerCrop().placeholder(R.drawable.profile).dontAnimate().into(img_event);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
    void uploadPictureToFirebase(Uri imgUri) {
// Create the file metadata
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        final StorageReference file_refer = Utils.mStorage.child("chats/"+ts);

        // Listen for state changes, errors, and completion of the upload.
        file_refer.putFile(imgUri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        String downloadUrl = uri.toString();
                        Message message = new Message("", Utils.mUser.getUid(), user_id, "", downloadUrl, System.currentTimeMillis(), false);
                        Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("messages").push().setValue(message);
                    }
                });
            }

        });
    }
    public void delete_message(Message message) {
        Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("messages").child(message._id).setValue(null);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}