package com.routeassociation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.routeassociation.R;
import com.routeassociation.pojo.NotificationHistoryData;

import java.util.List;


public class NotificationHistoryDataRecyAdapter extends RecyclerView.Adapter<NotificationHistoryDataRecyAdapter.MyViewHolder> {

    private List<NotificationHistoryData> notificationHistoryDataList;
    private Context context;

    public NotificationHistoryDataRecyAdapter(Context context, List<NotificationHistoryData> notificationHistoryDataList) {
        this.context = context;
        this.notificationHistoryDataList = notificationHistoryDataList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtDate;
        public TextView txtMessage;

        public MyViewHolder(View view) {
            super(view);

            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtMessage = (TextView) view.findViewById(R.id.txtMessage);


        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notification_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            NotificationHistoryData historyData = notificationHistoryDataList.get(position);

            //message
            holder.txtMessage.setText(historyData.getMessage());

            //date
            holder.txtDate.setText(historyData.getDate());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return notificationHistoryDataList.size();
    }
}

