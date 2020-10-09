package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ClassListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.adapter.SchoolListAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class NewStudentActivity extends AppCompatActivity {
    EditText edit_school, edit_class;
    School sel_school = new School();
    Class sel_class = new Class();
    boolean isNew = true;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.add_new_student));
        App.hideKeyboard(this);
        final CountryCodePicker countryCodePicker = (CountryCodePicker)findViewById(R.id.txt_countryCode);
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
//                edit_country.setText(countryCodePicker.getSelectedCountryName());
            }
        });
        final ListView listView = (ListView)findViewById(R.id.listView);
        edit_school = (EditText)findViewById(R.id.edit_school);
        final EditText edit_name = findViewById(R.id.edit_name);
        final EditText edit_email = findViewById(R.id.edit_email);
        final EditText edit_number = findViewById(R.id.edit_number);
        RadioButton radio_new = (RadioButton)findViewById(R.id.radio_new);
        RadioButton radio_old = (RadioButton)findViewById(R.id.radio_old);
        radio_new.setChecked(true);
        radio_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNew = true;
            }
        });
        radio_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNew = false;
            }
        });

        edit_school = findViewById(R.id.edit_school);
        edit_class = findViewById(R.id.edit_class);
        edit_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddSchoolDialog();
            }
        });
        edit_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_school.getText().toString().trim().length() == 0) {
                    Utils.showAlert(NewStudentActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_add_school_number));
                    return;
                }
                openAddClassDialog();
            }
        });
        Button btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edit_name.getText().toString().trim();
                String email = edit_email.getText().toString().trim();
                String number = edit_number.getText().toString().trim();
                if (name.length()*email.length()*number.length() == 0) {
                    Utils.showAlert(NewStudentActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                if (sel_school.number.length()*sel_class.name.length() == 0) {
                    Utils.showAlert(NewStudentActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                final CountryCodePicker countryCodePicker = (CountryCodePicker)findViewById(R.id.txt_countryCode);
                String country_code = countryCodePicker.getSelectedCountryCode();
                final String phone = country_code + number;

                progressDialog = new ProgressDialog(NewStudentActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.show();
                // phone number check by sms

                // school number check


                Utils.mDatabase.child(Utils.tbl_user).orderByChild(Utils.USER_PHONE).equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            progressDialog.dismiss();
                            Utils.showAlert(NewStudentActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.phone_number_already_exists));
                            return;
                        } else {
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword("qq@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String uid = task.getResult().getUser().getUid();
                                        String uid1 = Utils.mUser.getUid();
                                    } else {
                                    }

                                }
                            });
//                            sendSMS(phone);
//
//                            Student student = new Student(Utils.mUser.getUid(), sel_class.name, isNew);
//                            sel_school.students.add(student);
//                            Utils.mDatabase.child(Utils.tbl_school).child(sel_school._id).child("students").setValue(sel_school.students);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();

                    }
                });


            }
        });
        func();
    }
    void func() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword("qq@gmail.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();
                    String uid1 = Utils.mUser.getUid();
                    String dd = uid1;
                } else {
                }

            }
        });
    }
    private void sendSMS(final String mobileNumber) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                progressDialog.dismiss();
                Log.d("msg", "onVerificationCompleted");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Log.d("msg", e.getLocalizedMessage());
                Utils.showAlert(NewStudentActivity.this, "Warning", e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "SMS Code has been sent to the phone.", Toast.LENGTH_LONG).show();
                openOTPDialog(verificationID);
//                Intent intent = new Intent(NewStudentActivity.this, OTPActivity.class);
//                intent.putExtra("country_code", country_code);
//                intent.putExtra("number", number);
//                intent.putExtra("verificationID", verificationID);
//                LoginActivity.this.startActivity(intent);
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+" + mobileNumber, 60, TimeUnit.SECONDS, this, mCallback
        );
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Please Wait...");
//        progressDialog.show();
    }
    public void openOTPDialog(final String verificationID) {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        View view = getLayoutInflater().inflate(R.layout.dialog_otp, null);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        final EditText edit_code = dlg.findViewById(R.id.edit_code);
        final Button btnOTP = (Button)dlg.findViewById(R.id.btnOTP);
        btnOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input_code = edit_code.getText().toString();
                if (input_code.length() == 0 || input_code.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please input the security number", Toast.LENGTH_LONG).show();

                } else if (input_code.length() < 6) {

                    Toast.makeText(getApplicationContext(), "Please match length of security number", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, input_code);
                        // sign in with child phone
                        FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser sUser = FirebaseAuth.getInstance().getCurrentUser();
                                            String uid = sUser.getUid();
                                            FirebaseAuth.getInstance().signOut();

                                        } else {
                                            progressDialog.dismiss();
//                            Utils.showAlert(OTPActivity.this, "Warning", task.getException().getMessage());
                                            Toast.makeText(NewStudentActivity.this, "OTP verification failed! Please try again..", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
//                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        dlg.show();
    }

    public void openAddSchoolDialog() {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_item, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText("Choose School");
        ListView listView = dlg.findViewById(R.id.listView);
        final ArrayList<School> array_all_school = new ArrayList<>();
        final SchoolListAdapter schoolAdapter = new SchoolListAdapter(this, array_all_school);
        schoolAdapter.flag_view = true;
        listView.setAdapter(schoolAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                schoolAdapter.sel_index = i;
                schoolAdapter.notifyDataSetChanged();
            }
        });
        final Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        Utils.mDatabase.child(Utils.tbl_school).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    array_all_school.clear();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        School school = datas.getValue(School.class);
                        school._id = datas.getKey();
                        array_all_school.add(school);
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        schoolAdapter.arrayList = array_all_school;
                        schoolAdapter.notifyDataSetChanged();
                        if (array_all_school.size() == 0) {
                            btn_choose.setEnabled(false);
                        } else {
                            btn_choose.setEnabled(true);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sel_school = array_all_school.get(schoolAdapter.sel_index);
                edit_school.setText(sel_school.number);
                dlg.dismiss();
            }
        });
        dlg.show();
    }
    public void openAddClassDialog() {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_item, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        TextView txt_title = dlg.findViewById(R.id.txt_title);
        txt_title.setText("Choose Class");
        ListView listView = dlg.findViewById(R.id.listView);
        final ArrayList<Class> array_class = new ArrayList<>();
        final ClassListAdapter classAdapter = new ClassListAdapter(this, array_class);
        classAdapter.sel_index = 0;
        listView.setAdapter(classAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                classAdapter.sel_index = i;
                classAdapter.notifyDataSetChanged();
            }
        });
        final Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        Utils.mDatabase.child(Utils.tbl_school).child(sel_school._id).child("classes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    array_class.clear();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        Class aClass = datas.getValue(Class.class);
                        array_class.add(aClass);
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        classAdapter.arrayList = array_class;
                        classAdapter.notifyDataSetChanged();
                        if (array_class.size() == 0) {
                            btn_choose.setEnabled(false);
                        } else {
                            btn_choose.setEnabled(true);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sel_class = array_class.get(classAdapter.sel_index);
                edit_class.setText(sel_class.name);
                dlg.dismiss();
            }
        });
        dlg.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}