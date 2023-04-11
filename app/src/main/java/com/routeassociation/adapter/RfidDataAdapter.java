package com.routeassociation.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.routeassociation.R;
import com.routeassociation.pojo.RfidData;

import java.util.List;

public class RfidDataAdapter extends RecyclerView.Adapter<RfidDataAdapter.MyViewHolder> {

    private List<RfidData> rfidDataList;

    public RfidDataAdapter(List<RfidData> gptList) {
        this.rfidDataList = gptList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rasName;
        public TextView rasCard;
        public TextView rtnViolation;
        public TextView rtnTimestamp;

        public MyViewHolder(View view) {
            super(view);
            rasName = (TextView) view.findViewById(R.id.rasName);
            rasCard = (TextView) view.findViewById(R.id.rasCard);
            rtnViolation = (TextView) view.findViewById(R.id.rtnViolation);
            rtnTimestamp = (TextView) view.findViewById(R.id.rtnTimestamp);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_show_rfid_data, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            RfidData gpt = rfidDataList.get(position);

            //name
            holder.rasName.setText(gpt.getRasName());

            //card
            if (gpt.getRasCard() == null || gpt.getRasCard().equals("")) {
                holder.rasCard.setText(" - ");
            } else {
                holder.rasCard.setText(gpt.getRasCard());
            }

            //violation
            if (gpt.getRtnViolation() == null || gpt.getRtnViolation().equals("")) {

                holder.rtnViolation.setText(" - ");
            } else {
                holder.rtnViolation.setText(gpt.getRtnViolation());
            }

            //timestamp
            if (gpt.getRtnTimestamp() == null || gpt.getRtnTimestamp().equals("")) {
                holder.rtnTimestamp.setText(" - ");
            } else {
                holder.rtnTimestamp.setText(gpt.getRtnTimestamp());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return rfidDataList.size();
    }
}

