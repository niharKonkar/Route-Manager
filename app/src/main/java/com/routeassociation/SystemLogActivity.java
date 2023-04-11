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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.routeassociation.adapter.SystemLogRecyAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.SystemLog;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SystemLogActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(SystemLogActivity.this,
                    MainActivity.class));
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Context context;
    private Util util;
    private int usrId, orgId, depId, ugpId;
    private ProgressDialog progDailog;
    private String resSystemLog;
    private String toDate, fromDate;
    private ArrayList<SystemLog> systemLogArrayList;
    private RecyclerView systemLogRecyView;
    private LinearLayout layParent, layNoRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_log);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("System Log");
            toolbar.setTitleTextColor(Color.WHITE);

            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

            }


            //get reference
            context = this;
            util = new Util(context);

            //set white back icon to toolbar
            final Drawable upArrow = getResources().getDrawable(R.drawable.back);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            CheckInternet checkInternet = new CheckInternet();
            boolean checkConnection = checkInternet.checkConnection(context);
            if (checkConnection) {

            } else {
                Toast.makeText(SystemLogActivity.this,
                        "connection not found...plz check connection", Toast.LENGTH_LONG).show();
            }





            layParent = (LinearLayout) findViewById(R.id.layParent);
            layNoRecord = (LinearLayout) findViewById(R.id.layNoRecord);
            systemLogRecyView = (RecyclerView) findViewById(R.id.systemLogRecyView);
            systemLogRecyView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            systemLogRecyView.setLayoutManager(layoutManager);


            //get current date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            toDate = dateFormat.format(new Date());

            String dates[] = toDate.split(" ");
            fromDate = dates[0];
            fromDate = fromDate + " 00:00:00";


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
                ugpId = jsonObject2.getInt("ugpId");
            }

            //load system logs for admin
//            if (ugpId == 3) {
//                depId = 0;
//            }
            LoadSystemLog loadSystemLog = new LoadSystemLog();
            loadSystemLog.execute(null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class LoadSystemLog extends AsyncTask<String, String, String> {
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
                resSystemLog = util.getSystemLogs(fromDate, toDate, orgId, depId);


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


                if (resSystemLog != null) {

                    JSONObject object2 = new JSONObject(resSystemLog);
                    JSONArray array = object2.getJSONArray("data");
                    if (array.length() == 0) {
                        layParent.setVisibility(View.GONE);
                        layNoRecord.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "No system log!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    systemLogArrayList = new ArrayList<SystemLog>();
                    for (int i = 0; i < array.length(); i++) {
                        SystemLog systemLog = new SystemLog();
                        JSONObject object = array.getJSONObject(i);
                        systemLog.setCategory(object.getString("sylCategory"));
                        systemLog.setDescription(object.getString("sylDesc"));
                        systemLog.setTime(object.getString("sylLogDate"));
                        systemLogArrayList.add(systemLog);

                    }


                    //set adapter
                    layParent.setVisibility(View.VISIBLE);
                    layNoRecord.setVisibility(View.GONE);
                    SystemLogRecyAdapter adapter = new SystemLogRecyAdapter(context, systemLogArrayList);
                    systemLogRecyView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } else {
                    layParent.setVisibility(View.GONE);
                    layNoRecord.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "No system log!", Toast.LENGTH_SHORT).show();
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
