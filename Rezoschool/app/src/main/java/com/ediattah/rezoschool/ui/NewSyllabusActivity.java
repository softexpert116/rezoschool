package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.Class;
import com.ediattah.rezoschool.Model.Course;
import com.ediattah.rezoschool.Model.CourseTime;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Syllabus;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.SchoolCourseListAdapter;
import com.ediattah.rezoschool.adapter.SchoolListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class NewSyllabusActivity extends AppCompatActivity {
    EditText edit_school, edit_course, edit_date;
    Date sel_date;
    School sel_school;
    LinearLayout ly_times;
    CourseTime sel_courseTime;
    Dialog dlgCourse;
    EditText edit_title, edit_thematic, edit_summary, edit_title_next, edit_thematic_next, edit_attach, edit_comment;
    int REQUEST_CODE_DOC = 200;
    String filePath = "";
    Uri fileuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_syllabus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create New Syllabus");
        App.hideKeyboard(this);
        sel_date = (Date)getIntent().getSerializableExtra("Date");
        ly_times = findViewById(R.id.ly_times);
        edit_school = (EditText)findViewById(R.id.edit_school);
        edit_course = (EditText)findViewById(R.id.edit_course);
        edit_date = (EditText)findViewById(R.id.edit_date);
        edit_title = (EditText)findViewById(R.id.edit_title);
        edit_thematic = (EditText)findViewById(R.id.edit_thematic);
        edit_summary = (EditText)findViewById(R.id.edit_summary);
        edit_title_next = (EditText)findViewById(R.id.edit_title_next);
        edit_thematic_next = (EditText)findViewById(R.id.edit_thematic_next);
        edit_attach = findViewById(R.id.edit_attachment);
        edit_comment = findViewById(R.id.edit_comment);
        edit_date.setText(new SimpleDateFormat(App.DATE_FORMAT).format(sel_date));
        edit_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddSchoolDialog();
            }
        });
        edit_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sel_school == null) {
                    Utils.showAlert(NewSyllabusActivity.this, "Warning", "Please select school number.");
                    return;
                }
                openAddCourseDialog();
            }
        });
        edit_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar myCalender = Calendar.getInstance();
                int year = myCalender.get(Calendar.YEAR);
                int month = myCalender.get(Calendar.MONTH);
                int date = myCalender.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        if (datePicker.isShown()) {
                            myCalender.set(Calendar.YEAR, i);
                            myCalender.set(Calendar.MONTH, i1);
                            myCalender.set(Calendar.DAY_OF_MONTH, i2);
                            sel_date = myCalender.getTime();
                            edit_date.setText(new SimpleDateFormat(App.DATE_FORMAT).format(sel_date));
                        }
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewSyllabusActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myDateListener, year, month, date);
                datePickerDialog.setTitle("Choose date:");
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();
            }
        });
        edit_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
                startActivityForResult(intent, REQUEST_CODE_DOC);
            }
        });
        Button btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String course = edit_course.getText().toString().trim();
                if (course.length() == 0) {
                    Utils.showAlert(NewSyllabusActivity.this, "Warning", "Please fill in the blank field");
                    return;
                }
                if (sel_date == null || sel_school == null) {
                    Utils.showAlert(NewSyllabusActivity.this, "Warning", "Please fill in the blank field");
                    return;
                }
                String dayOfTheWeek = (String) DateFormat.format("EEEE", sel_date);
                if (!dayOfTheWeek.equals(Utils.getDayStrFromInt(sel_courseTime.dayOfWeek))) {
                    Utils.showAlert(NewSyllabusActivity.this, "Warning", "Date doesn't match with the course timeslot.\nPlease try other one.");
                    return;
                }
                String title = edit_title.getText().toString().trim();
                String thematic = edit_thematic.getText().toString().trim();
                String summary = edit_summary.getText().toString().trim();
                String title_next = edit_title_next.getText().toString().trim();
                String thematic_next = edit_thematic_next.getText().toString().trim();
                String attach = edit_attach.getText().toString().trim();
                String comment = edit_comment.getText().toString().trim();
                final Syllabus syllabus = new Syllabus("", sel_date, sel_courseTime, course, sel_school._id, Utils.mUser.getUid(), title, thematic, summary, title_next,
                        thematic_next, attach, comment);
                Utils.mDatabase.child(Utils.tbl_syllabus).orderByChild("school_id").equalTo(syllabus.school_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            for (DataSnapshot datas:dataSnapshot.getChildren()) {
                                Syllabus syllabus1 = datas.getValue(Syllabus.class);
                                String date1 = Utils.getDateString(syllabus1.date);
                                String date = Utils.getDateString(syllabus.date);
                                if (date.equals(date1) && syllabus.time.start_time.equals(syllabus1.time.start_time) &&
                                        syllabus.time.end_time.equals(syllabus1.time.end_time) && syllabus.time.dayOfWeek == syllabus1.time.dayOfWeek) {
                                    Utils.showAlert(NewSyllabusActivity.this, "Warning", "A syllabus already set in the same timeslot.");
                                    return;
                                }
                            }
                        }
                        if (fileuri!=null) {
                            final ProgressDialog progressDialog = new ProgressDialog(NewSyllabusActivity.this);
                            progressDialog.setMessage("Please Wait...");
                            progressDialog.show();
                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setContentType("application/pdf")
                                    .build();
                            Long tsLong = System.currentTimeMillis();
                            String ts = tsLong.toString();
                            final StorageReference file_refer = Utils.mStorage.child("syllabus/"+ts);

                            file_refer.putFile(fileuri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Handle successful uploads on complete
                                    file_refer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            progressDialog.dismiss();
                                            String downloadUrl = uri.toString();
                                            syllabus.attach = downloadUrl;
                                            submitSyllabus(syllabus);
                                        }
                                    });
                                }

                            });
                        } else {
                            submitSyllabus(syllabus);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    void submitSyllabus(Syllabus syllabus) {
        Utils.mDatabase.child(Utils.tbl_syllabus).push().setValue(syllabus);
        Toast.makeText(NewSyllabusActivity.this, "Successfully created!", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int req, int result, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(req, result, data);
        if (result == RESULT_OK)
        {
            fileuri = data.getData();
            filePath = getFileNameByUri(this, fileuri);
            int index = filePath.lastIndexOf("/");
            String fileName = filePath.substring(index+1);
            edit_attach.setText(fileName);
        }
    }

    private String getFileNameByUri(Context context, Uri uri) {
        String filepath = "";//default fileName
        //Uri filePathUri = uri;
        File file;
        if (uri.getScheme().toString().compareTo("content") == 0)
        {
            Cursor cursor = context.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION }, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            String mImagePath = cursor.getString(column_index);
            cursor.close();
            filepath = mImagePath;

        }
        else
        if (uri.getScheme().compareTo("file") == 0)
        {
            try
            {
                file = new File(new URI(uri.toString()));
                if (file.exists())
                    filepath = file.getAbsolutePath();

            }
            catch (URISyntaxException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            filepath = uri.getPath();
        }
        return filepath;
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
        final LinearLayout ly_no_items = dlg.findViewById(R.id.ly_no_items);
        txt_title.setText("Choose School");
        ListView listView = dlg.findViewById(R.id.listView);
        final ArrayList<School> array_all_school = new ArrayList<>();
        final SchoolListAdapter schoolAdapter = new SchoolListAdapter(this, array_all_school);
        schoolAdapter.flag_view = true;
        schoolAdapter.sel_index = -1;
        listView.setAdapter(schoolAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sel_school = array_all_school.get(i);
                edit_school.setText(sel_school.number);
                dlg.dismiss();
            }
        });
        final Button btn_choose = (Button)dlg.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);
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
                            ly_no_items.setVisibility(View.VISIBLE);
                        } else {
                            ly_no_items.setVisibility(View.GONE);
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
//                sel_school = array_all_school.get(schoolAdapter.sel_index);
//                edit_school.setText(sel_school.number);
//                dlg.dismiss();
            }
        });
        dlg.show();
    }

    public void openAddCourseDialog() {
        dlgCourse = new Dialog(this);
        Window window = dlgCourse.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_choose_item, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.4);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlgCourse.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgCourse.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlgCourse.show();
        final LinearLayout ly_no_items = dlgCourse.findViewById(R.id.ly_no_items);
        TextView txt_title = dlgCourse.findViewById(R.id.txt_title);
        txt_title.setText("Choose Course Timeslot");
        ListView listView = dlgCourse.findViewById(R.id.listView);
        final ArrayList<Course> array_course_sel = new ArrayList<>();
        ArrayList<Course> arrayList = new ArrayList<>();
        for (Class _class:sel_school.classes) {
            for (Course _course:_class.courses) {
                arrayList.add(_course);
            }
        }
        final SchoolCourseListAdapter courseAdapter = new SchoolCourseListAdapter(this, arrayList, array_course_sel);
        courseAdapter.flag_syllabus = true;
        listView.setAdapter(courseAdapter);
        if (arrayList.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }

        final Button btn_choose = (Button)dlgCourse.findViewById(R.id.btn_choose);
        btn_choose.setVisibility(View.GONE);

        dlgCourse.show();
    }
    public void refresh_courseTime(String courseName, CourseTime courseTime) {
        sel_courseTime = courseTime;
        if (sel_courseTime != null) {
            edit_course.setText(courseName);
            LayoutInflater inflater = LayoutInflater.from(NewSyllabusActivity.this);
            View view1 = inflater.inflate(R.layout.cell_timeslot, null);
            LinearLayout ly1 = view1.findViewById(R.id.ly1);
            ly1.setBackground(NewSyllabusActivity.this.getResources().getDrawable(R.drawable.frame_round_dark));
            LinearLayout ly2 = view1.findViewById(R.id.ly2);
            ly2.setVisibility(View.INVISIBLE);
            TextView txt_day = view1.findViewById(R.id.txt_day1);
            TextView txt_time = view1.findViewById(R.id.txt_time1);
            txt_time.setTextColor(getResources().getColor(R.color.white));
            txt_day.setText(Utils.getDayStrFromInt(sel_courseTime.dayOfWeek));
            txt_time.setText(sel_courseTime.start_time + " ~ " + sel_courseTime.end_time);
            ly_times.removeAllViews();
            ly_times.addView(view1);
        }
        dlgCourse.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}