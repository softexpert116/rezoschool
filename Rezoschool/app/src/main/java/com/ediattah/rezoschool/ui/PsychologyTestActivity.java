package com.ediattah.rezoschool.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.PsychologyResult;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.Model.PsychologySubmit;
import com.ediattah.rezoschool.Model.QA;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.QAanswerListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class PsychologyTestActivity extends AppCompatActivity {
    PsychologySection psychologySection;
    PsychologyResult psychologyResult;
    QAanswerListAdapter qAanswerListAdapter;
    int q_index = 0;
//    ArrayList<String> arrayAnswer = new ArrayList<>();
    TextView txt_question, txt_index, txt_singleChoice;
    ListView listView;
    Button btn_next, btn_exit;
    int score = 0;
    QA cur_qa;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psychology_test);
        psychologySection = (PsychologySection) getIntent().getSerializableExtra("OBJECT");
        psychologyResult = (PsychologyResult) getIntent().getSerializableExtra("RESULT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(psychologySection.name);

        txt_question = findViewById(R.id.txt_question);
        txt_singleChoice = findViewById(R.id.txt_singleChoice);
        txt_index = findViewById(R.id.txt_index);
        listView = findViewById(R.id.listView);
        btn_next = findViewById(R.id.btn_next);
        btn_exit = findViewById(R.id.btn_exit);


        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getScore()) return;
                if (q_index == psychologySection.qas.size()-1) {
                    submitResultScore();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PsychologyTestActivity.this);
                    builder.setMessage("Are you going to quit this test?");
                    builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            submitResultScore();
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getScore()) return;
                q_index += 1;
                setQA(q_index);
            }
        });

        setQA(0);
        App.getPsychologySubmit();
    }

    boolean getScore() {
//        if (qAanswerListAdapter.sel_arrayList.size() == 0) {
//            Utils.showAlert(PsychologyTestActivity.this, getResources().getString(R.string.warning), "Please choose answer(s)");
//            return false;
//        }

        ArrayList<String> arrayCorrects = new ArrayList<>(Arrays.asList(cur_qa.corrects.split(",")));
        if (arrayCorrects.contains("")) {
            arrayCorrects.remove("");
        }
        for (String intStr:qAanswerListAdapter.sel_arrayList) {
            arrayCorrects.remove(intStr);
        }
        if (arrayCorrects.size() == 0) {
            score += cur_qa.points;
        }
        return true;
    }

    void setQA(int index) {
        cur_qa = psychologySection.qas.get(index);
        txt_question.setText(cur_qa.question);
        if (cur_qa.isSingleChoice) {
            txt_singleChoice.setVisibility(View.VISIBLE);
        } else {
            txt_singleChoice.setVisibility(View.GONE);
        }
        qAanswerListAdapter = new QAanswerListAdapter(this, cur_qa.answers);
        qAanswerListAdapter.isSingleChoice = cur_qa.isSingleChoice;
        listView.setAdapter(qAanswerListAdapter);
        if (index == psychologySection.qas.size()-1) {
            btn_next.setBackground(getResources().getDrawable(R.drawable.btn_round_gray));
            btn_next.setEnabled(false);
        }
        txt_index.setText(index+1 + "/" + psychologySection.qas.size());
    }

    void submitResultScore() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting result...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        psychologyResult.score = score;
        Utils.mDatabase.child(Utils.tbl_psychology_result).orderByChild("uid").equalTo(Utils.currentUser._id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                boolean flag = false;
                progressDialog.dismiss();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        PsychologyResult result = datas.getValue(PsychologyResult.class);
                        result._id = datas.getKey();
                        if (result.school_id.equals(psychologyResult.school_id) && result.section_id.equals(psychologyResult.section_id)) {
                            Utils.mDatabase.child(Utils.tbl_psychology_result).child(result._id).setValue(psychologyResult);
                            flag = true;
                        }
                    }
                }
                if (!flag) {
                    Utils.mDatabase.child(Utils.tbl_psychology_result).push().setValue(psychologyResult);
                }
                if (Utils.currentPsychologySubmit._id.length() > 0) {
                    Utils.mDatabase.child(Utils.tbl_psychology_submit).child(Utils.currentPsychologySubmit._id).setValue(null);
                    Utils.currentPsychologySubmit = new PsychologySubmit();
                    App.setPreference(App.NewTest, "");
                }
                Intent resultIntent = new Intent();
                resultIntent.putExtra("score", score);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you going to quit this test?");
        builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
//                onBackPressed();
                submitResultScore();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }
}