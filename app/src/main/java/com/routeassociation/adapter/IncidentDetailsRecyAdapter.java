package com.routeassociation.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.routeassociation.R;
import com.routeassociation.pojo.IncidentDetails;

import java.util.ArrayList;

public class IncidentDetailsRecyAdapter extends RecyclerView.Adapter<IncidentDetailsRecyAdapter.IncidentDetailsHolder> {


    private ArrayList<IncidentDetails> incidentDetailsArrayList;
    private Context context;

    public IncidentDetailsRecyAdapter(ArrayList<IncidentDetails> incidentDetailsArrayList, Context context) {
        this.incidentDetailsArrayList = incidentDetailsArrayList;
        this.context = context;
    }

    public class IncidentDetailsHolder extends RecyclerView.ViewHolder {

        private TextView txtIncCat,txtIncDesc, txtIncDate, txtIncVehName, txtIncRouteName, txtIncDriverName, txtIncPickDrop,txtIncKms;

        public IncidentDetailsHolder(@NonNull View itemView) {
            super(itemView);
            txtIncDesc = (TextView) itemView.findViewById(R.id.txtIncDesc);
            txtIncDate = (TextView) itemView.findViewById(R.id.txtIncDate);
            txtIncVehName = (TextView) itemView.findViewById(R.id.txtIncVehName);
            txtIncRouteName = (TextView) itemView.findViewById(R.id.txtIncRouteName);
            txtIncDriverName = (TextView) itemView.findViewById(R.id.txtIncDriverName);
            txtIncPickDrop = (TextView) itemView.findViewById(R.id.txtIncPickDrop);
            txtIncCat = (TextView) itemView.findViewById(R.id.txtIncCat);
            txtIncKms = (TextView) itemView.findViewById(R.id.txtIncKms);

        }
    }

    @NonNull
    @Override
    public IncidentDetailsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_incident_details, viewGroup, false);
        IncidentDetailsHolder holder = new IncidentDetailsHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull IncidentDetailsHolder incidentDetailsHolder, int i) {

        IncidentDetails incidentDetails = incidentDetailsArrayList.get(i);

        //inc desc
        incidentDetailsHolder.txtIncDesc.setText(incidentDetails.getIncDescription());

        //inc date
        if (incidentDetails.getIncDate() == null || incidentDetails.getIncDate().equals("") || incidentDetails.getIncDate().equals("null")) {
            incidentDetailsHolder.txtIncDate.setText("Incident Date: -");
        } else {
            incidentDetailsHolder.txtIncDate.setText("Incident Date: " + incidentDetails.getIncDate());
        }

        //driver
        if (incidentDetails.getIncDriver() == null || incidentDetails.getIncDriver().equals("") || incidentDetails.getIncDriver().equals("null")) {
            incidentDetailsHolder.txtIncDriverName.setText("Driver Name: " + incidentDetails.getIncDriver());

        } else {
            incidentDetailsHolder.txtIncDriverName.setText("Driver Name: " + incidentDetails.getIncDriver());
        }

        //vehicle
        if (incidentDetails.getIncVeh() == null || incidentDetails.getIncVeh().equals("") || incidentDetails.getIncVeh().equals("null")) {
            incidentDetailsHolder.txtIncVehName.setText("Vehicle Name: -");
        } else {
            incidentDetailsHolder.txtIncVehName.setText("Vehicle Name: " + incidentDetails.getIncVeh());
        }

        //route
        if (incidentDetails.getIncRoute() == null || incidentDetails.getIncRoute().equals("") || incidentDetails.getIncRoute().equals("null")) {
            incidentDetailsHolder.txtIncRouteName.setText("Route : -");
        } else {
            incidentDetailsHolder.txtIncRouteName.setText("Route Name: " + incidentDetails.getIncRoute());
        }

        //pickdrop
        if (incidentDetails.getIncPickupDropPd() == null || incidentDetails.getIncPickupDropPd().equals("") || incidentDetails.getIncPickupDropPd().equals("null")) {
            incidentDetailsHolder.txtIncPickDrop.setText("PickDrop : -");
        } else {
            incidentDetailsHolder.txtIncPickDrop.setText("PickDrop: " + incidentDetails.getIncPickupDropPd());
        }

        //cat
        if (incidentDetails.getIncCat() == null || incidentDetails.getIncCat().equals("") || incidentDetails.getIncCat().equals("null")) {
            incidentDetailsHolder.txtIncCat.setText("Category : -");
        } else {
            incidentDetailsHolder.txtIncCat.setText("Category: " + incidentDetails.getIncCat());
        }

        //kms
        if (incidentDetails.getIncKms() == 0 ) {
            incidentDetailsHolder.txtIncKms.setText("Kms : -");
        } else {
            incidentDetailsHolder.txtIncKms.setText("Kms: " + incidentDetails.getIncKms());
        }
    }

    @Override
    public int getItemCount() {
        return incidentDetailsArrayList.size();
    }
}
