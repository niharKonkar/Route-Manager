package com.routeassociation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.routeassociation.adapter.TimelineDataRecyAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.RouteDetails;
import com.routeassociation.pojo.SchoolTimelineData;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TimelineActivity extends AppCompatActivity {
    private Util util;
    private int orgId,depId;
    private String sbeAlertFlag="A";
    private String response;
    private ProgressDialog progDailog;
    private Context context;
    private ArrayList<SchoolTimelineData> timelineDataArrayList ;
    private RecyclerView timelineRecyView;
    private LinearLayout layNoRecordFound,layRecyView;
    private String params;
    private SwipeRefreshLayout swipeView;
    private Timer timer;
    private ArrayList<RouteDetails> routeDetailsArrayList;
    private TimelineActivity.LoadTimelineData loadTimelineData;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(TimelineActivity.this,
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
        setContentView(R.layout.activity_timeline);
        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Timeline");
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
                depId= jsonObject2.getInt("depId");

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


            swipeView =(SwipeRefreshLayout) findViewById(R.id.swipeView);
            timelineRecyView = (RecyclerView) findViewById(R.id.timelineRecyView);
            layNoRecordFound = (LinearLayout) findViewById(R.id.layNoRecordFound);
            layRecyView =(LinearLayout)findViewById(R.id.layRecyView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            timelineRecyView.setLayoutManager(layoutManager);
            layRecyView.setVisibility(View.VISIBLE);
            layNoRecordFound.setVisibility(View.GONE);

            swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    TimelineActivity.LoadTimelineData timelineData = new TimelineActivity.LoadTimelineData();
                    timelineData.execute(null, null);
                    swipeView.setRefreshing(false);
                }
            });

            //load the timeline data
            TimelineActivity.LoadTimelineData timelineData = new TimelineActivity.LoadTimelineData();
            timelineData.execute(null, null);

            //auto refresh
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    new LoadTimelineData().execute();
                }
            }, 30000, 30000);//put here time 1000 milliseconds=1 second

        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            //   mapUpdateTask.cancel(true);
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (timer == null) {

                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        new LoadTimelineData().execute();
                    }
                }, 30000, 30000);//put here time 1000 milliseconds=1 second
            }
            } catch(Exception e){
                e.printStackTrace();
            }


    }

    private class LoadTimelineData extends AsyncTask<String,String,String>
    {
      /*  @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(context);
            progDailog.setMessage("Loading...");
            progDailog.setCancelable(false);
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.show();
        }
*/
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // progDailog.dismiss();

            try {
                if (response != null) {


                    SharedPreferences prefs = getSharedPreferences("RouteDetails", MODE_PRIVATE);
                    String resRouteDetails = prefs.getString("response", null);
                    if (resRouteDetails != null) {
                        routeDetailsArrayList = new ArrayList<RouteDetails>();
                        JSONArray array = new JSONArray(resRouteDetails);
                        for (int i = 0; i < array.length(); i++) {

                            RouteDetails details = new RouteDetails();

                            JSONObject jsonObject = array.getJSONObject(i);
                            details.setRouteId(jsonObject.getInt("routeId"));
                            details.setRouteName(jsonObject.getString("routeName"));
                            details.setVehId(jsonObject.getInt("vehId"));
                            details.setVehNumber(jsonObject.getString("vehNumber"));
                            details.setDriverName( jsonObject.getString("driverName"));
                            details.setDriverId(jsonObject.getString("driverId"));
                            details.setConductorName(jsonObject.getString("conductorName"));
                            details.setDriverNumber(jsonObject.getString("driverNumber"));
                            details.setConductorNumber(jsonObject.getString("conductorNumber"));

                            routeDetailsArrayList.add(details);
                        }

                    }



                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object1= jsonArray.getJSONObject(0);
                    String status =  object1.getString("status");
                    if(status.equals("SUCCESS"))
                    {
                        timelineDataArrayList = new ArrayList<SchoolTimelineData>();
                        JSONObject object2 = jsonArray.getJSONObject(1);
                        JSONArray array = object2.getJSONArray("data");
                        if(array.length()==0)
                        {
                            Toast.makeText(context, "Timeline data not found !", Toast.LENGTH_SHORT).show();
                            layRecyView.setVisibility(View.GONE);
                            layNoRecordFound.setVisibility(View.VISIBLE);

                            return;
                        }
                        for(int i=0;i<array.length();i++)
                        {
                            JSONObject object = array.getJSONObject(i);
                            SchoolTimelineData timelineData = new SchoolTimelineData();
                            timelineData.setVehId(object.getInt("vehId"));
                            timelineData.setGegId(object.getInt("gegId"));
                            timelineData.setSbeEvent(object.getString("sbeEvent"));
                            timelineData.setSbeEventType(object.getString("sbeEventType"));
                            timelineData.setSbeTimestamp(object.getString("sbeTimeStamp"));
                            timelineData.setRouteDetailsArrayList(routeDetailsArrayList);
                            timelineDataArrayList.add(timelineData);
                        }

                        if(timelineDataArrayList.size()==0)
                        {
                            Toast.makeText(context, "Timeline data not found !", Toast.LENGTH_SHORT).show();
                            layRecyView.setVisibility(View.GONE);
                            layNoRecordFound.setVisibility(View.VISIBLE);
                            return;

                        }

                        //set adapter
                        Toast.makeText(context, "Timeline updated!", Toast.LENGTH_SHORT).show();

                        TimelineDataRecyAdapter adapter = new TimelineDataRecyAdapter(context,timelineDataArrayList);
                        timelineRecyView.setAdapter(adapter);




                    }else {
                        Toast.makeText(context, "Timeline data not found !", Toast.LENGTH_SHORT).show();
                        layRecyView.setVisibility(View.GONE);
                        layNoRecordFound.setVisibility(View.VISIBLE);

                        return;
                    }
                } else {
                    Toast.makeText(context, "Timeline data not found !", Toast.LENGTH_SHORT).show();
                    layRecyView.setVisibility(View.GONE);
                    layNoRecordFound.setVisibility(View.VISIBLE);

                    return;
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try{
                response = util.getTimelineData(orgId,depId,sbeAlertFlag);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
