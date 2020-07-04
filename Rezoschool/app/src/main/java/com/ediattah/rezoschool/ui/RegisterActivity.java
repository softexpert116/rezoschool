package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.ediattah.rezoschool.R;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    Button btn_signup, btn_have_account;
    String country_code, number;
    ProgressDialog progressDialog;
    String school_number, school_type;
    boolean isPublic = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        country_code = getIntent().getExtras().getString("country_code");
        number = getIntent().getExtras().getString("number");
        getSupportActionBar().hide();

        final EditText edit_name = (EditText)findViewById(R.id.edit_name);
        final EditText edit_country = (EditText)findViewById(R.id.edit_country);
        final EditText edit_phone = (EditText)findViewById(R.id.edit_phone);
        final EditText edit_email = (EditText)findViewById(R.id.edit_email);
        final EditText edit_city = (EditText)findViewById(R.id.edit_city);
        final EditText edit_school_number = (EditText)findViewById(R.id.edit_school_number);
        final CountryCodePicker countryCodePicker = (CountryCodePicker)findViewById(R.id.txt_countryCode);
        final LinearLayout ly_school = (LinearLayout)findViewById(R.id.ly_school);
        RadioButton radio_school = (RadioButton)findViewById(R.id.radio_school);
        RadioButton radio_teacher = (RadioButton)findViewById(R.id.radio_teacher);
        RadioButton radio_parent = (RadioButton)findViewById(R.id.radio_parent);
        RadioButton radio_primary = (RadioButton)findViewById(R.id.radio_primary);
        RadioButton radio_secondary = (RadioButton)findViewById(R.id.radio_secondary);
        RadioButton radio_private = (RadioButton)findViewById(R.id.radio_private);
        RadioButton radio_public = (RadioButton)findViewById(R.id.radio_public);
        final LinearLayout ly_school_number = findViewById(R.id.ly_school_number);
        // default setting for user type and school type
        radio_school.setChecked(true);
        radio_primary.setChecked(true);
        radio_private.setChecked(true);
        Utils.currentUser.type = Utils.SCHOOL;
        school_type = Utils.PRIMARY;
        isPublic = false;

        btn_signup = findViewById(R.id.btn_signup);
        countryCodePicker.setCountryForPhoneCode(Integer.valueOf(country_code));
        edit_phone.setText(number);
        edit_country.setText(countryCodePicker.getSelectedCountryName());

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString().trim();
                String name = edit_name.getText().toString().trim();
                school_number = edit_school_number.getText().toString().trim();
                String city = edit_city.getText().toString().trim();
                String country = edit_country.getText().toString();
                String token = Utils.getDeviceToken(RegisterActivity.this);

                String phone = country_code + number;
                if (email.length()*name.length()*city.length() == 0) {
                    Utils.showAlert(RegisterActivity.this, "Warning", "Please fill in blank fields");
                    return;
                }
                if (!Utils.currentUser.type.equals(Utils.PARENT)) {
                    if (school_number.length() == 0) {
                        Utils.showAlert(RegisterActivity.this, "Warning", "Please fill in blank fields");
                        return;
                    }
                }

                User user = new User("", name, "", email, phone, country, city, Utils.currentUser.type, token, true);
                userRegister(user);
            }
        });

        radio_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ly_school.setVisibility(View.VISIBLE);
                ly_school_number.setVisibility(View.VISIBLE);
                Utils.currentUser.type = Utils.SCHOOL;
            }
        });
        radio_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ly_school.setVisibility(View.GONE);
                ly_school_number.setVisibility(View.VISIBLE);
                Utils.currentUser.type = Utils.TEACHER;
            }
        });
        radio_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ly_school.setVisibility(View.GONE);
                ly_school_number.setVisibility(View.GONE);
                Utils.currentUser.type = Utils.PARENT;
            }
        });
        radio_primary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                school_type = Utils.PRIMARY;
            }
        });
        radio_secondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                school_type = Utils.SECONDARY;
            }
        });
        radio_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPublic = false;
            }
        });
        radio_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPublic = true;
            }
        });

    }
    public void userRegister(final User user) {

        // Showing progress dialog at user registration time.
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        // check if exists phone in user table
        Utils.mDatabase.child(Utils.tbl_user).orderByChild(Utils.USER_PHONE).equalTo(user.phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            progressDialog.dismiss();
                            Utils.showAlert(RegisterActivity.this, "Warning", "Phone number already exists!");
                            return;
                        } else {
                            if (Utils.currentUser.type.equals(Utils.PARENT)) {
                                // parent register
                                register_newUser(user);
                                return;
                            }
                            Utils.mDatabase.child(Utils.tbl_school).orderByChild(Utils.SCHOOL_NUMBER).equalTo(school_number)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            progressDialog.dismiss();
                                            if (dataSnapshot.getValue() != null) {
                                                for(DataSnapshot datas: dataSnapshot.getChildren()){
                                                    // already exist
                                                    if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
                                                        Utils.showAlert(RegisterActivity.this, "Warning", "School already exists!");
                                                    } else {
                                                        // get school_id by school_number
                                                        final String school_id = datas.getKey();
                                                        // teacher register
                                                        if (Utils.currentUser.type.equals(Utils.TEACHER)) {
                                                            Utils.mDatabase.child(Utils.tbl_school).child(school_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.getValue() != null) {
                                                                        Utils.currentSchool = dataSnapshot.getValue(School.class);
                                                                        assert Utils.currentSchool != null;
                                                                        Utils.currentSchool._id = dataSnapshot.getKey();
                                                                        Teacher teacher = new Teacher(Utils.mUser.getUid(), "");
                                                                        Utils.currentSchool.teachers.add(teacher);
                                                                        Utils.mDatabase.child(Utils.tbl_school).child(school_id).child("teachers").setValue(Utils.currentSchool.teachers);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                        // user register
                                                        register_newUser(user);
                                                    }
                                                }
                                            } else {
                                                if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
                                                    // school register
                                                    School school = new School("", Utils.mUser.getUid(), school_number, school_type, isPublic, new ArrayList<Course>(), new ArrayList<Class>(), new ArrayList<Teacher>(), new ArrayList<Student>());
                                                    Utils.mDatabase.child(Utils.tbl_school).push().setValue(school);

                                                    // school staff register
                                                    register_newUser(user);
                                                } else {
                                                    Utils.showAlert(RegisterActivity.this, "Warning", "Invalid school number!");
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progressDialog.dismiss();
                                        }
                                    });

                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        progressDialog.dismiss();
                        Log.w( "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });
    }
    void register_newUser(User user) {
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_NAME).setValue(user.name);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_PHONE).setValue(user.phone);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_EMAIL).setValue(user.email);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_COUNTRY).setValue(user.country);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_CITY).setValue(user.city);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_TYPE).setValue(Utils.currentUser.type);
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_TOKEN).setValue(user.token);
        // currentUser setting --------------
        Utils.currentUser = user;
        Utils.currentUser._id = Utils.mUser.getUid();
        String msg = "Successfully registered! You can login after you are allowed by School staff.";
        if (Utils.currentUser.type.equals(Utils.SCHOOL)||Utils.currentUser.type.equals(Utils.PARENT)) {
            msg = "Successfully registered! You can login after you are allowed by Admin.";
        }
        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}