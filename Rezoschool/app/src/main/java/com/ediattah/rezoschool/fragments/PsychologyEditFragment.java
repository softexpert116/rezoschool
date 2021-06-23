package com.ediattah.rezoschool.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ediattah.rezoschool.App;
import com.ediattah.rezoschool.Model.PsychologyResult;
import com.ediattah.rezoschool.Model.PsychologySection;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.adapter.PsychologySectionEditListAdapter;
import com.ediattah.rezoschool.adapter.PsychologySectionListAdapter;
import com.ediattah.rezoschool.ui.MainActivity;
import com.ediattah.rezoschool.ui.PsychologyEditActivity;
import com.ediattah.rezoschool.ui.PsychologyQuestionEditActivity;
import com.ediattah.rezoschool.ui.PsychologyTestActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PsychologyEditFragment extends Fragment {
    MainActivity activity;
    ArrayList<PsychologySection> arrayList = new ArrayList<>();
    PsychologySectionEditListAdapter psychologySectionListAdapter;
    LinearLayout ly_no_items;

    ListView listView;
    PsychologySection sel_section;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_psychology_edit, container, false);
        psychologySectionListAdapter = new PsychologySectionEditListAdapter(this, arrayList);
        listView = v.findViewById(R.id.listView);
        ly_no_items = v.findViewById(R.id.ly_no_items);
        FloatingActionButton btn_new = v.findViewById(R.id.fab);
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddSectionDialog();
            }
        });
        listView.setAdapter(psychologySectionListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                psychologySectionListAdapter.sel_index = position;
//                psychologySectionListAdapter.notifyDataSetChanged();
                sel_section = arrayList.get(position);
                Intent intent = new Intent(activity, PsychologyQuestionEditActivity.class);
                intent.putExtra("OBJECT_SECTION", sel_section);
                startActivityForResult(intent, 0);
            }
        });
        loadPsychologySections();
//        ArrayList<String> answers = new ArrayList<>();
//        answers.add("Oui, j'aime ça");
//        answers.add("J'aime ça plus ou moins");
//        answers.add("Ça m'est égal");
//        answers.add("Je n'aime pas ça");
//        answers.add("J'en ai marre du lycée");
//        QA qa = new QA("D'une façon générale, est-ce que tu aimes ou non aller au lycée ?", answers, "0,1", 2);
//        ArrayList<QA> qas = new ArrayList<>();
//        qas.add(qa);
//        answers = new ArrayList<>();
//        answers.add("À l'occasion de réunions parents-professeurs");
//        answers.add("En prenant rendez-vous avec un professeur");
//        answers.add("S'ils sont convoqués");
//        answers.add("Rarement parce qu'ils n'ont pas le temps");
//        answers.add("Rarement parce qu'ils n'aiment pas venir au lycée");
//        qa = new QA("Tes parents rencontrent-ils les professeurs :", answers, "2", 1);
//        qas.add(qa);
//        PsychologySection psychologySection = new PsychologySection("", "section1", qas);
//        Utils.mDatabase.child(Utils.tbl_psychology_section).push().setValue(psychologySection);
        return v;
    }
    void loadPsychologySections() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Utils.mDatabase.child(Utils.tbl_psychology_section).orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.getValue() != null) {
                    arrayList.clear();
                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                        PsychologySection psychologySection = datas.getValue(PsychologySection.class);
                        psychologySection._id = datas.getKey();
                        arrayList.add(psychologySection);
                    }
                    sel_section = arrayList.get(0);
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (arrayList.size() == 0) {
                            ly_no_items.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        } else {
                            ly_no_items.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                            psychologySectionListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }
    void openAddSectionDialog() {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_value, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.1);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        EditText edit_name = dlg.findViewById(R.id.edit_name);
        EditText edit_description = dlg.findViewById(R.id.edit_description);
        EditText edit_score = dlg.findViewById(R.id.edit_score);
        Button btn_remove = (Button)dlg.findViewById(R.id.btn_remove);
        btn_remove.setVisibility(View.GONE);
        Button btn_update = (Button)dlg.findViewById(R.id.btn_update);
        btn_update.setText("Add");
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sectionName = edit_name.getText().toString().trim();
                String sectionDescription = edit_description.getText().toString().trim();
                String sectionScore = edit_score.getText().toString().trim();
                if (sectionName.length()*sectionDescription.length()*sectionScore.length() == 0) {
                    Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                PsychologySection section = new PsychologySection("", sectionName, sectionDescription, Integer.valueOf(sectionScore), null);
                Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).push().setValue(section);
                task.addOnCompleteListener(activity, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dlg.dismiss();
                        if (!task.isSuccessful()) {
                            Utils.showAlert(activity, "Error", task.getException().getLocalizedMessage());
                        } else {
                            Toast.makeText(activity, "Section name has been added successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        dlg.show();
    }
    public void openUpdateSectionNameDialog(PsychologySection section) {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        View view = getLayoutInflater().inflate(R.layout.dialog_update_value, null);
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.80);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.1);
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        dlg.show();
        EditText edit_name = dlg.findViewById(R.id.edit_name);
        EditText edit_description = dlg.findViewById(R.id.edit_description);
        EditText edit_score = dlg.findViewById(R.id.edit_score);
        edit_name.setText(section.name);
        edit_description.setText(section.description);
        edit_score.setText(String.valueOf(section.score));
        Button btn_remove = (Button)dlg.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you going to quit this test?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        arrayList.remove(section);

                        Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).child(section._id).setValue(null);
                        task.addOnCompleteListener(activity, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                dlg.dismiss();
                                if (!task.isSuccessful()) {
                                    Utils.showAlert(activity, "Error", task.getException().getLocalizedMessage());
                                } else {
                                    psychologySectionListAdapter.notifyDataSetChanged();
                                    Toast.makeText(activity, "Section has been removed successfully", Toast.LENGTH_SHORT).show();
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
                String sectionName = edit_name.getText().toString().trim();
                String sectionDescription = edit_description.getText().toString().trim();
                String sectionScore = edit_score.getText().toString().trim();
                if (sectionName.length()*sectionDescription.length()*sectionScore.length() == 0) {
                    Utils.showAlert(activity, getResources().getString(R.string.warning), getResources().getString(R.string.please_fill_in_blank_field));
                    return;
                }
                section.name = sectionName; section.description = sectionDescription; section.score = Integer.valueOf(sectionScore);
                Task task  = Utils.mDatabase.child(Utils.tbl_psychology_section).child(section._id).setValue(section);
                task.addOnCompleteListener(activity, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dlg.dismiss();
                        if (!task.isSuccessful()) {
                            Utils.showAlert(activity, "Error", task.getException().getLocalizedMessage());
                        } else {
                            Toast.makeText(activity, "Section name has been updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        dlg.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }
}