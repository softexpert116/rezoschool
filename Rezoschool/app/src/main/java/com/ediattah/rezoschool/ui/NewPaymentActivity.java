package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Transaction;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.httpsModule.RestClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class NewPaymentActivity extends AppCompatActivity {
    Student sel_student;
    EditText edit_student, edit_level_name, edit_level_fee;
    String gatewayid = "1", merchantID = "", uniqeID = "";
    String transactionid = "", pay_link = "";
    float amount = 0;
    TextView txt_mobile;
    ProgressDialog mDialog;
    String method = "MTN Mobile Money";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_payment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.new_payment));
        App.hideKeyboard(this);
        sel_student = (Student)getIntent().getSerializableExtra("OBJECT");

        final EditText edit_amount = findViewById(R.id.edit_amount);
        txt_mobile = findViewById(R.id.txt_mobile);
        edit_student = findViewById(R.id.edit_student);
        edit_level_name = findViewById(R.id.edit_level_name);
        edit_level_fee = findViewById(R.id.edit_level_fee);
        final EditText edit_merchant = findViewById(R.id.edit_merchant);
        loadData();

        RadioGroup pay_method = findViewById(R.id.pay_method);
        pay_method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                txt_mobile.setVisibility(View.GONE);
                if (checkedId == R.id.pay_mtn) {
                    method = "MTN Mobile Money";
                    gatewayid = "1";
                    txt_mobile.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.pay_dunya) {
                    gatewayid = "2";
                    method = "PayDunya";
                } else {
                    gatewayid = "3";
                    method = "BICI";
                }
            }
        });
        Button btn_pay = findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = edit_amount.getText().toString().trim();
                merchantID = edit_merchant.getText().toString().trim();
                if (amountStr.length()*merchantID.length() == 0) {
                    Utils.showAlert(NewPaymentActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                amount = Float.valueOf(amountStr);
                uniqeID = App.getTimestampString();
                merchant_pay_ediaRequest();
            }
        });
    }
    private void merchant_pay_ediaRequest() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getResources().getString(R.string.payment_processing));
        mDialog.setCancelable(false);
        mDialog.show();
        final JSONObject object = new JSONObject();
        try {
            object.put("command", "create payment request");
            object.put("gatewayid", gatewayid);
            object.put("merchantid", merchantID);
            object.put("service", "RezoSchool tuition payment");
            object.put("amount", amount);
            object.put("currency", "XOF");
            object.put("uniqueid", uniqeID);
            object.put("sandbox", 0);
            if (gatewayid.equals("1")) {
                object.put("mobile", txt_mobile.getText().toString().trim());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final RestClient restClient = RestClient.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = restClient.postRequest(App.ediapayUrl+"api_payment", object);
                JSONObject jsonObject = null;
                String _error = null;
                int pay_code = 0;
                try {
                    jsonObject = new JSONObject(response);
                    pay_code = jsonObject.getInt("code");

                    switch (pay_code) {
                        case 200:
                            _error = getResources().getString(R.string.payment_queued);
                            transactionid = jsonObject.getString("transactionid");
                            pay_link = jsonObject.getString("url");
                            final String finalPay_link = pay_link;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    openCompleteDialog(finalPay_link);
                                }
                            });
                            Transaction transaction = new Transaction("", Utils.mUser.getUid(), sel_student.uid, sel_student.school_id, String.valueOf(amount), method, transactionid, merchantID, uniqeID, pay_link, String.valueOf(pay_code), Utils.getCurrentDateString());
                            Utils.mDatabase.child(Utils.tbl_transaction).push().setValue(transaction);
                            break;
                        case 100:
                            _error = getResources().getString(R.string.payment_success);
                            break;
                        default:
                            _error = jsonObject.getString("error");
                            final String final_error = _error;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewPaymentActivity.this, final_error, Toast.LENGTH_LONG).show();
                                }
                            });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDialog.dismiss();
            }
        }).start();
    }
    private void payment_status_ediaRequest() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getResources().getString(R.string.loading));
        mDialog.setCancelable(false);
        mDialog.show();
        final JSONObject object = new JSONObject();
        try {
            object.put("command", "get payment request status");
            object.put("merchantid", merchantID);
            object.put("uniqueid", uniqeID);
            object.put("transactionid", transactionid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final RestClient restClient = RestClient.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = restClient.postRequest(App.ediapayUrl+"api_payment", object);
                JSONObject jsonObject = null;
                String _error = null;
                int pay_code = 0;
                try {
                    jsonObject = new JSONObject(response);
                    pay_code = jsonObject.getInt("code");

                    switch (pay_code) {
                        case 200:
                            _error = getResources().getString(R.string.payment_queued);
                            transactionid = jsonObject.getString("transactionid");
                            pay_link = jsonObject.getString("url");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewPaymentActivity.this, getResources().getString(R.string.payment_queued), Toast.LENGTH_SHORT).show();
                                }
                            });

                            break;
                        case 100:
                            _error = getResources().getString(R.string.payment_success);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewPaymentActivity.this, getResources().getString(R.string.payment_success), Toast.LENGTH_SHORT).show();
                                }
                            });

                            final int finalPay_code = pay_code;
                            Utils.mDatabase.child(Utils.tbl_transaction).orderByChild("transaction_id").equalTo(transactionid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue()!=null) {
                                        for (DataSnapshot datas:dataSnapshot.getChildren()) {
                                            Transaction transaction1 = datas.getValue(Transaction.class);
                                            transaction1.status = String.valueOf(finalPay_code);
                                            Utils.mDatabase.child(Utils.tbl_transaction).child(datas.getKey()).setValue(transaction1);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            break;
                        default:
                            _error = jsonObject.getString("error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mDialog.dismiss();
            }
        }).start();
    }
    private void openCompleteDialog(final String pay_link) {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final View view = getLayoutInflater().inflate(R.layout.dialog_ediapay_link, null);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        Button btn_complete = (Button)view.findViewById(R.id.btn_complete);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.openUrl(pay_link, NewPaymentActivity.this);
                dlg.dismiss();
            }
        });
        dlg.show();
        dlg.getWindow().setLayout((int)(width*0.95f), ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    void loadData() {
        Utils.mDatabase.child(Utils.tbl_user).child(sel_student.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    User user = dataSnapshot.getValue(User.class);
                    edit_student.setText(user.name);
                    txt_mobile.setText(getResources().getString(R.string.mobile_number) + ": " + user.phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Utils.mDatabase.child(Utils.tbl_school).child(sel_student.school_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    School school = dataSnapshot.getValue(School.class);
                    String mlevel = "", mfee = "";
                    for (Class _class:school.classes) {
                        if (_class.name.equals(sel_student.class_name)) {
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
                    edit_level_name.setText(mlevel);
                    edit_level_fee.setText(mfee);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();
        //
        if (transactionid.length() > 0) {
            payment_status_ediaRequest();
        }

    }
}