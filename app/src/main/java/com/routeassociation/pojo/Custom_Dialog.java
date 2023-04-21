package com.routeassociation.pojo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.routeassociation.AttendanceListDetailsActivity;
import com.routeassociation.R;
import com.routeassociation.adapter.StudentAttendanceAdapter;

import java.util.ArrayList;
import java.util.List;

public class Custom_Dialog extends AppCompatDialogFragment {
    private Context context;

  public Custom_Dialog(Context context){
      this.context = context;
  }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.show_studattendancelist,null);
        builder.setView(view);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Toolbar toolbar = view.findViewById(R.id.toolbar);
            toolbar.setTitle("Student Details");
            toolbar.setTitleTextColor(Color.WHITE);

            ImageButton cancelImgBtn = toolbar.findViewById(R.id.cancelImgBtn);
            cancelImgBtn.setVisibility(View.VISIBLE);
            cancelImgBtn.setOnClickListener(v -> {
                //builder.
            });
        }

//        RecyclerView recyclerView;
//        recyclerView = (RecyclerView)  view.findViewById(R.id.studAttList);
//        List<Student> studLists = new ArrayList<>();
//        studLists.add(new Student("nihar"));
//        studLists.add(new Student("prathamesh"));
//        studLists.add(new Student("vedant"));
//        studLists.add(new Student("atharva"));
//        studLists.add(new Student("ram"));
//        studLists.add(new Student("krishna"));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new StudentAttendanceAdapter(getContext(),studLists));
        return builder.create();
    }
}
//dialog3.setContentView(R.layout.show_studattendancelist);
//
//        dialog3.show();