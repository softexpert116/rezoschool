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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.Level;
import com.ediattah.rezoschool.Model.Ministry;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Student;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.Model.User;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.ClassListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.ediattah.rezoschool.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Button btn_signup, btn_have_account;
    String country_code, number, country_code1, number1;
    ProgressDialog progressDialog;
    String school_number, school_type, ministry_type, school_area, phone1;
    boolean isPublic = false, isNew = false;
    MaterialSpinner spinner_area, spinner_account_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        country_code = getIntent().getExtras().getString("country_code");
        number = getIntent().getExtras().getString("number");
        getSupportActionBar().hide();
        App.hideKeyboard(this);
        spinner_area = findViewById(R.id.spinner_area);
        spinner_area.setItems(Utils.array_school_area);
        spinner_account_type = findViewById(R.id.spinner_account_type);
        spinner_account_type.setItems(Utils.array_account_type);

//        final EditText edit_username = (EditText)findViewById(R.id.edit_username_sms);
//        final EditText edit_password = (EditText)findViewById(R.id.edit_password_sms);
//        final EditText edit_senderID = (EditText)findViewById(R.id.edit_senderID_sms);
        final EditText edit_name = (EditText)findViewById(R.id.edit_name);
        final EditText edit_country = (EditText)findViewById(R.id.edit_country);
        final EditText edit_phone = (EditText)findViewById(R.id.edit_phone);
        final EditText edit_email = (EditText)findViewById(R.id.edit_email);
        final EditText edit_city = (EditText)findViewById(R.id.edit_city);
        final EditText edit_school_number = (EditText)findViewById(R.id.edit_school_number);
        final CountryCodePicker countryCodePicker = (CountryCodePicker)findViewById(R.id.txt_countryCode);
        final LinearLayout ly_school = (LinearLayout)findViewById(R.id.ly_school);
        final LinearLayout ly_school_area = (LinearLayout)findViewById(R.id.ly_school_area);
        final EditText edit_phone1 = (EditText)findViewById(R.id.edit_phone1);
        final CountryCodePicker countryCodePicker1 = (CountryCodePicker)findViewById(R.id.txt_countryCode1);
        final LinearLayout ly_student = (LinearLayout)findViewById(R.id.ly_student);
        final LinearLayout ly_student_type = (LinearLayout)findViewById(R.id.ly_student_type);
        final LinearLayout ly_ministry_type = (LinearLayout)findViewById(R.id.ly_ministry_type);
//        RadioButton radio_school = (RadioButton)findViewById(R.id.radio_school);
//        RadioButton radio_teacher = (RadioButton)findViewById(R.id.radio_teacher);
//        RadioButton radio_parent = (RadioButton)findViewById(R.id.radio_parent);
//        RadioButton radio_student = (RadioButton)findViewById(R.id.radio_student);
        RadioButton radio_old = (RadioButton)findViewById(R.id.radio_old);
        RadioButton radio_new = (RadioButton)findViewById(R.id.radio_new);
        RadioButton radio_primary = (RadioButton)findViewById(R.id.radio_primary);
        RadioButton radio_secondary = (RadioButton)findViewById(R.id.radio_secondary);
        RadioButton radio_private = (RadioButton)findViewById(R.id.radio_private);
        RadioButton radio_public = (RadioButton)findViewById(R.id.radio_public);
        RadioButton radio_supervisor = (RadioButton)findViewById(R.id.radio_supervisor);
        RadioButton radio_middle_staff = (RadioButton)findViewById(R.id.radio_middle_staff);
        RadioButton radio_filed_agent = (RadioButton)findViewById(R.id.radio_filed_agent);
        final LinearLayout ly_school_number = findViewById(R.id.ly_school_number);
        // default setting for user type and school type
//        radio_school.setChecked(true);
        radio_primary.setChecked(true);
        radio_private.setChecked(true);
        radio_old.setChecked(true);
        radio_supervisor.setChecked(true);
        Utils.currentUser.type = Utils.SCHOOL;
        school_type = Utils.PRIMARY;
        ministry_type = Utils.SUPERVISOR;

        isPublic = false;

        btn_signup = findViewById(R.id.btn_signup);
        countryCodePicker.setCountryForPhoneCode(Integer.valueOf(country_code));
        edit_phone.setText(number);
        edit_country.setText(countryCodePicker.getSelectedCountryName());

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String username_sms = edit_username.getText().toString().trim();
//                String password_sms = edit_password.getText().toString().trim();
//                String senderID_sms = edit_senderID.getText().toString().trim();
                String email = edit_email.getText().toString().trim();
                String name = edit_name.getText().toString().trim();
                school_number = edit_school_number.getText().toString().trim();
                school_area = Utils.array_school_area.get(spinner_area.getSelectedIndex());
                String city = edit_city.getText().toString().trim();
                String country = edit_country.getText().toString();
                country_code1 = countryCodePicker1.getSelectedCountryCode();
                String token = Utils.getDeviceToken(RegisterActivity.this);
                number1 = edit_phone1.getText().toString().trim();
                number1 = number1.replace(" ", "");
                number1 = number1.replace("-", "");
                String phone = country_code + number;
                phone1 = country_code1 + number1;
                if (email.length()*name.length()*city.length() == 0) {
                    Utils.showAlert(RegisterActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                if (Utils.currentUser.type.equals(Utils.SCHOOL) || Utils.currentUser.type.equals(Utils.TEACHER) || Utils.currentUser.type.equals(Utils.STUDENT)) {
                    if (school_number.length() == 0) {
                        Utils.showAlert(RegisterActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                        return;
                    }
                }
                if (Utils.currentUser.type.equals(Utils.STUDENT)) {
                    if (number1.length() == 0) {
                        Utils.showAlert(RegisterActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                        return;
                    }
                }


                User user = new User("", name, "", email, phone, country, city, Utils.currentUser.type, token, true, 0, "", "", "");
                userRegister(user);
            }
        });

        spinner_account_type.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String type = Utils.array_account_type.get(position);
                if (type.equals(Utils.SCHOOL)) {
                    ly_school.setVisibility(View.VISIBLE);
                    ly_school_number.setVisibility(View.VISIBLE);
                    ly_school_area.setVisibility(View.VISIBLE);
                    ly_student.setVisibility(View.GONE);
                    ly_student_type.setVisibility(View.GONE);
                    ly_ministry_type.setVisibility(View.GONE);
                    Utils.currentUser.type = Utils.SCHOOL;
                } else if (type.equals(Utils.TEACHER)) {
                    ly_school.setVisibility(View.GONE);
                    ly_school_number.setVisibility(View.VISIBLE);
                    ly_school_area.setVisibility(View.GONE);
                    ly_student.setVisibility(View.GONE);
                    ly_student_type.setVisibility(View.GONE);
                    ly_ministry_type.setVisibility(View.GONE);
                    Utils.currentUser.type = Utils.TEACHER;
                } else if (type.equals(Utils.PARENT)) {
                    ly_school.setVisibility(View.GONE);
                    ly_school_number.setVisibility(View.GONE);
                    ly_school_area.setVisibility(View.GONE);
                    ly_student.setVisibility(View.GONE);
                    ly_student_type.setVisibility(View.GONE);
                    ly_ministry_type.setVisibility(View.GONE);
                    Utils.currentUser.type = Utils.PARENT;
                } else if (type.equals(Utils.STUDENT)) {
                    ly_school.setVisibility(View.GONE);
                    ly_school_number.setVisibility(View.VISIBLE);
                    ly_school_area.setVisibility(View.GONE);
                    ly_student.setVisibility(View.VISIBLE);
                    ly_student_type.setVisibility(View.VISIBLE);
                    ly_ministry_type.setVisibility(View.GONE);
                    Utils.currentUser.type = Utils.STUDENT;
                } else if (type.equals(Utils.MINISTRY)) {
                    ly_school.setVisibility(View.GONE);
                    ly_school_number.setVisibility(View.GONE);
                    ly_school_area.setVisibility(View.GONE);
                    ly_student.setVisibility(View.GONE);
                    ly_student_type.setVisibility(View.GONE);
                    ly_ministry_type.setVisibility(View.VISIBLE);
                    Utils.currentUser.type = Utils.MINISTRY;
                }
            }
        });
//
//        radio_school.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ly_school.setVisibility(View.VISIBLE);
//                ly_school_number.setVisibility(View.VISIBLE);
//                ly_school_area.setVisibility(View.VISIBLE);
//                ly_student.setVisibility(View.GONE);
//                ly_student_type.setVisibility(View.GONE);
//                Utils.currentUser.type = Utils.SCHOOL;
//            }
//        });
//        radio_teacher.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ly_school.setVisibility(View.GONE);
//                ly_school_number.setVisibility(View.VISIBLE);
//                ly_school_area.setVisibility(View.GONE);
//                ly_student.setVisibility(View.GONE);
//                ly_student_type.setVisibility(View.GONE);
//                Utils.currentUser.type = Utils.TEACHER;
//            }
//        });
//        radio_parent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ly_school.setVisibility(View.GONE);
//                ly_school_number.setVisibility(View.GONE);
//                ly_school_area.setVisibility(View.GONE);
//                ly_student.setVisibility(View.GONE);
//                ly_student_type.setVisibility(View.GONE);
//                Utils.currentUser.type = Utils.PARENT;
//            }
//        });
//        radio_student.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ly_school.setVisibility(View.GONE);
//                ly_school_number.setVisibility(View.VISIBLE);
//                ly_school_area.setVisibility(View.GONE);
//                ly_student.setVisibility(View.VISIBLE);
//                ly_student_type.setVisibility(View.VISIBLE);
//                Utils.currentUser.type = Utils.STUDENT;
//            }
//        });
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
        radio_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNew = false;
            }
        });
        radio_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNew = true;
            }
        });
        radio_supervisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ministry_type = Utils.SUPERVISOR;
            }
        });
        radio_middle_staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ministry_type = Utils.MIDDLE_STAFF;
            }
        });
        radio_filed_agent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ministry_type = Utils.FILED_AGENT;
            }
        });
    }
    public void userRegister(final User user) {

        // Showing progress dialog at user registration time.
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();

        // check if exists phone in user table
        Utils.mDatabase.child(Utils.tbl_user).orderByChild(Utils.USER_PHONE).equalTo(user.phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if (dataSnapshot.getValue() != null) {
                            Utils.showAlert(RegisterActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.phone_number_already_exists));
                            return;
                        } else {
                            if (Utils.currentUser.type.equals(Utils.PARENT)) {
                                // parent register
                                register_newUser(user);
                                return;
                            }
                            if (Utils.currentUser.type.equals(Utils.MINISTRY)) {
                                // ministry register
                                register_newUser(user);
                                Ministry ministry = new Ministry("", Utils.mUser.getUid(), ministry_type);
                                Utils.mDatabase.child(Utils.tbl_ministry).push().setValue(ministry);
                                return;
                            }
                            progressDialog.show();
                            Utils.mDatabase.child(Utils.tbl_school).orderByChild(Utils.SCHOOL_NUMBER).equalTo(school_number)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            progressDialog.dismiss();
                                            if (dataSnapshot.getValue() != null) {
                                                for(DataSnapshot datas: dataSnapshot.getChildren()){
                                                    // already exist
                                                    if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
                                                        Utils.showAlert(RegisterActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.school_already_exists));
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
                                                                        register_newUser(user);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        } else if (Utils.currentUser.type.equals(Utils.STUDENT)) {
                                                            Utils.mDatabase.child(Utils.tbl_user).orderByChild(Utils.USER_PHONE).equalTo(phone1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.getValue()!=null) {
                                                                        for (DataSnapshot datas:dataSnapshot.getChildren()) {
                                                                            User parent = datas.getValue(User.class);
                                                                            if (!parent.type.equals(Utils.PARENT)) {
                                                                                Utils.showAlert(RegisterActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.invalid_parent_number));
                                                                                return;
                                                                            } else {
                                                                                openAddClassDialog(school_id, datas.getKey(), user);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        Utils.showAlert(RegisterActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.invalid_parent_number));
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                        // user register

                                                    }
                                                }
                                            } else {
                                                if (Utils.currentUser.type.equals(Utils.SCHOOL)) {
                                                    // school register
                                                    School school = new School("", Utils.mUser.getUid(), school_number, school_type, school_area, isPublic, new ArrayList<Course>(), new ArrayList<Class>(), new ArrayList<Level>(), new ArrayList<Teacher>(), new ArrayList<Student>());
                                                    Utils.mDatabase.child(Utils.tbl_school).push().setValue(school);

                                                    // school staff register
                                                    register_newUser(user);
                                                } else {
                                                    Utils.showAlert(RegisterActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.invalid_school_number));
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progressDialog.dismiss();
                                        }
                                    });

                        }

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
        Utils.mDatabase.child(Utils.tbl_user).child(Utils.mUser.getUid()).child(Utils.USER_ALLOW).setValue(true);
        // currentUser setting --------------
        Utils.currentUser = user;
        Utils.currentUser._id = Utils.mUser.getUid();
        String msg = getResources().getString(R.string.successfully_registered);
        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void openAddClassDialog(final String school_id, final String parent_id, final User user) {
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
        txt_title.setText(getResources().getString(R.string.choose_class));
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
        Utils.mDatabase.child(Utils.tbl_school).child(school_id).child("classes").addValueEventListener(new ValueEventListener() {
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
                Class sel_class = array_class.get(classAdapter.sel_index);
                Student student = new Student(Utils.mUser.getUid(), parent_id, school_id, sel_class.name, isNew, false);
                Utils.currentSchool.students.add(student);
                Utils.mDatabase.child(Utils.tbl_school).child(school_id).child("students").push().setValue(Utils.currentSchool.students);
                HashMap hashMap = new HashMap<String, String>();           //some random data
                hashMap.put("parent_id", parent_id);
                hashMap.put("student_id", Utils.mUser.getUid());
                hashMap.put("isNew", isNew);
                Utils.mDatabase.child(Utils.tbl_parent_student).push().setValue(hashMap);
                register_newUser(user);
                dlg.dismiss();
            }
        });
        dlg.show();
    }

}