package com.routeassociation.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.R;
import com.routeassociation.SelectCommunicationDetailsActivity;
import com.routeassociation.SendNotificationActivity;
import com.routeassociation.pojo.RouteDetails;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RouteDetailsAndSendCommunicaionRecyAdapter extends RecyclerView.Adapter<RouteDetailsAndSendCommunicaionRecyAdapter.RouteDetailsHolder> {

    private ArrayList<RouteDetails> routeDetailsArrayList;
    private Context context;
    private int orgId, depId,gegId;
    private String message;
    private String pickupDrop="P";
    private Util util;
    private String depName;

    public RouteDetailsAndSendCommunicaionRecyAdapter(ArrayList<RouteDetails> routeDetailsArrayList, Context context, int orgId, int depId,String depName) {
        this.routeDetailsArrayList = routeDetailsArrayList;
        this.context = context;
        this.orgId = orgId;
        this.depId = depId;
        this.depName = depName;
    }

    public class RouteDetailsHolder extends RecyclerView.ViewHolder {
        private TextView txtRouteName;
        private ImageView imgMore;
        private LinearLayout layCommunication;
        private Button btnSendMessage;
        private EditText edtMessage;
        private RadioGroup rgPickDrop;

        public RouteDetailsHolder(@NonNull View itemView) {
            super(itemView);

            imgMore = (ImageView) itemView.findViewById(R.id.imgMore);
            txtRouteName = (TextView) itemView.findViewById(R.id.txtRouteName);
            btnSendMessage = (Button) itemView.findViewById(R.id.btnSendMessage);
            edtMessage = (EditText) itemView.findViewById(R.id.edtMessage);
            rgPickDrop = (RadioGroup)itemView.findViewById(R.id.rgPickDrop);

            layCommunication = (LinearLayout) itemView.findViewById(R.id.layCommunication);

        }
    }

    @NonNull
    @Override
    public RouteDetailsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_route_communication_details, viewGroup, false);
        RouteDetailsHolder holder = new RouteDetailsHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final RouteDetailsHolder routeDetailsHolder, int i) {

        util = new Util(context);
        final RouteDetails routeDetails = routeDetailsArrayList.get(i);
        routeDetailsHolder.txtRouteName.setText("" + routeDetails.getRouteName());
        routeDetailsHolder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gegId= routeDetails.getRouteId();
                message = routeDetailsHolder.edtMessage.getText().toString().trim();

                Intent intent = new Intent(context,SendNotificationActivity.class);

                intent.putExtra("orgId",orgId);
                intent.putExtra("depId",depId);
                intent.putExtra("gegId",gegId);
                intent.putExtra("routeName",routeDetails.getRouteName());
                intent.putExtra("depName",depName);
                ((Activity)context).startActivity(intent);
                ((Activity)context).finish();
                /*if (routeDetailsHolder.layCommunication.getVisibility() == View.VISIBLE) {
                    routeDetailsHolder.layCommunication.setVisibility(View.GONE);
                } else {
                    routeDetailsHolder.layCommunication.setVisibility(View.VISIBLE);
                }*/
            }
        });
        routeDetailsHolder.rgPickDrop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbPickup)
                {
                    pickupDrop = "P";
                }else if(checkedId == R.id.rbDrop)
                {
                    pickupDrop = "D";
                }
            }
        });
        routeDetailsHolder.btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    gegId= routeDetails.getRouteId();
                    message = routeDetailsHolder.edtMessage.getText().toString().trim();

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.execute(null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return routeDetailsArrayList.size();
    }

    public class SendMessage extends AsyncTask<String, String, String> {
        private String response;
        private ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(context);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                progDailog.dismiss();

                if (response == null || response.isEmpty()) {
                    Toast.makeText(context, "Failed to send notification!", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONArray array = new JSONArray(response);
                JSONObject statusObj = array.getJSONObject(0);
                JSONObject data = array.getJSONObject(1);

                String status = statusObj.getString("status");
                if (!status.equalsIgnoreCase("SUCCESS")) {
                    Toast.makeText(context, "Failed to send notification!", Toast.LENGTH_LONG).show();
                    return;
                }else {
                    Toast.makeText(context, "Notification sent successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,SelectCommunicationDetailsActivity.class);
                    ((Activity)context).startActivity(intent);
                    ((Activity)context).finish();
                }




            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = util.sendCommunication(orgId,depId,gegId,pickupDrop,message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
