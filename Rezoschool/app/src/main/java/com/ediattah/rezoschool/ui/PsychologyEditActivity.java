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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.Model.PsychologyResult;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.QA;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.QAanswerListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class PsychologyEditActivity extends AppCompatActivity {
    PsychologySection psychologySection;
//    PsychologyResult psychologyResult;
    QAanswerListAdapter qAanswerListAdapter;
    int q_index = 0;
    //    ArrayList<String> arrayAnswer = new ArrayList<>();
    TextView txt_question, txt_index;
    ListView listView;
    Button btn_back, btn_next, btn_exit;
    int score = 0;
    QA cur_qa;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psychology_edit);

        psychologySection = (PsychologySection) getIntent().getSerializableExtra("OBJECT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(psychologySection.name);

        txt_question = findViewById(R.id.txt_question);
        txt_index = findViewById(R.id.txt_index);
        listView = findViewById(R.id.listView);
        btn_back = findViewById(R.id.btn_back);
        btn_next = findViewById(R.id.btn_next);
        btn_exit = findViewById(R.id.btn_exit);


        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q_index -= 1;
                setQA(q_index);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q_index += 1;
                setQA(q_index);
            }
        });

        ImageView img_edit_question = findViewById(R.id.img_edit_question);
        img_edit_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateQuestionDialog();
            }
        });

        setQA(0);
    }
    void setQA(int index) {
        cur_qa = psychologySection.qas.get(index);
        txt_question.setText(cur_qa.question);
        qAanswerListAdapter = new QAanswerListAdapter(this, cur_qa.answers);
        qAanswerListAdapter.clickEnabled = false;
        String[] indexArr = cur_qa.corrects.split(",");
        for (String str:indexArr) {
            qAanswerListAdapter.sel_arrayList.add(cur_qa.answers.get(Integer.valueOf(str)));
        }
        listView.setAdapter(qAanswerListAdapter);
        btn_back.setEnabled(true);
        btn_next.setEnabled(true);
        btn_next.setBackground(getResources().getDrawable(R.drawable.btn_round));
        btn_back.setBackground(getResources().getDrawable(R.drawable.btn_round));
        if (index == psychologySection.qas.size()-1) {
            btn_next.setBackground(getResources().getDrawable(R.drawable.btn_round_gray));
            btn_next.setEnabled(false);
        } else if (index == 0) {
            btn_back.setBackground(getResources().getDrawable(R.drawable.btn_round_gray));
            btn_back.setEnabled(false);
        }
        txt_index.setText(index+1 + "/" + psychologySection.qas.size());
    }
    public void openUpdateQuestionDialog() {
        final Dialog dlg = new Dialog(this);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_value, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.2);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        EditText edit_value = dlg.findViewById(R.id.edit_value);
        edit_value.setText(cur_qa.question);
        Button btn_update = (Button)dlg.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = edit_value.getText().toString().trim();
                if (value.length() == 0) {
                    Utils.showAlert(PsychologyEditActivity.this, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                cur_qa.question = value;

                Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).child(psychologySection._id).child("qas").child(String.valueOf(q_index-1)).setValue(cur_qa);
                task.addOnCompleteListener(PsychologyEditActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dlg.dismiss();
                        if (!task.isSuccessful()) {
                            Utils.showAlert(PsychologyEditActivity.this, "Error", task.getException().getLocalizedMessage());
                        } else {
                            Toast.makeText(PsychologyEditActivity.this, "Question has been updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


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