package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.QA;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.PsychologyAnswerEditListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class PsychologyAnswerEditActivity extends AppCompatActivity {
    PsychologySection psychologySection;
    QA sel_qa;
    int qa_index = 0;
    PsychologyAnswerEditListAdapter psychologyAnswerEditListAdapter;
    ListView listView;
    LinearLayout ly_no_items;
    TextView txt_no_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psychology_answer_edit);
        psychologySection = (PsychologySection) getIntent().getSerializableExtra("OBJECT_SECTION");
        sel_qa = (QA) getIntent().getSerializableExtra("OBJECT_QA");
        qa_index = getIntent().getIntExtra("OBJECT_INDEX", 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(qa_index+1+". "+sel_qa.question);
        txt_no_answer = findViewById(R.id.txt_no_answer);
        TextView txt_singleChoice = findViewById(R.id.txt_singleChoice);
        if (sel_qa.isSingleChoice) {
            txt_singleChoice.setVisibility(View.VISIBLE);
        } else {
            txt_singleChoice.setVisibility(View.GONE);
        }

        listView = findViewById(R.id.listView);
        if (sel_qa.answers == null) {
            sel_qa.answers = new ArrayList<>();
        }
        if (sel_qa.corrects == null) {
            sel_qa.corrects = "";
        }

        ly_no_items = findViewById(R.id.ly_no_items);

        FloatingActionButton btn_new = findViewById(R.id.fab);
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddAnswerDialog();
            }
        });
        refreshListView();
    }
    void refreshListView() {
        if (sel_qa.corrects.startsWith(",")) {
            sel_qa.corrects = sel_qa.corrects.replaceFirst(",", "");
        }
        if (sel_qa.isSingleChoice) {
            ArrayList<String> indexArr = new ArrayList<>(Arrays.asList(sel_qa.corrects.split(",")));
            if (indexArr.size() > 0) {
                sel_qa.corrects = indexArr.get(0);
                Utils.mDatabase.child(Utils.tbl_psychology_section).child(psychologySection._id).child("qas").child(String.valueOf(qa_index)).setValue(sel_qa);
            }
        }
        if (sel_qa.corrects == "") {
            txt_no_answer.setVisibility(View.VISIBLE);
        } else {
            txt_no_answer.setVisibility(View.GONE);
        }
        psychologyAnswerEditListAdapter = new PsychologyAnswerEditListAdapter(this, sel_qa.answers, sel_qa.corrects);
        listView.setAdapter(psychologyAnswerEditListAdapter);
        if (sel_qa.answers.size() == 0) {
            ly_no_items.setVisibility(View.VISIBLE);
        } else {
            ly_no_items.setVisibility(View.GONE);
        }
    }
    public void openAddAnswerDialog() {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_answer, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.2);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        EditText edit_value = dlg.findViewById(R.id.edit_value);
        edit_value.setHint("input answer");
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        Button btn_remove = (Button)dlg.findViewById(R.id.btn_remove);
        btn_remove.setVisibility(View.GONE);
        Button btn_update = (Button)dlg.findViewById(R.id.btn_update);
        btn_update.setText("Add");
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = edit_value.getText().toString().trim();
                if (value.length() == 0) {
                    Utils.showAlert(PsychologyAnswerEditActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                sel_qa.answers.add(value);
                ArrayList<String> indexArr = new ArrayList<>(Arrays.asList(sel_qa.corrects.split(",")));
                if (checkBox.isChecked()) {
                    String indexStr = String.valueOf(sel_qa.answers.size()-1);
                    if (!indexArr.contains(indexStr)) {
                        indexArr.add(indexStr);
                    } else {
                        indexArr.remove(indexStr);
                    }
                }
                sel_qa.corrects = TextUtils.join(",", indexArr);
                if (sel_qa.corrects.startsWith(",")) {
                    sel_qa.corrects = sel_qa.corrects.replaceFirst(",", "");
                }

                Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).child(psychologySection._id).child("qas").child(String.valueOf(qa_index)).setValue(sel_qa);
                task.addOnCompleteListener(PsychologyAnswerEditActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dlg.dismiss();
                        if (!task.isSuccessful()) {
                            Utils.showAlert(PsychologyAnswerEditActivity.this, "Error", task.getException().getLocalizedMessage());
                        } else {
                            psychologySection.qas.set(qa_index, sel_qa);
                            refreshListView();
                            Toast.makeText(PsychologyAnswerEditActivity.this, "Answer has been added successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        dlg.show();
    }
    public void openUpdateAnswerDialog(String answer, int index, boolean isChecked) {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_answer, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.2);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        EditText edit_value = dlg.findViewById(R.id.edit_value);
        edit_value.setText(answer);
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        checkBox.setChecked(isChecked);
        Button btn_remove = (Button)dlg.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PsychologyAnswerEditActivity.this);
                builder.setMessage("Are you going to quit this test?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        sel_qa.answers.remove(index);
                        String indexStr = String.valueOf(index);
                        ArrayList<String> indexArr = new ArrayList<>(Arrays.asList(sel_qa.corrects.split(",")));
                        indexArr.remove(indexStr);
                        sel_qa.corrects = TextUtils.join(",", indexArr);
                        Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).child(psychologySection._id).child("qas").child(String.valueOf(qa_index)).setValue(sel_qa);
                        task.addOnCompleteListener(PsychologyAnswerEditActivity.this, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                dlg.dismiss();
                                if (!task.isSuccessful()) {
                                    Utils.showAlert(PsychologyAnswerEditActivity.this, "Error", task.getException().getLocalizedMessage());
                                } else {
                                    psychologySection.qas.set(qa_index, sel_qa);
                                    refreshListView();
                                    Toast.makeText(PsychologyAnswerEditActivity.this, "Answer has been removed successfully", Toast.LENGTH_SHORT).show();
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
                String value = edit_value.getText().toString().trim();
                if (value.length() == 0) {
                    Utils.showAlert(PsychologyAnswerEditActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                sel_qa.answers.set(index, value);
                String indexStr = String.valueOf(index);
                ArrayList<String> indexArr = new ArrayList<>(Arrays.asList(sel_qa.corrects.split(",")));
                if (checkBox.isChecked()) {
                    if (!indexArr.contains(indexStr)) {
                        indexArr.add(indexStr);
                    }
                } else {
                    indexArr.remove(indexStr);
                }
                sel_qa.corrects = TextUtils.join(",", indexArr);

                Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).child(psychologySection._id).child("qas").child(String.valueOf(qa_index)).setValue(sel_qa);
                task.addOnCompleteListener(PsychologyAnswerEditActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dlg.dismiss();
                        if (!task.isSuccessful()) {
                            Utils.showAlert(PsychologyAnswerEditActivity.this, "Error", task.getException().getLocalizedMessage());
                        } else {
                            psychologySection.qas.set(qa_index, sel_qa);
                            refreshListView();
                            Toast.makeText(PsychologyAnswerEditActivity.this, "Answer has been updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        dlg.show();
    }
    @Override
    public boolean onSupportNavigateUp() {
//        onBackPressed();
        finishWithResult();
        return true;
    }
    void finishWithResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("OBJECT_SECTION", psychologySection);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}