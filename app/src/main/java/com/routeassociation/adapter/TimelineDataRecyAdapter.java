package com.routeassociation.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.MapsActivity;
import com.routeassociation.R;
import com.routeassociation.pojo.SchoolTimelineData;
import com.routeassociation.util.Util;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TimelineDataRecyAdapter extends RecyclerView.Adapter<TimelineDataRecyAdapter.TimelineData> {
    public ArrayList<SchoolTimelineData> timelineDataArrayList;
    private Context context;
    private int vehId;
    private String alertFlag = "A";
    private Util util;
    private Dialog dialog, dialog2;
    private int i;

    public class TimelineData extends RecyclerView.ViewHolder {
        private TextView txtSchoolEvent, txtSchoolEventType, txtTimestamp;
        private ImageView imgCircle;
        private Button btnLiveMap, btnCall;
        private LinearLayout parentLayout;

        public TimelineData(View itemView) {
            super(itemView);

            txtSchoolEvent = (TextView) itemView.findViewById(R.id.txtSchoolEvent);
            txtSchoolEventType = (TextView) itemView.findViewById(R.id.txtSchoolEventType);
            txtTimestamp = (TextView) itemView.findViewById(R.id.txtTimestamp);
            imgCircle = (ImageView) itemView.findViewById(R.id.imgCircle);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);


        }
    }


    public TimelineDataRecyAdapter(Context context, ArrayList<SchoolTimelineData> timelineDataArrayList) {
        this.context = context;
        this.timelineDataArrayList = timelineDataArrayList;
    }

    @Override
    public TimelineDataRecyAdapter.TimelineData onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_timeline_data, parent, false);
        TimelineData holder = new TimelineData(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TimelineDataRecyAdapter.TimelineData holder, int position) {

        try {

            util = new Util(context);
            final SchoolTimelineData timelineData = timelineDataArrayList.get(position);

            //event name
            holder.txtSchoolEvent.setText("" + timelineData.getSbeEvent());

            //event type
            holder.txtSchoolEventType.setText("Type : " + timelineData.getSbeEventType());


            String Timestamp[] = timelineData.getSbeTimestamp().split(" ");
            String time = Timestamp[1];

            //convert date format
            DateFormat originalFormat = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("hh:mm a");
            Date date = originalFormat.parse(time);
            String formattedDate = targetFormat.format(date);

            //timestamp
            holder.txtTimestamp.setText("Time : " + formattedDate);
            if (alertFlag.equals("A")) {
                holder.imgCircle.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_incomplete));

            } else {
                holder.imgCircle.setImageDrawable(context.getResources().getDrawable(R.drawable.circle_complete));

            }
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_timeline_options);
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    dialog.setCanceledOnTouchOutside(false);

                    TextView eventName = (TextView) dialog.findViewById(R.id.eventName);
                    Button btnLiveMap = (Button) dialog.findViewById(R.id.btnLiveMap);
                    Button btnCall = (Button) dialog.findViewById(R.id.btnCall);

                    eventName.setText("" + timelineData.getSbeEvent());
                    //live map
                    btnLiveMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                dialog.hide();
                                vehId = timelineData.getVehId();

                                String response = util.getLatestLocationByVehId(vehId);

                                if (response == null) {
                                    Toast.makeText(context, "Vehicle location not found !", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                JSONObject object = new JSONObject(response);
                                String status = object.getString("status");
                                if (status.equals("SUCCESS")) {
                                    double lat = object.getDouble("lat");
                                    double lng = object.getDouble("lng");
                                    String address = object.getString("address");

                                    Intent intent = new Intent(context, MapsActivity.class);
                                    intent.putExtra("lat", lat);
                                    intent.putExtra("lng", lng);
                                    intent.putExtra("address", address);
                                    intent.putExtra("vehId", vehId);
                                    context.startActivity(intent);

                                } else {
                                    Toast.makeText(context, "Vehicle location not found !", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    //call
                    btnCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {

                                dialog.hide();

                                for (i = 0; i < timelineData.getRouteDetailsArrayList().size(); i++) {
                                    if (timelineData.getGegId() == timelineData.getRouteDetailsArrayList().get(i).getRouteId()) {
                                        dialog2 = new Dialog(context);
                                        dialog2.setContentView(R.layout.dialog_confirm_call_driver_conductor);
                                        dialog2.show();
                                        Window window = dialog2.getWindow();
                                        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                                        TextView txtDrvName = (TextView) dialog2.findViewById(R.id.txtDrvName);
                                        ImageView btnCallDriver = (ImageView) dialog2.findViewById(R.id.btnCallDriver);
                                        TextView txtCondName = (TextView) dialog2.findViewById(R.id.txtCondName);
                                        ImageView btnCallCond = (ImageView) dialog2.findViewById(R.id.btnCallCond);
                                        Button btnCancel = (Button) dialog2.findViewById(R.id.btnCancel);

                                        btnCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog2.dismiss();
                                            }
                                        });
                                        if (timelineData.getRouteDetailsArrayList().get(i).getDriverName().equals("") || timelineData.getRouteDetailsArrayList().get(i).getDriverName() == null) {
                                            txtDrvName.setText("Driver : - ");
                                        } else {
                                            txtDrvName.setText("Driver : " + timelineData.getRouteDetailsArrayList().get(i).getDriverName() + "(" + timelineData.getRouteDetailsArrayList().get(i).getDriverNumber() + ")");
                                        }
                                        if (timelineData.getRouteDetailsArrayList().get(i).getConductorName().equals("") || timelineData.getRouteDetailsArrayList().get(i).getConductorName() == null) {
                                            txtCondName.setText("Conductor : - ");
                                        } else {
                                            txtCondName.setText("Conductor : " + timelineData.getRouteDetailsArrayList().get(i).getConductorName() + "(" + timelineData.getRouteDetailsArrayList().get(i).getConductorNumber() + ")");
                                        }
                                        //call driver
                                        btnCallDriver.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog2.dismiss();
                                                if (timelineData.getRouteDetailsArrayList().get(i).getDriverNumber().equals("") || timelineData.getRouteDetailsArrayList().get(i).getDriverNumber() == null) {
                                                    Toast.makeText(context, "Unable to call driver!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                } else {
                                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + timelineData.getRouteDetailsArrayList().get(i).getDriverNumber()));
                                                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                        return;
                                                    }
                                                    context.startActivity(intent);
                                                }
                                            }
                                        });
                                        //call conductor
                                        btnCallCond.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog2.dismiss();
                                                if (timelineData.getRouteDetailsArrayList().get(i).getConductorNumber().equals("") || timelineData.getRouteDetailsArrayList().get(i).getConductorNumber() == null) {
                                                    Toast.makeText(context, "Unable to call conductor!", Toast.LENGTH_SHORT).show();
                                                    return;
                                                } else {
                                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + timelineData.getRouteDetailsArrayList().get(i).getConductorNumber()));
                                                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                        return;
                                                    }
                                                    context.startActivity(intent);
                                                }
                                            }
                                        });

                                    }

                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return timelineDataArrayList.size();
    }
}
