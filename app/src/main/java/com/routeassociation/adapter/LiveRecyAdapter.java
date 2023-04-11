package com.routeassociation.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.DriverInfoActivity;
import com.routeassociation.LiveMapsActivity;
import com.routeassociation.R;
import com.routeassociation.pojo.GpsTransactionLive;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class LiveRecyAdapter extends RecyclerView.Adapter<LiveRecyAdapter.LiveDataHolder> {

    private Context context;
    private ArrayList<GpsTransactionLive> gpsTransactionLiveArrayList;

    public LiveRecyAdapter(Context context, ArrayList<GpsTransactionLive> gpsTransactionLiveArrayList) {
        this.context = context;
        this.gpsTransactionLiveArrayList = gpsTransactionLiveArrayList;
    }

    public class LiveDataHolder extends RecyclerView.ViewHolder {
        private TextView vehNumber, vehName, speed, timestamp, location, violation;
        private ImageView imgLiveMap, imgNavigate, imgDriver, icon;

        public LiveDataHolder(View itemView) {
            super(itemView);
            vehNumber = (TextView) itemView.findViewById(R.id.vehNumber);
            vehName = (TextView) itemView.findViewById(R.id.vehName);
            speed = (TextView) itemView.findViewById(R.id.speed);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            location = (TextView) itemView.findViewById(R.id.location);
            violation = (TextView) itemView.findViewById(R.id.violation);
            icon = (ImageView) itemView.findViewById(R.id.vehIcon);
            imgDriver = (ImageView) itemView.findViewById(R.id.imgDriver);
            imgNavigate = (ImageView) itemView.findViewById(R.id.imgNavigate);
            imgLiveMap = (ImageView) itemView.findViewById(R.id.imgLiveMap);


        }
    }

    @Override
    public LiveDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_live_details, parent, false);
        LiveDataHolder holder = new LiveDataHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LiveDataHolder holder, int position) {
        final GpsTransactionLive gpsTransactionLive = gpsTransactionLiveArrayList.get(position);


        //vehicle number
        if (gpsTransactionLive.getRouteType() == null || gpsTransactionLive.getRouteType().equals("null") || gpsTransactionLive.getRouteType().equals("")) {
            holder.vehNumber.setText(gpsTransactionLive.getVehNumber());

        } else {
            holder.vehNumber.setText(gpsTransactionLive.getVehNumber() + "(" + gpsTransactionLive.getRouteType() + ")");

        }

        // Changed VehName Placeholder to have Route Name
        if (gpsTransactionLive.getRouteName() != null) {
            holder.vehName.setText("Route: " + gpsTransactionLive.getRouteName());
        } else {
            holder.vehName.setText(gpsTransactionLive.getVehName());
        }

        //timestamp
        holder.timestamp.setText("Time: " + gpsTransactionLive.getGpsTimeStamp());

        //location
        holder.location.setText("Location: " + gpsTransactionLive.getLocation());

        //speed
        holder.speed.setText("Speed: " + gpsTransactionLive.getSpeed() + " Kmph");

        //violation
        holder.violation.setText(gpsTransactionLive.getViolation());


        Date date = stringToDate(gpsTransactionLive.getGpsTimeStamp(), "dd-MM-yyyy HH:mm:ss");
        if (((new Date()).getTime() - date.getTime()) / 3600000 > 4) {
            //row.setBackgroundResource(R.drawable.rounded_corners_red);
            holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.schoolbus_4));
        } else {
            if (gpsTransactionLive.getSpeed().equals("-1") || gpsTransactionLive.getLocation().trim().equals("(9999.9999,9999.9999)")) {
                //row.setBackgroundResource(R.drawable.rounded_corners);
                holder.speed.setText("Speed: -");
                holder.location.setText("Location: -");
                holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.schoolbus_3));
            } else if (gpsTransactionLive.getSpeed().equals("0")) {
                //row.setBackgroundResource(R.drawable.rounded_corners);
                holder.speed.setText("Speed: " + gpsTransactionLive.getSpeed() + " Kmph for " + gpsTransactionLive.getStoppage() + " min(s)");
                holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.schoolbus_2
                ));
            } else {
                //row.setBackgroundResource(R.drawable.rounded_corners_green);
                holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.schoolbus_1));
            }
        }


        //live map
        holder.imgLiveMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Loading Google Map..", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(context, LiveMapsActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("lat", gpsTransactionLive.getGpsLat());
                mBundle.putString("lng", gpsTransactionLive.getGpsLng());
                mBundle.putString("vehNumber", gpsTransactionLive.getVehNumber());
                mBundle.putInt("vehId", gpsTransactionLive.getVehId());
                mBundle.putString("location", gpsTransactionLive.getLocation());
                intent.putExtra("data", mBundle);
                context.startActivity(intent);
            }
        });

        //navigation
        holder.imgNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%s,%s", gpsTransactionLive.getGpsLat(), gpsTransactionLive.getGpsLng());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context.startActivity(intent);
            }
        });

        //driver info
        holder.imgDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DriverInfoActivity.class);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return gpsTransactionLiveArrayList.size();
    }

    private Date stringToDate(String aDate, String aFormat) {

        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }
}
