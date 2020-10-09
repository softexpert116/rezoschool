package com.ediattah.rezoschool.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Transaction;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.StudentBulkSMSListAdapter;
import com.ediattah.rezoschool.httpsModule.RestClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class BulkSMSActivity extends AppCompatActivity {
    ArrayList<String> array_sel_name = new ArrayList<>();
    ArrayList<String> array_sel_phone = new ArrayList<>();
    School sel_school;
    User user;
    TextView txt_receivers;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_s_m_s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sel_school = (School)getIntent().getSerializableExtra("SCHOOL");
        user = (User)getIntent().getSerializableExtra("USER");

        final EditText edit_username = findViewById(R.id.edit_username);
        final EditText edit_password = findViewById(R.id.edit_password);
        final EditText edit_senderID = findViewById(R.id.edit_senderID);
        final EditText edit_text = findViewById(R.id.edit_text);
        txt_receivers = findViewById(R.id.txt_receivers);
        TextView txt_select = findViewById(R.id.txt_select);
        CardView cardView = findViewById(R.id.card_select);
        if (sel_school != null) {
            setTitle(getResources().getString(R.string.bulk_sms));
            txt_select.setText(getResources().getString(R.string.select_users_in_school_) + sel_school.number);
            StudentBulkSMSListAdapter studentBulkSMSListAdapter = new StudentBulkSMSListAdapter(this, txt_receivers, sel_school.students, array_sel_name, array_sel_phone);
            ListView listView = (ListView)findViewById(R.id.listView);
            listView.setAdapter(studentBulkSMSListAdapter);
        } else {
            setTitle(getResources().getString(R.string.sms));
            txt_select.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
            txt_receivers.setText(user.name);
            array_sel_phone.add(user.phone);
        }
        Button btn_send = findViewById(R.id.btn_send_sms);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edit_username.getText().toString().trim();
                String password = edit_password.getText().toString().trim();
                String senderID = edit_senderID.getText().toString().trim();
                String text = edit_text.getText().toString().trim();
                if (username.length()*password.length()*senderID.length()*text.length() == 0) {
                    Utils.showAlert(BulkSMSActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                if (array_sel_phone.size() == 0) {
                    Utils.showAlert(BulkSMSActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                sendEdiaSMS(username, password, senderID, array_sel_phone, text, "text");
            }
        });
    }
    private void sendEdiaSMS(String username, String password, String sender, ArrayList<String> array_receivers, String text, String type) {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getResources().getString(R.string.sending_sms));
        mDialog.setCancelable(false);
        mDialog.show();
        String to = TextUtils.join(";", array_receivers);
        String datetime = Utils.getCurrentDateTimeString();
        final JSONObject object = new JSONObject();
        try {
            object.put("username", username);
            object.put("password", password);
            object.put("sender", sender);
            object.put("to", to);
            object.put("text", text);
            object.put("type", type);
            object.put("datetime", datetime);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final RestClient restClient = RestClient.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = restClient.postRequest(App.ediaSMSUrl, object);
                if (response.contains("OK:")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BulkSMSActivity.this, getResources().getString(R.string.successfully_sent), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (response.contains("ERROR:")) {
                    final String error = response.substring(7);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.showAlert(BulkSMSActivity.this, getResources().getString(R.string.error), error);
                        }
                    });
                }
                mDialog.dismiss();

            }
        }).start();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}