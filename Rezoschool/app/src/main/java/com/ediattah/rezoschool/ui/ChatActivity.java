package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
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
import com.ediattah.rezoschool.Model.Message;
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

import java.io.File;
import java.util.ArrayList;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

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
    int VOICE_REQUEST_CODE = 900;
    String audioFilePath = "";
    File audioFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        final Button btn_send = findViewById(R.id.btn_send);
        btn_send.setTag("record");
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
                if (edit_message.getText().toString().trim().length() > 0) {
                    btn_send.setTag("send");
                    btn_send.setBackground(getResources().getDrawable(R.drawable.ic_send));
                } else {
                    btn_send.setTag("record");
                    btn_send.setBackground(getResources().getDrawable(R.drawable.ic_record));
                }
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_send.getTag().equals("send")) {
                    String msg = edit_message.getText().toString().trim();
                    if (msg.length() == 0) {
                        return;
                    }
                    Message message = new Message("", Utils.mUser.getUid(), user_id, msg, "", "", System.currentTimeMillis(), false);
                    Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("messages").push().setValue(message);
                    edit_message.setText("");
                    App.sendPushMessage(user.token, "Chat from " + Utils.currentUser.name, message.message, "", "", ChatActivity.this, App.PUSH_CHAT, Utils.mUser.getUid());
                } else if (btn_send.getTag().equals("record")) {
                    if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
                        ArrayList<String> arrPermissionRequests = new ArrayList<>();
                        arrPermissionRequests.add(Manifest.permission.RECORD_AUDIO);
                        ActivityCompat.requestPermissions(ChatActivity.this, arrPermissionRequests.toArray(new String[arrPermissionRequests.size()]), 201);
                        return;
                    } else {
                        goToVoiceRecordPage();
                    }
//                    Intent intent = new Intent(ChatActivity.this, VoiceRecorderActivity.class);
//                    startActivityForResult(intent, VOICE_REQUEST_CODE);

                }
            }
        });

        load_user(user_id);
        readMessages();
        watchTypingEvent();

        ArrayList<String> array_message = App.readPreference_array_String(App.NewMessage);
        if (array_message.contains(user_id)) {
            array_message.remove(user_id);
            App.setPreference_array_String(App.NewMessage, array_message);
        }
    }
    void goToVoiceRecordPage() {
        Time time = new Time();
        time.setToNow();
//                    return new File(App.MY_AUDIO_PATH + File.separator + time.format("%Y%m%d%H%M%S") + "." + suffix);

        audioFilePath = App.MY_AUDIO_PATH + File.separator + time.format("%Y%m%d%H%M%S") + ".wav";

        int color = getResources().getColor(R.color.colorPrimaryDark);
        AndroidAudioRecorder.with(ChatActivity.this)
                // Required
                .setFilePath(audioFilePath)
                .setColor(color)
                .setRequestCode(VOICE_REQUEST_CODE)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.MONO)
                .setSampleRate(AudioSampleRate.HZ_16000)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
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
                    listView.setSelection(arrayList.size()-1);
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
                    try {
                        Glide.with(ChatActivity.this).load(user.photo).apply(new RequestOptions()
                                .placeholder(R.drawable.default_user).centerCrop().dontAnimate()).into(img_photo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 201: {
                if (grantResults[0] == 0) {
                    goToVoiceRecordPage();
                } else {
                    Toast.makeText(ChatActivity.this, "Permission denied", Toast.LENGTH_SHORT);
                    finish();
                }
                break;
            }
            default:
                Toast.makeText(ChatActivity.this, "Permission denied", Toast.LENGTH_SHORT);
                finish();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
//                imgUri = result.getUri();
                uploadFileToFirebase(result.getUri(), "jpeg");
//                Glide.with(EventCreateActivity.this).load(result.getUri()).centerCrop().placeholder(R.drawable.profile).dontAnimate().into(img_event);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == VOICE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = Uri.fromFile(new File(audioFilePath));
                if (uri!=null) {
                    uploadFileToFirebase(uri, "wav");
                }
            }
        }
    }
    void uploadFileToFirebase(Uri uri, final String file_type) {
// Create the file metadata
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        StorageMetadata metadata = null;
        if (file_type.equals("jpeg")) {
            metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();
        } else if (file_type.equals("wav")) {
            metadata = new StorageMetadata.Builder()
                    .setContentType("audio/wav")
                    .build();
        }
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        final StorageReference file_refer = Utils.mStorage.child("chats/"+ts);

        // Listen for state changes, errors, and completion of the upload.
        file_refer.putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressDialog.dismiss();
                        String downloadUrl = uri.toString();
                        Message message = new Message("", Utils.mUser.getUid(), user_id, "", downloadUrl, file_type, System.currentTimeMillis(), false);
                        Utils.mDatabase.child(Utils.tbl_chat).child(roomId).child("messages").push().setValue(message);
                        App.sendPushMessage(user.token, "Chat from " + Utils.currentUser.name, "File attached", "", "", ChatActivity.this, App.PUSH_CHAT, Utils.mUser.getUid());
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