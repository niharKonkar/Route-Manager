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
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.adapter.RouteAnalysisRecyAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.RouteAnalysis;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RouteAnalysisDetailsActivity extends AppCompatActivity {

    private RecyclerView routeDetailsRecyView;
    private Context context;
    private Util util;
    private ProgressDialog progDailog;
    private String params, response;
    private int usrId, routeId, orgId, depId;
    private LinearLayout layParent, layNoRecord;
    private TextView txtRouteName, txtTimings;
    private ArrayList<RouteAnalysis> routeAnalysisArrayList;
    private String pickDropRouteAnalysis = "P";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(RouteAnalysisDetailsActivity.this,
                    RoutesDetailsAndInfoActivity.class));
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
        setContentView(R.layout.activity_route_details);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Route Analysis");
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

            //check internet connection
            CheckInternet checkInternet = new CheckInternet();
            boolean checkConnection = checkInternet.checkConnection(context);
            if (checkConnection) {

            } else {
                Toast.makeText(context,
                        "Please check your internet connection or try again later!", Toast.LENGTH_LONG).show();
            }

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

                usrId = jsonObject2.getInt("usrId");
                orgId = jsonObject2.getInt("orgId");
                depId = jsonObject2.getInt("depId");

            }
            txtRouteName = (TextView) findViewById(R.id.txtRouteName);
            Intent intent = getIntent();
            if (intent != null) {
                routeId = intent.getIntExtra("routeId", 0);
                String routeName = intent.getStringExtra("routeName");
                txtRouteName.setText("" + routeName);
                pickDropRouteAnalysis = intent.getStringExtra("pickDropRouteAnalysis");
            }


            txtTimings = (TextView) findViewById(R.id.txtTimings);
            layParent = (LinearLayout) findViewById(R.id.layParent);
            layNoRecord = (LinearLayout) findViewById(R.id.layNoRecord);
            routeDetailsRecyView = (RecyclerView) findViewById(R.id.routeDetailsRecyView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            routeDetailsRecyView.setLayoutManager(layoutManager);

            //load route analysis
            RouteAnalysisDetailsActivity.LoadRouteAnalysisDetails loadRouteAnalysisDetails = new RouteAnalysisDetailsActivity.LoadRouteAnalysisDetails();
            loadRouteAnalysisDetails.execute(null, null);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class LoadRouteAnalysisDetails extends AsyncTask<String, String, String> {
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
                response = util.getRouteAnalysis(orgId, depId, routeId, pickDropRouteAnalysis);
                return response;

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
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object1 = jsonArray.getJSONObject(0);
                    String status = object1.getString("status");
                    if (status.equals("SUCCESS")) {

                        JSONObject object2 = jsonArray.getJSONObject(1);
                        String timings = object2.getString("timestring");
                        txtTimings.setText("" + Html.fromHtml(timings));
                        JSONArray array = object2.getJSONArray("data");
                        routeAnalysisArrayList = new ArrayList<RouteAnalysis>();
                        for (int i = 0; i < array.length(); i++) {
                            RouteAnalysis routeAnalysis = new RouteAnalysis();
                            JSONObject object = array.getJSONObject(i);
                            routeAnalysis.setGplActualTime(object.getString("gplActualTime"));
                            routeAnalysis.setGplTime(object.getString("gplTime"));
                            routeAnalysis.setGplName(object.getString("gplName"));
                            routeAnalysis.setGplActualTimeOut(object.getString("gplActualTimeOut"));
                            routeAnalysis.setGplMultipleTimings(object.getString("gplMultipleTimings"));
                            routeAnalysis.setGplColourCode(object.getString("gplColourCode"));


                            routeAnalysisArrayList.add(routeAnalysis);

                        }
                        //set adapter
                        layParent.setVisibility(View.VISIBLE);
                        layNoRecord.setVisibility(View.GONE);
                        RouteAnalysisRecyAdapter adapter = new RouteAnalysisRecyAdapter(context, routeAnalysisArrayList);
                        routeDetailsRecyView.setAdapter(adapter);


                    } else {
                        layParent.setVisibility(View.GONE);
                        layNoRecord.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "Route analysis data not found !", Toast.LENGTH_SHORT).show();
                        return;

                    }

                } else {

                    layParent.setVisibility(View.GONE);
                    layNoRecord.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Route analysis data not found !", Toast.LENGTH_SHORT).show();
                    return;
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
