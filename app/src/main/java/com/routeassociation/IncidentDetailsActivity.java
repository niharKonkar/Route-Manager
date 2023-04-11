package com.routeassociation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.routeassociation.adapter.IncidentDetailsRecyAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.IncidentDetails;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class IncidentDetailsActivity extends AppCompatActivity {
    private Context context;
    private Util util;
    private int usrId, routeId, orgId, depId;
    private ProgressDialog progDailog;
    private String response;
    private ArrayList<IncidentDetails> incidentDetailsArrayList;
    private RecyclerView incidentRecyView;
    private LinearLayout layNoRecord, layParent;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(IncidentDetailsActivity.this,
                    MainActivity.class));
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_details);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Incidents");
            toolbar.setTitleTextColor(Color.WHITE);

            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

            }
            //set white back icon to toolbar
            final Drawable upArrow = getResources().getDrawable(R.drawable.back);
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            context = this;
            util = new Util(context);

            //load login details
            SharedPreferences loginDetails = getSharedPreferences("user", 0);
            String params = loginDetails.getString("params", null);

            JSONArray jsonArray = new JSONArray(params);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String status = jsonObject.getString("status");
            if (status.equals("SUCCESS")) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);

                usrId = jsonObject2.getInt("usrId");
                orgId = jsonObject2.getInt("orgId");
                depId = jsonObject2.getInt("depId");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //get reference

        layNoRecord = (LinearLayout)findViewById(R.id.layNoRecord);
        layParent = (LinearLayout)findViewById(R.id.layParent);

        incidentRecyView = (RecyclerView)findViewById(R.id.incidentRecyView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        incidentRecyView.setLayoutManager(layoutManager);

        //check internet connection
        CheckInternet checkInternet = new CheckInternet();
        boolean checkConnection = checkInternet.checkConnection(context);
        if (checkConnection) {

        } else {
            Toast.makeText(context,
                    "Please check your internet connection or try again later!", Toast.LENGTH_LONG).show();
        }

        //load incident details
        LoadIncidentDetails loadIncidentDetails = new LoadIncidentDetails();
        loadIncidentDetails.execute(null,null);

        FloatingActionButton fbUploadIncident = (FloatingActionButton)findViewById(R.id.fbUploadIncident);
        fbUploadIncident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(IncidentDetailsActivity.this,UploadIncidentActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public class LoadIncidentDetails extends AsyncTask<String,String,String>
    {
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
            try
            {
                progDailog.dismiss();

                if(response != null)
                {
                    JSONArray resArray = new JSONArray(response);
                    JSONObject statusObj = resArray.getJSONObject(0);
                    String status = statusObj.getString("status");
                    if(status.equals("SUCCESS"))
                    {

                        incidentDetailsArrayList = new ArrayList<>();
                        JSONObject dataObject = resArray.getJSONObject(1);
                        JSONArray dataArray =  dataObject.getJSONArray("data");
                        for(int i=0;i<dataArray.length();i++)
                        {
                            JSONObject object = dataArray.getJSONObject(i);

                            IncidentDetails incidentDetails = new IncidentDetails();
                            incidentDetails.setIncDate(object.getString("incDate"));
                            incidentDetails.setIncCat(object.getString("incCat"));
                            incidentDetails.setIncDescription(object.getString("incDescription"));

                            if(object.getString("incVehId") == null ||(object.getString("incVehId").equals("null")))
                            {
                                incidentDetails.setIncVehId(0);
                            }else {
                                incidentDetails.setIncVehId(object.getInt("incVehId"));
                            }
                            incidentDetails.setIncVeh(object.getString("incVeh"));
                            if(object.getString("incRouteId") == null || object.getString("incRouteId").equals("null")) {
                                incidentDetails.setIncRouteId(0);
                            }else {
                                incidentDetails.setIncRouteId(object.getInt("incRouteId"));
                            }
                            incidentDetails.setIncRoute(object.getString("incRoute"));
                            if(object.getString("incDrvId") == null || object.getString("incDrvId").equals("null")){
                                incidentDetails.setIncDrvId(0);
                            }else {
                                incidentDetails.setIncDrvId(object.getInt("incDrvId"));
                            }
                            incidentDetails.setIncDriver(object.getString("incDriver"));
                            incidentDetails.setIncPickupDropPd(object.getString("incPickupDropPd"));
                            incidentDetailsArrayList.add(incidentDetails);

                        }

                        if(incidentDetailsArrayList.size() != 0) {
                            IncidentDetailsRecyAdapter adapter = new IncidentDetailsRecyAdapter(incidentDetailsArrayList, context);
                            incidentRecyView.setAdapter(adapter);
                        }else {
                            Toast.makeText(context, "Incident not found!", Toast.LENGTH_SHORT).show();
                            layNoRecord.setVisibility(View.VISIBLE);
                            layParent.setVisibility(View.GONE);
                        }

                    }else {
                        Toast.makeText(context, "Incident not found!", Toast.LENGTH_SHORT).show();
                        layNoRecord.setVisibility(View.VISIBLE);
                        layParent.setVisibility(View.GONE);
                    }
                }else {
                    Toast.makeText(context, "Incident not found!", Toast.LENGTH_SHORT).show();
                    layNoRecord.setVisibility(View.VISIBLE);
                    layParent.setVisibility(View.GONE);
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {

               response =  util.GetIncidentsByOrgDep(orgId,depId);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
