package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.QA;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.PsychologyAnswerEditListAdapter;
import com.ediattah.rezoschool.adapter.PsychologyQuestionEditListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class PsychologyQuestionEditActivity extends AppCompatActivity {
    PsychologySection psychologySection;
    ListView listView;
    PsychologyQuestionEditListAdapter psychologyQuestionEditListAdapter;
    LinearLayout ly_no_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psychology_question_edit);
        psychologySection = (PsychologySection) getIntent().getSerializableExtra("OBJECT_SECTION");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(psychologySection.name);
        listView = findViewById(R.id.listView);
        if (psychologySection.qas == null) {
            psychologySection.qas = new ArrayList<>();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QA sel_qa = psychologySection.qas.get(position);
                Intent intent = new Intent(PsychologyQuestionEditActivity.this, PsychologyAnswerEditActivity.class);
                intent.putExtra("OBJECT_SECTION", psychologySection);
                intent.putExtra("OBJECT_QA", sel_qa);
                intent.putExtra("OBJECT_INDEX", position);
                startActivityForResult(intent, 0);
            }
        });
        ly_no_items = findViewById(R.id.ly_no_items);
        FloatingActionButton btn_new = findViewById(R.id.fab);
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddQuestionDialog();
            }
        });
        refreshListView();
    }
    void refreshListView() {
        psychologyQuestionEditListAdapter = new PsychologyQuestionEditListAdapter(this, psychologySection.qas);
        listView.setAdapter(psychologyQuestionEditListAdapter);
        if (psychologySection.qas.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
    }
    public void openAddQuestionDialog() {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_question, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.1);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        CheckBox checkBox = dlg.findViewById(R.id.checkbox);
        EditText edit_question = dlg.findViewById(R.id.edit_question);
        EditText edit_point = dlg.findViewById(R.id.edit_point);
        Button btn_remove = (Button)dlg.findViewById(R.id.btn_remove);
        btn_remove.setVisibility(View.GONE);
        Button btn_update = (Button)dlg.findViewById(R.id.btn_update);
        btn_update.setText("Add");
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = edit_question.getText().toString().trim();
                String point = edit_point.getText().toString().trim();
                if (value.length()*point.length() == 0) {
                    Utils.showAlert(PsychologyQuestionEditActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                QA qa = new QA(value, null, null, Integer.valueOf(point), checkBox.isChecked());
                psychologySection.qas.add(qa);
                Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).child(psychologySection._id).child("qas").setValue(psychologySection.qas);
                task.addOnCompleteListener(PsychologyQuestionEditActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dlg.dismiss();
                        if (!task.isSuccessful()) {
                            Utils.showAlert(PsychologyQuestionEditActivity.this, "Error", task.getException().getLocalizedMessage());
                        } else {
                            refreshListView();
                            Toast.makeText(PsychologyQuestionEditActivity.this, "Question has been added successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        dlg.show();
    }
    public void openUpdateQuestionDialog(QA cur_qa, int index) {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_question, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.1);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        EditText edit_question = dlg.findViewById(R.id.edit_question);
        EditText edit_point = dlg.findViewById(R.id.edit_point);
        edit_question.setText(cur_qa.question);
        edit_point.setText(String.valueOf(cur_qa.points));
        CheckBox checkBox = dlg.findViewById(R.id.checkbox);
        checkBox.setChecked(cur_qa.isSingleChoice);
        Button btn_remove = (Button)dlg.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PsychologyQuestionEditActivity.this);
                builder.setMessage("Are you going to quit this test?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        psychologySection.qas.remove(index);

                        Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).child(psychologySection._id).child("qas").setValue(psychologySection.qas);
                        task.addOnCompleteListener(PsychologyQuestionEditActivity.this, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                dlg.dismiss();
                                if (!task.isSuccessful()) {
                                    Utils.showAlert(PsychologyQuestionEditActivity.this, "Error", task.getException().getLocalizedMessage());
                                } else {
                                    refreshListView();
                                    Toast.makeText(PsychologyQuestionEditActivity.this, "Question has been removed successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        Button btn_update = (Button)dlg.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = edit_question.getText().toString().trim();
                String point = edit_point.getText().toString().trim();
                if (value.length()*point.length() == 0) {
                    Utils.showAlert(PsychologyQuestionEditActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                cur_qa.question = value;
                cur_qa.points = Integer.valueOf(point);
                cur_qa.isSingleChoice = checkBox.isChecked();
                Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).child(psychologySection._id).child("qas").child(String.valueOf(index)).setValue(cur_qa);
                task.addOnCompleteListener(PsychologyQuestionEditActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dlg.dismiss();
                        if (!task.isSuccessful()) {
                            Utils.showAlert(PsychologyQuestionEditActivity.this, "Error", task.getException().getLocalizedMessage());
                        } else {
                            psychologySection.qas.set(index, cur_qa);
                            refreshListView();
                            Toast.makeText(PsychologyQuestionEditActivity.this, "Question has been updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        dlg.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                psychologySection = (PsychologySection)data.getSerializableExtra("OBJECT_SECTION");
                refreshListView();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}