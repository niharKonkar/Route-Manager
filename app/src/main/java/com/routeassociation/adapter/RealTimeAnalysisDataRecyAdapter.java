package com.routeassociation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.routeassociation.R;
import com.routeassociation.pojo.RealTimePickDropData;

import java.util.List;

public class RealTimeAnalysisDataRecyAdapter extends RecyclerView.Adapter<RealTimeAnalysisDataRecyAdapter.MyViewHolder> {

    private List<RealTimePickDropData> realTimePickDropDataList;
    private Context context;
    private boolean colorFlag;
    private String selectedStatus;

    public RealTimeAnalysisDataRecyAdapter(Context context, List<RealTimePickDropData> realTimePickDropDataList, boolean colorFlag, String selectedStatus) {
        this.context = context;
        this.realTimePickDropDataList = realTimePickDropDataList;
        this.colorFlag = colorFlag;
        this.selectedStatus = selectedStatus;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView routeName;
        public TextView geofenceName, vehNumber, location, timestamp;

        public MyViewHolder(View view) {
            super(view);

            routeName = (TextView) view.findViewById(R.id.routeName);
            geofenceName = (TextView) view.findViewById(R.id.geofenceName);
            vehNumber = (TextView) view.findViewById(R.id.vehNumber);
            location = (TextView) view.findViewById(R.id.location);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_realtimepickdrop_details, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            RealTimePickDropData analysisData = realTimePickDropDataList.get(position);

            //route name
            if (analysisData.getRouteName().equals("") || analysisData.getRouteName() == null || analysisData.getRouteName().equals("null")) {
                holder.routeName.setText("-");
            } else {
                holder.routeName.setText(analysisData.getRouteName());
            }

            //geofence name
            if (analysisData.getGeofenceName().equals("") || analysisData.getGeofenceName() == null || analysisData.getGeofenceName().equals("null")) {
                holder.geofenceName.setText("Geofence Name : - ");
            } else {
                holder.geofenceName.setText("Geofence Name : " + analysisData.getGeofenceName());
            }

            //location
            if (analysisData.getLocation().equals("") || analysisData.getLocation() == null || analysisData.getLocation().equals("null")) {
                holder.location.setText("Location : - ");
            } else {
                holder.location.setText("Location : " + analysisData.getLocation());
            }

            //timestamp
            if (analysisData.getTimestamp().equals("") || analysisData.getTimestamp() == null || analysisData.getTimestamp().equals("null")) {
                holder.timestamp.setText("Timestamp : -");
            } else {
                holder.timestamp.setText("Timestamp : " + analysisData.getTimestamp());
            }

            //vehicle number
            if (analysisData.getVehicleNumber().equals("") || analysisData.getVehicleNumber() == null || analysisData.getVehicleNumber().equals("null")) {
                holder.vehNumber.setText("Vehicle Number : - ");
            } else {
                holder.vehNumber.setText("Vehicle Number : " + analysisData.getVehicleNumber());

            }

            //set background as per status

           /* if(colorFlag  == true ) {
                if (analysisData.getStatus().equals("OK") && colorFlag == true) {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (analysisData.getStatus().equals("CRITICAL")) {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.red));
                } else {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.purple));
                }
            }else
            {
                if (analysisData.getStatus().equals("OK")) {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.red));
                } else if (analysisData.getStatus().equals("CRITICAL")) {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.purple));
                }
            }*/

            //set color to route name
            if (selectedStatus.equals("O") && colorFlag == false) {
                holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else if (selectedStatus.equals("O") && colorFlag == true) {
                holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.red));
            }

            //set color to route name
            if (selectedStatus.equals("C") && colorFlag == false) {
                holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.red));
            } else if (selectedStatus.equals("C") && colorFlag == true) {
                holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.green));
            }
            //set color to route name
            if (selectedStatus.equals("T") && colorFlag == false) {
                if (analysisData.getStatus().equals("OK")) {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else if (analysisData.getStatus().equals("CRITICAL")) {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.red));
                } else {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.purple));
                }
            } else if (selectedStatus.equals("T") && colorFlag == true) {
                if (analysisData.getStatus().equals("OK")) {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.red));
                } else if (analysisData.getStatus().equals("CRITICAL")) {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.green));
                } else {
                    holder.routeName.setBackgroundColor(context.getResources().getColor(R.color.purple));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return realTimePickDropDataList.size();
    }
}

