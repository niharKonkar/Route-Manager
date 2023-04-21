package com.routeassociation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.routeassociation.R;
import com.routeassociation.pojo.Student;
import com.routeassociation.pojo.StudentListHolder;

import java.util.List;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentListHolder> {
    Context context;
    List<Student> items;
    List<String> ids;

    public StudentAttendanceAdapter(Context context, List<Student> items, List<String> ids ){
        this.context = context;
        this.items = items;
        this.ids = ids;
    }

    @NonNull
    @Override
    public StudentListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentListHolder(LayoutInflater.from(context).inflate(R.layout.list_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListHolder holder, int position) {
        holder.nameText.setText(items.get(position).getName());

        String studIds = String.valueOf(items.get(position).getId());

        if(ids.contains(studIds)){
            holder.presentImage.setVisibility(View.VISIBLE);
            holder.absentImage.setVisibility(View.GONE);
        }else {
            holder.presentImage.setVisibility(View.GONE);
            holder.absentImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
