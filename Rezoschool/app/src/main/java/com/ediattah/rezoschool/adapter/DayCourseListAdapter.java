package com.ediattah.rezoschool.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.ediattah.rezoschool.Model.DayCourseModel;
import com.ediattah.rezoschool.Model.Library;
import com.ediattah.rezoschool.Model.School;
import com.ediattah.rezoschool.Model.Syllabus;
import com.ediattah.rezoschool.Model.Teacher;
import com.ediattah.rezoschool.R;
import com.ediattah.rezoschool.Utils.Utils;
import com.ediattah.rezoschool.fragments.SchoolClassFragment;
import com.ediattah.rezoschool.ui.LibraryDetailActivity;
import com.ediattah.rezoschool.ui.PDFViewerActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class DayCourseListAdapter extends BaseAdapter {
    ArrayList<Syllabus> arrayList;

    Context context;
    SchoolClassFragment fragment;

    public DayCourseListAdapter(Context _context, ArrayList<Syllabus> _arrayList) {
        context = _context;
        this.arrayList = _arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Syllabus syllabus = arrayList.get(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_day_course, null);
        }
        final TextView txt_course = view.findViewById(R.id.txt_course);
        final TextView txt_school = view.findViewById(R.id.txt_school);
        TextView txt_time = view.findViewById(R.id.txt_time);
        TextView txt_title = view.findViewById(R.id.txt_title);
        TextView txt_thematic = view.findViewById(R.id.txt_thematic);
        TextView txt_summary = view.findViewById(R.id.txt_summary);
        TextView txt_title_next = view.findViewById(R.id.txt_title_next);
        TextView txt_thematic_next = view.findViewById(R.id.txt_thematic_next);
        TextView txt_attach = view.findViewById(R.id.txt_attachment);
        TextView txt_comment = view.findViewById(R.id.txt_comment);
        TextView txt_attachment_view = view.findViewById(R.id.txt_attachment_view);
        txt_title.setText(syllabus.title);
        txt_thematic.setText(syllabus.thematic);
        txt_summary.setText(syllabus.summary);
        txt_title_next.setText(syllabus.title_next);
        txt_thematic_next.setText(syllabus.thematic_next);
        if (syllabus.attach.length() == 0) {
            txt_attach.setText(context.getResources().getString(R.string.no_file));
            txt_attachment_view.setVisibility(View.GONE);
        } else {
            txt_attach.setText(context.getResources().getString(R.string.pdf_file));
            txt_attachment_view.setVisibility(View.VISIBLE);
        }
        txt_comment.setText(syllabus.comment);
        txt_attachment_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PDFViewerActivity.class);
                intent.putExtra("Attach", syllabus.attach);
                context.startActivity(intent);
            }
        });
        txt_time.setText(syllabus.time.start_time + " ~ " + syllabus.time.end_time);
        Utils.mDatabase.child(Utils.tbl_school).orderByChild(syllabus.school_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    for (DataSnapshot datas:dataSnapshot.getChildren()) {
                        School school = datas.getValue(School.class);
                        txt_school.setText(context.getResources().getString(R.string.school_) + school.number);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Button btn_remove = view.findViewById(R.id.btn_remove);
        if (Utils.currentUser.type.equals(Utils.TEACHER)) {
            btn_remove.setVisibility(View.VISIBLE);
        } else {
            btn_remove.setVisibility(View.GONE);
        }
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.are_you_going_to_remove_this_item));
                builder.setPositiveButton(context.getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Utils.mDatabase.child(Utils.tbl_syllabus).child(syllabus._id).setValue(null);
                        Toast.makeText(context, context.getResources().getString(R.string.successfully_deleted), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(context.getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        txt_course.setText(syllabus.course);
        return view;
    }
}
