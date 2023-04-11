package com.routeassociation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.VehicleDetails;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadDocumentsActivity extends AppCompatActivity {

    private ProgressDialog progDailog;
    private Context context;
    private Util util;
    private int orgId, depId;
    private JSONArray response;
    private ArrayList<VehicleDetails> liveList;
    private Map<String, Integer> map;
    private Spinner vehSpinner;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(UploadDocumentsActivity.this,
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
        setContentView(R.layout.activity_upload_documents);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Upload Document");
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        context = this;
        util = new Util(context);

        CheckInternet checkInternet = new CheckInternet();
        boolean checkConnection = checkInternet.checkConnection(context);
        if (checkConnection) {

        } else {
            Toast.makeText(context,
                    "connection not found...plz check connection", Toast.LENGTH_LONG).show();
        }

        //set white back icon to toolbar
        final Drawable upArrow = getResources().getDrawable(R.drawable.back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        FloatingActionButton btnaddNew = (FloatingActionButton) findViewById(R.id.btnaddNew);
        btnaddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadDocumentsActivity.this, NewUploadDocumentActivity.class);
                startActivity(intent);
            }
        });

        vehSpinner = (Spinner) findViewById(R.id.vehSpinner);
        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });
        //load vehicle data
        UploadDocumentsActivity.LoadVehicleData vehicleData = new UploadDocumentsActivity.LoadVehicleData();
        vehicleData.execute(null, null);

    }

    //load vehicle data
    class LoadVehicleData extends AsyncTask<String, String, String> {
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
        protected String doInBackground(String... aurl) {

            try {
                response = util.getAllVehicles(orgId, 0);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            try {
                progDailog.dismiss();
                if (response == null) {
                    Toast.makeText(context, "Vehicles not found !", Toast.LENGTH_LONG).show();
                } else {

                    liveList = new ArrayList<VehicleDetails>();
                    map = new HashMap<String, Integer>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            VehicleDetails vehicleDetails = new VehicleDetails();
                            vehicleDetails.setVehId(jsonObject.getInt("vehId"));
                            vehicleDetails.setVehName(jsonObject.getString("vehNumber"));
                            map.put(jsonObject.getString("vehNumber"), jsonObject.getInt("vehId"));
                            liveList.add(vehicleDetails);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<VehicleDetails> arrayAdapter = new ArrayAdapter<VehicleDetails>(
                            context,
                            android.R.layout.simple_list_item_1,
                            liveList);

                    vehSpinner.setAdapter(arrayAdapter);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (progDailog != null) {
                progDailog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (progDailog != null) {
                progDailog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (progDailog != null) {
                progDailog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
