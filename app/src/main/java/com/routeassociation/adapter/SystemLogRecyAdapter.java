package com.routeassociation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.routeassociation.R;
import com.routeassociation.pojo.SystemLog;

import java.util.ArrayList;

/**
 * Created by techlead on 30/1/18.
 */

public class SystemLogRecyAdapter extends RecyclerView.Adapter<SystemLogRecyAdapter.SystemLogData> {
    public ArrayList<SystemLog> systemLogArrayList;
    private Context context;


    public class SystemLogData extends RecyclerView.ViewHolder {
        private TextView txtCategory, txtDescription, txtTime;

        public SystemLogData(View itemView) {
            super(itemView);

            txtCategory = (TextView) itemView.findViewById(R.id.txtCategory);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);


        }
    }


    public SystemLogRecyAdapter(Context context, ArrayList<SystemLog> systemLogArrayList) {
        this.context = context;
        this.systemLogArrayList = systemLogArrayList;
    }

    @Override
    public SystemLogRecyAdapter.SystemLogData onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_system_log, parent, false);
        SystemLogData holder = new SystemLogData(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SystemLogRecyAdapter.SystemLogData holder, int position) {

        try {
            SystemLog systemLog = systemLogArrayList.get(position);
            holder.txtCategory.setText("Category : " + systemLog.getCategory());
            holder.txtDescription.setText("Description : " + systemLog.getDescription());
            holder.txtTime.setText("Time : " + systemLog.getTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return systemLogArrayList.size();
    }
}
