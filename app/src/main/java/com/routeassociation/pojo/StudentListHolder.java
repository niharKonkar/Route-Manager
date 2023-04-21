package com.routeassociation.pojo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.routeassociation.R;

public class StudentListHolder extends RecyclerView.ViewHolder {

    public TextView nameText;
    public ImageView presentImage;
    public  ImageView absentImage;

    public StudentListHolder(@NonNull View itemView) {
        super(itemView);

        nameText = itemView.findViewById(R.id.nameText);
        presentImage = itemView.findViewById(R.id.presentImgView);
        absentImage = itemView.findViewById(R.id.absentImgView);
    }
}
