package com.routeassociation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.routeassociation.adapter.RoutesDetailsRecyclerAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.RouteDetails;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoutesDetailsAndInfoActivity extends AppCompatActivity {

    private Util util;
    private RecyclerView recycler_view;
    private ArrayList<RouteDetails> routeDetailslist;
    private Context context;
    private Dialog dialog;
    private String params;
    private int orgId;
    public static int depId;
    private String showRfidFlag;
    private ProgressDialog progDailog;
    private JSONArray response;
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private EditText edtSearch;
    private int usrId, ugpId;
    private RoutesDetailsRecyclerAdapter adapter2;
    private String[] departments;
    private int[] depIds;
    private Spinner depSpinner;
    private String depName;
    private TextView departmentText;
    private LinearLayout departmentSelectionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_details_and_info);
        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Route Details");
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

            try {

                //get reference
                context = this;
                util = new Util(context);
                recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
                depSpinner = findViewById(R.id.depSpinner);
                departmentSelectionLayout = findViewById(R.id.departmentSelectionLayout);
                edtSearch = (EditText) findViewById(R.id.edtSearch);
                dialog = new Dialog(context);

                //check internet
                CheckInternet checkInternet = new CheckInternet();
                boolean checkConnection = checkInternet.checkConnection(context);
                if (checkConnection) {

                } else {
                    Toast.makeText(RoutesDetailsAndInfoActivity.this,
                            "connection not found...plz check connection", Toast.LENGTH_LONG).show();
                }

                //search data
                addTextListener();

                //load login details
                SharedPreferences loginDetails = getSharedPreferences("user", 0);
                params = loginDetails.getString("params", null);

                JSONArray jsonArray = new JSONArray(params);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String status = jsonObject.getString("status");
                if (status.equals("SUCCESS")) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                    JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                    orgId = jsonObject2.getInt("orgId");
                    depId = jsonObject2.getInt("depId");
                    usrId = jsonObject2.getInt("usrId");
                    ugpId = jsonObject2.getInt("ugpId");
                    showRfidFlag = jsonObject2.getString("showRfidFlag");

                }

                routeDetailslist = new ArrayList<>();
                recycler_view.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext());
                recycler_view.setLayoutManager(layoutManager2);

                //load list of routes
                try {

                    if (ugpId == 2) {

                        departmentSelectionLayout.setVisibility(View.VISIBLE);
                        LoadDepartment loadDepartment = new LoadDepartment();
                        loadDepartment.execute(null, null);
                    } else {

                        departmentSelectionLayout.setVisibility(View.GONE);
                        LoadRoutes mAsyync = new LoadRoutes();
                        mAsyync.execute(null, null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //search by text
    public void addTextListener() {
        try {
            edtSearch.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence query, int start, int before, int count) {

                    query = query.toString().toLowerCase();

                    final ArrayList<RouteDetails> filteredList = new ArrayList<RouteDetails>();

                    for (int i = 0; i < routeDetailslist.size(); i++) {

                        final String text = routeDetailslist.get(i).getRouteName().toString().toLowerCase();
                        final String text2 = routeDetailslist.get(i).getVehNumber().toString().toLowerCase();
                        if (text.contains(query) || text2.contains(query)) {

                            filteredList.add(routeDetailslist.get(i));
                        }
                    }

                    adapter2 = new RoutesDetailsRecyclerAdapter(context, filteredList);
                    recycler_view.setAdapter(adapter2);
                    adapter2.notifyDataSetChanged();
                    //data set changed
                }
            });
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

                ArrayAdapter<String> depAdaptor = new ArrayAdapter<String>(RoutesDetailsAndInfoActivity.this,
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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(RoutesDetailsAndInfoActivity.this);
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

                //store
                SharedPreferences.Editor editor = getSharedPreferences("RouteDetails", MODE_PRIVATE).edit();
                editor.putString("response", response.toString());
                editor.apply();
                Log.d("RESPONSE", "" + response);


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

                    routeDetailslist.clear();

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
                        details.setShowRfidFlag(showRfidFlag);
                        routeDetailslist.add(details);
                    }

                    //set adapter
                    adapter2 = new RoutesDetailsRecyclerAdapter(RoutesDetailsAndInfoActivity.this, routeDetailslist);
                    recycler_view.setAdapter(adapter2);

                } else {
                    Toast.makeText(context, "Route details not found !", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        } else if (id == R.id.action_refresh) {
//            loadData();
//            return true;
//        } else
        if (id == android.R.id.home) {
            this.startActivity(new Intent(RoutesDetailsAndInfoActivity.this,
                    MainActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void refreshActivity() {
        finish();
        startActivity(getIntent());
    }
}