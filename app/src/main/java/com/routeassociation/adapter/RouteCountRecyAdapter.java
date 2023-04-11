package com.routeassociation.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.routeassociation.R;
import com.routeassociation.RouteCountRecyInterface;
import com.routeassociation.pojo.RouteDetails;

import java.util.ArrayList;

public class RouteCountRecyAdapter extends RecyclerView.Adapter<RouteCountRecyAdapter.RouteCountHolder> {

    private Context context;
    private ArrayList<RouteDetails> routeDetailsArrayList;
    private RouteCountRecyInterface itemListener;

    public RouteCountRecyAdapter(Context context, ArrayList<RouteDetails> routeDetailsArrayList, RouteCountRecyInterface itemListener) {
        this.context = context;
        this.routeDetailsArrayList = routeDetailsArrayList;
        this.itemListener = itemListener;
    }

    public class RouteCountHolder extends RecyclerView.ViewHolder {

        TextView routeName;

        public RouteCountHolder(View itemView) {
            super(itemView);

            routeName = itemView.findViewById(R.id.routeRecyName);
        }
    }

    @Override
    public RouteCountRecyAdapter.RouteCountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_routes_count, parent, false);
        RouteCountHolder holder = new RouteCountHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RouteCountRecyAdapter.RouteCountHolder holder, int position) {
        try {
            RouteDetails routeDetails = routeDetailsArrayList.get(position);
            holder.routeName.setText(routeDetails.getRouteName());

            holder.routeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.recyclerViewListClicked(holder.routeName.getText().toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return routeDetailsArrayList.size();
    }

    //This method will filter the list
    //here we are passing the filtered data
    public void filterList(ArrayList<RouteDetails> filteredRoutesArrayList) {
        this.routeDetailsArrayList = filteredRoutesArrayList;
        notifyDataSetChanged();
    }

}
