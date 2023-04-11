package com.routeassociation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.routeassociation.pojo.CameraData;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CameraStatusActivity extends AppCompatActivity {


    private ProgressDialog progDailog;
    private Context context;
    private String response;
    private String status;
    private Util util;
    private Integer orgId;
    private Integer depId;
    private List<CameraData> cameraStatusList = new ArrayList<>();
    private List<Date> contentDateList = new ArrayList<>();
    private SwipeRefreshLayout swipeLay;

    private TextView tvGreenZone;
    private TextView tvYellowZone;
    private TextView tvRedZone;

    private TextView tvGreenLabel;
    private TextView tvYellowLabel;
    private TextView tvRedLabel;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(CameraStatusActivity.this,
                    MainActivity.class));
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_status);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Camera Status");
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

        util = new Util(this);
        context = this;

        swipeLay = findViewById(R.id.swipeLay);

        tvGreenZone = (TextView)findViewById(R.id.tvGreen);
        tvYellowZone = (TextView)findViewById(R.id.tvYellow);
        tvRedZone = (TextView)findViewById(R.id.tvRed);

        tvGreenLabel = (TextView)findViewById(R.id.tvGreenLabel);
        tvYellowLabel = (TextView)findViewById(R.id.tvYellowLabel);
        tvRedLabel = (TextView)findViewById(R.id.tvRedLabel);

        //load login details
        try {
            SharedPreferences loginDetails = getSharedPreferences("user", 0);
            String params = loginDetails.getString("params", null);

            JSONArray jsonArray = new JSONArray(params);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String status = jsonObject.getString("status");
            if (status.equals("SUCCESS")) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);

                //usrId = jsonObject2.getInt("usrId");
                //ugpId = jsonObject2.getInt("ugpId");
                orgId = jsonObject2.getInt("orgId");
                depId = jsonObject2.getInt("depId");
                //rasId = 0;

            }
        }catch(Exception e)
        {

        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
////        fab.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
////            }
////        });

        //refresh on swipe down
        swipeLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (swipeLay.isRefreshing()) {
                    swipeLay.setRefreshing(false);
                }
                // Call Webservice
                LoadCameraStatusData loadCameraStatusData = new LoadCameraStatusData();
                loadCameraStatusData.execute(null,null,null);
            }
        });


        // Call Webservice
        LoadCameraStatusData loadCameraStatusData = new LoadCameraStatusData();
        loadCameraStatusData.execute(null,null,null);
    }



    class LoadCameraStatusData extends AsyncTask<String, String, String> {
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
                response = util.getCameraAnalysisData(orgId, depId);

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

                cameraStatusList = new ArrayList<CameraData>();
                final StringBuilder green = new StringBuilder();
                final StringBuilder red = new StringBuilder();
                final StringBuilder yellow = new StringBuilder();
                JSONArray jsonArray;
                if (response != null) {
                    jsonArray = new JSONArray(response);
                    JSONObject object1 = jsonArray.getJSONObject(0);
                    status = object1.getString("status");

                    if (status.equals("SUCCESS")) {
                        JSONObject objectData = jsonArray.getJSONObject(1);

                        // Green Data
                        JSONArray dataArray1 = objectData.getJSONArray("data_green");
                        JSONArray dataArray = sortJsonArray(dataArray1);
                        final CameraData greenData = new CameraData();
                        greenData.setCameraClass("GREEN");
                        greenData.setCount(dataArray.length());

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject object = dataArray.getJSONObject(i);
                            StringBuilder sb = new StringBuilder();
                            sb.append(object.get("vehNumber")).append(" (");
                            sb.append(object.get("camera")).append(") ");
                            sb.append(" " + object.get("vehCameraFileDate1"));
                            sb.append(" " +object.get("vehCameraDownloadLocation"));
                            sb.append("<br>Downloaded Date : ".concat(object.get("vehCameraDownloadDate1").toString()));
                            sb.append(object.toString().contains("vehCameraUpdatedDate1") ? "<br>Last Updated Date : ".concat(object.get("vehCameraUpdatedDate1").toString()) : "");
                            sb.append("<br>");

                            greenData.getCameraList().add(sb.toString());
                            green.append(sb.toString() + "<br/>");

                        }
                        cameraStatusList.add(greenData);

                        // Yellow Data
                        dataArray1 = objectData.getJSONArray("data_yellow");
                        dataArray = sortJsonArray(dataArray1);
                        final CameraData yellowData = new CameraData();
                        yellowData.setCameraClass("YELLOW");
                        yellowData.setCount(dataArray.length());

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject object = dataArray.getJSONObject(i);
                            StringBuilder sb = new StringBuilder();
                            sb.append(object.get("vehNumber")).append(" (");
                            sb.append(object.get("camera")).append(") ");
                            sb.append(" " + object.get("vehCameraFileDate1"));
                            sb.append(" " +object.get("vehCameraDownloadLocation"));
                            sb.append("<br>Downloaded Date : ".concat(object.get("vehCameraDownloadDate1").toString()));
                            sb.append(object.toString().contains("vehCameraUpdatedDate1") ? "<br>Last Updated Date : ".concat(object.get("vehCameraUpdatedDate1").toString()) : "");
                            sb.append("<br>");

                            yellowData.getCameraList().add(sb.toString());
                            yellow.append(sb.toString() + "<br/>");

                        }
                        cameraStatusList.add(yellowData);

                        // Red Data
                        dataArray1 = objectData.getJSONArray("data_red");
                        dataArray = sortJsonArray(dataArray1);
                        final CameraData redData = new CameraData();
                        redData.setCameraClass("RED");
                        redData.setCount(dataArray.length());

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject object = dataArray.getJSONObject(i);
                            StringBuilder sb = new StringBuilder();
                            sb.append(object.get("vehNumber")).append(" (");
                            sb.append(object.get("camera")).append(") ");
                            sb.append(" " + object.get("vehCameraFileDate1"));
                            sb.append(" " + object.get("vehCameraDownloadLocation"));
                            sb.append("<br>Downloaded Date : ".concat(object.get("vehCameraDownloadDate1").toString()));
                            sb.append(object.toString().contains("vehCameraUpdatedDate1") ? "<br>Last Updated Date : ".concat(object.get("vehCameraUpdatedDate1").toString()) : "");
                            sb.append("<br>");

                            redData.getCameraList().add(sb.toString());
                            red.append(sb.toString() + "<br/>");

                        }
                        cameraStatusList.add(redData);

                        runOnUiThread(new Runnable() {
                            public void run() {

                                tvGreenLabel.setText("GREEN ZONE " + greenData.getCount().toString());
                                tvRedLabel.setText("RED ZONE " + redData.getCount().toString());
                                tvYellowLabel.setText("YELLOW ZONE " + yellowData.getCount().toString());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                    tvGreenZone.setText(Html.fromHtml(green.toString(), Html.FROM_HTML_MODE_COMPACT));
                                    tvYellowZone.setText(Html.fromHtml(yellow.toString(), Html.FROM_HTML_MODE_COMPACT));
                                    tvRedZone.setText(Html.fromHtml(red.toString(), Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    tvGreenZone.setText(Html.fromHtml(green.toString()));
                                    tvYellowZone.setText(Html.fromHtml(yellow.toString()));
                                    tvRedZone.setText(Html.fromHtml(red.toString()));

                                }
                            }});
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JSONArray sortJsonArray(JSONArray jsonArray){
        final List<JSONObject> jsonValues = new ArrayList<>();
        final List<JSONObject> jsonValuesWithoutDates = new ArrayList<>();
        JSONArray sortedJsonArray = new JSONArray();

        try{
            for (int i = 0; i < jsonArray.length(); i++) {
                if(jsonArray.getJSONObject(i).get("vehCameraDownloadDate1").equals("-")){
                    jsonValuesWithoutDates.add(jsonArray.getJSONObject(i));
                }else{
                    jsonValues.add(jsonArray.getJSONObject(i));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "vehCameraDownloadDate1";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                Date valA = new Date();
                Date valB = new Date();

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    valA = simpleDateFormat.parse(a.get(KEY_NAME).toString());
                    valB = simpleDateFormat.parse(b.get(KEY_NAME).toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                return valB.compareTo(valA);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonValues.size(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        if(!jsonValuesWithoutDates.isEmpty()){
            for (int i = 0; i < jsonValuesWithoutDates.size(); i++) {
                sortedJsonArray.put(jsonValuesWithoutDates.get(i));
            }
        }
        return sortedJsonArray;
    }
}
