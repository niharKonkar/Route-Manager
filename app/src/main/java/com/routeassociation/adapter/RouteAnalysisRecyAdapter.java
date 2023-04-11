package com.routeassociation.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.routeassociation.R;
import com.routeassociation.pojo.RouteAnalysis;

import java.util.ArrayList;

/**
 * Created by techlead on 15/7/18.
 */

public class RouteAnalysisRecyAdapter extends RecyclerView.Adapter<RouteAnalysisRecyAdapter.RouteAnalysisHolder> {
    private Context context;
    private ArrayList<RouteAnalysis> routeAnalysisArrayList;

    public RouteAnalysisRecyAdapter(Context context, ArrayList<RouteAnalysis> routeAnalysisArrayList) {
        this.context = context;
        this.routeAnalysisArrayList = routeAnalysisArrayList;
    }

    public class RouteAnalysisHolder extends RecyclerView.ViewHolder {
        private TextView txtgplName, txtMultipleTime, txtActualTime, txtScheduledTime;
        private ImageView imgCircle;

        public RouteAnalysisHolder(View itemView) {
            super(itemView);
            txtgplName = (TextView) itemView.findViewById(R.id.txtgplName);
            txtMultipleTime = (TextView) itemView.findViewById(R.id.txtMultipleTime);
            txtActualTime = (TextView) itemView.findViewById(R.id.txtActualTime);
            txtScheduledTime = (TextView) itemView.findViewById(R.id.txtScheduledTime);
            imgCircle = (ImageView) itemView.findViewById(R.id.imgCircle);


        }
    }

    @Override
    public RouteAnalysisHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_route_analysis, parent, false);
        RouteAnalysisHolder holder = new RouteAnalysisHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RouteAnalysisHolder holder, int position) {
        final RouteAnalysis routeAnalysis = routeAnalysisArrayList.get(position);

        //name
        holder.txtgplName.setText(routeAnalysis.getGplName());


        //set color code
        if (routeAnalysis.getGplColourCode().equals("1000")) {
            holder.imgCircle.setImageResource(R.drawable.circle_incomplete);
        } else if (routeAnalysis.getGplColourCode().equals("2000")) {
            holder.imgCircle.setImageResource(R.drawable.circle_complete);
        } else if (routeAnalysis.getGplColourCode().equals("3000")) {
            holder.imgCircle.setImageResource(R.drawable.circle_purple);
        } else {
            holder.imgCircle.setImageResource(R.drawable.circle_blue);
        }


        //scheduled time
        if (routeAnalysis.getGplTime() == null || routeAnalysis.getGplTime().equals("") || routeAnalysis.getGplTime().equals("null")) {
            holder.txtScheduledTime.setVisibility(View.GONE);

        } else {
            holder.txtScheduledTime.setVisibility(View.VISIBLE);
            holder.txtScheduledTime.setText("Scheduled Time: " + routeAnalysis.getGplTime());
        }


        //actual timing
        //multiple timings
        if (routeAnalysis.getGplMultipleTimings() == null || routeAnalysis.getGplMultipleTimings().equals("") || routeAnalysis.getGplMultipleTimings().equals("null")) {

            holder.txtMultipleTime.setVisibility(View.GONE);

            holder.txtActualTime.setVisibility(View.VISIBLE);

            if (routeAnalysis.getGplActualTime() == null || routeAnalysis.getGplActualTime().equals("") || routeAnalysis.getGplActualTime().equals("null")) {
                holder.txtActualTime.setText("Actual Time: -");
            } else {
                holder.txtActualTime.setText("Actual Time: " + routeAnalysis.getGplActualTime());
            }

        } else {
            holder.txtActualTime.setVisibility(View.GONE);
            holder.txtMultipleTime.setVisibility(View.VISIBLE);
            holder.txtMultipleTime.setText("Multiple Time: " + routeAnalysis.getGplMultipleTimings());
        }


    }

    @Override
    public int getItemCount() {
        return routeAnalysisArrayList.size();
    }


}
