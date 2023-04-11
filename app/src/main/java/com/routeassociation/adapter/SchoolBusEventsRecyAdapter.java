package com.routeassociation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.routeassociation.R;
import com.routeassociation.pojo.SchoolEventsData;

import java.util.List;

/**
 * Created by sonal on 29/12/17.
 */
public class SchoolBusEventsRecyAdapter extends RecyclerView.Adapter<SchoolBusEventsRecyAdapter.MyViewHolder>{

    private List<SchoolEventsData> schoolEventsDataList;
    private Context context ;

    public SchoolBusEventsRecyAdapter(Context context, List<SchoolEventsData> schoolEventsDataList) {
        this.context=context;
        this.schoolEventsDataList = schoolEventsDataList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtLocName;
        public TextView txtTimestamp;
       public ImageView imgCircle;

        public MyViewHolder(View view) {
            super(view);

            txtLocName = (TextView) view.findViewById(R.id.txtLocName);
            txtTimestamp = (TextView) view.findViewById(R.id.txtTimestamp);
            imgCircle = (ImageView) view.findViewById(R.id.imgCircle);


        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_location_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            SchoolEventsData schoolEventsData = schoolEventsDataList.get(position);

            //location name
            holder.txtLocName.setText(schoolEventsData.getEventName());

            //timestamp
            holder.txtTimestamp.setText(schoolEventsData.getEventTimestamp());

            //set color code
            String alertFlag =
                    schoolEventsData.getEventAlertFlag();
            if (alertFlag.equals("N")) {
                holder.imgCircle.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_complete));

            } else {
                holder.imgCircle.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_incomplete));

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }




    }

    @Override
    public int getItemCount() {
        return schoolEventsDataList.size();
    }
}

