package com.routeassociation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.adapter.RouteDetailsAndSendCommunicaionRecyAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.RouteDetails;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectCommunicationDetailsActivity extends AppCompatActivity {
    private Spinner depSpinner;
    private Context context;
    private ProgressDialog progDailog;
    private int orgId;
    private Util util;

    private String[] departments;
    private int[] depIds;
    private int depId;
    private RecyclerView routeDetailsRecyView;
    private ArrayList<RouteDetails> routeDetailsArrayList;
    private TextView depNameTextView;
    private LinearLayout layParent,layNoRecord;
    private String depName;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(SelectCommunicationDetailsActivity.this,
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
        setContentView(R.layout.activity_select_communication_details);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Communication");
            toolbar.setTitleTextColor(Color.WHITE);

            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

            }
            //set white back icon to toolbar
            final Drawable upArrow = getResources().getDrawable(R.drawable.back);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            context = this;
            util = new Util(context);

            //check internet connection
            CheckInternet checkInternet = new CheckInternet();
            boolean checkConnection = checkInternet.checkConnection(context);
            if (checkConnection) {

            } else {
                Toast.makeText(context,
                        "Please check your internet connection or try again later!", Toast.LENGTH_LONG).show();
            }

//            depSpinner = (Spinner) findViewById(R.id.depSpinner);
            depNameTextView = findViewById(R.id.depName);
            routeDetailsRecyView = (RecyclerView)findViewById(R.id.routeDetailsRecyView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            routeDetailsRecyView.setLayoutManager(layoutManager);
            layNoRecord = (LinearLayout)findViewById(R.id.layNoRecord);
            layParent = (LinearLayout)findViewById(R.id.layParent);

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

                orgId = jsonObject2.getInt("orgId");
                depId = jsonObject2.getInt("depId");
                depName = jsonObject2.getString("depName");

                depNameTextView.setText(depName);

                if(depId != 0)
                {
                    LoadRoutes loadRoutes = new LoadRoutes();
                    loadRoutes.execute(null,null);
                }
            }

//            //load department
//            LoadDepartment loadDepartment = new LoadDepartment();
//            loadDepartment.execute(null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LoadDepartment extends AsyncTask<String, String, String> {
        private String response;

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
                    Toast.makeText(getApplicationContext(), "Unable to parse...", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONArray array = new JSONArray(response);
                JSONObject statusObj = array.getJSONObject(0);
                JSONObject data = array.getJSONObject(1);

                String status = statusObj.getString("status");
                if (!status.equalsIgnoreCase("SUCCESS")) {
                    Toast.makeText(getApplicationContext(), data.getString("data"), Toast.LENGTH_LONG).show();
                    return;
                }

                JSONArray jsonArray = data.getJSONArray("data");
                int length = jsonArray.length();
                departments = new String[length];
                depIds = new int[length];
                for (int i = 0; i < length; i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    departments[i] = jo.getString("depName");
                    depIds[i] = jo.getInt("depId");
                }

                ArrayAdapter<String> depAdaptor = new ArrayAdapter<String>(SelectCommunicationDetailsActivity.this,
                        android.R.layout.simple_spinner_item, departments);
                depAdaptor
                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                depSpinner.setAdapter(depAdaptor);

                depSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        depId = depIds[position];
                        depName = depSpinner.getSelectedItem().toString().trim();

                        if(depId != 0)
                        {
                            LoadRoutes loadRoutes = new LoadRoutes();
                            loadRoutes.execute(null,null);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = util.getDepartments(orgId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    //loading routes
    class LoadRoutes extends AsyncTask<String, String, String> {
        private JSONArray response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(SelectCommunicationDetailsActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();

        }

        @Override
        protected String doInBackground(String... aurl) {

            try {
                response = util.getRoutes(orgId, depId);

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

                if (response != null) {


                    if(response.length() == 0 )
                    {
                        Toast.makeText(context, "Route details not found !", Toast.LENGTH_SHORT).show();

                        layNoRecord.setVisibility(View.VISIBLE);
                        layParent.setVisibility(View.GONE);
                        routeDetailsArrayList.clear();
                        return;
                    }
                    routeDetailsArrayList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {

                        RouteDetails details = new RouteDetails();
                        JSONObject jsonObject = response.getJSONObject(i);
                        details.setRouteId(jsonObject.getInt("routeId"));
                        details.setRouteName(jsonObject.getString("routeName"));
                        details.setVehId(jsonObject.getInt("vehId"));
                        details.setVehNumber(jsonObject.getString("vehNumber"));
                        details.setDriverName(jsonObject.getString("driverName"));
                        details.setDriverId(jsonObject.getString("driverId"));
                        details.setConductorName(jsonObject.getString("conductorName"));
                        details.setDriverNumber(jsonObject.getString("driverNumber"));
                        details.setConductorNumber(jsonObject.getString("conductorNumber"));
                        details.setDropDriverId(jsonObject.getString("dropDriverId"));
                        details.setDropDiverNumber(jsonObject.getString("dropDriverNumber"));
                        details.setDropDriverName(jsonObject.getString("dropDriverName"));
                        details.setDropConductorName(jsonObject.getString("dropConductorName"));
                        details.setDropConductorNumber(jsonObject.getString("dropConductorNumber"));

                        details.setPickupStudentCount(jsonObject.getString("routePickupStudentCount"));
                        details.setPickupTeacherCount(jsonObject.getString("routePickupStaffCount"));
                        details.setDropStudentCount(jsonObject.getString("routeDropStudentCount"));
                        details.setDropTeacherCount(jsonObject.getString("routeDropStaffCount"));
                        details.setBusCapacity(jsonObject.getString("vehSeatCapacity"));


                        if (!jsonObject.has("routeDesc")) {
                            details.setRouteDesc("-");
                        } else {
                            details.setRouteDesc(jsonObject.getString("routeDesc"));
                        }

                        routeDetailsArrayList.add(details);
                    }

                    if(routeDetailsArrayList.size() > 0) {
                        //set adapter
                        layNoRecord.setVisibility(View.GONE);
                        layParent.setVisibility(View.VISIBLE);
                        RouteDetailsAndSendCommunicaionRecyAdapter adapter = new RouteDetailsAndSendCommunicaionRecyAdapter(routeDetailsArrayList, SelectCommunicationDetailsActivity.this, orgId, depId,depName);
                        routeDetailsRecyView.setAdapter(adapter);
                    }

                } else {
                    Toast.makeText(context, "Route details not found!", Toast.LENGTH_SHORT).show();

                    layNoRecord.setVisibility(View.VISIBLE);
                    layParent.setVisibility(View.GONE);
                    routeDetailsArrayList.clear();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
