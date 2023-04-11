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

import com.routeassociation.adapter.NotificationHistoryDataRecyAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.NotificationHistoryData;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationHistoryActivity extends AppCompatActivity {
    private RecyclerView notificationRecyView;
    private Util util;
    private ProgressDialog progDailog;
    private Context context;
    private String response, status, params;
    private JSONArray dataArray;
    private int usrId;
    private ArrayList<NotificationHistoryData> notificationHistoryDataArrayList;
    private LinearLayout layNoRecordFound;

    private LinearLayout layNoRecord, layParent;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(NotificationHistoryActivity.this,
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
        setContentView(R.layout.activity_notification_history);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Notification History");
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

                usrId = jsonObject2.getInt("usrId");

            }

            context = this;
            util = new Util(context);
            layParent = (LinearLayout) findViewById(R.id.layParent);
            layNoRecord = (LinearLayout) findViewById(R.id.layNoRecord);

            //check internet
            CheckInternet checkInternet = new CheckInternet();
            boolean checkConnection = checkInternet.checkConnection(context);
            if (checkConnection) {

            } else {
                Toast.makeText(NotificationHistoryActivity.this,
                        "connection not found...plz check connection", Toast.LENGTH_LONG).show();
            }


            notificationRecyView = (RecyclerView) findViewById(R.id.notificationRecyView);
            notificationRecyView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext());
            notificationRecyView.setLayoutManager(layoutManager2);

            //load notifications
            NotificationHistoryActivity.LoadNotificationData notificationData = new NotificationHistoryActivity.LoadNotificationData();
            notificationData.execute(null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class LoadNotificationData extends AsyncTask<String, String, String> {
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
                response = util.getNotificationsDataV2(usrId, 20, "U");
                if (response != null) {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object1 = jsonArray.getJSONObject(0);
                    status = object1.getString("status");
                    notificationHistoryDataArrayList = new ArrayList<NotificationHistoryData>();
                    if (status.equals("SUCCESS")) {
                        JSONObject object2 = jsonArray.getJSONObject(1);
                        dataArray = object2.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject object = dataArray.getJSONObject(i);
                            NotificationHistoryData historyData = new NotificationHistoryData();
                            historyData.setActivity(object.getString("activity"));
                            //    historyData.setAndroidId(object.getInt("androidId"));
                            historyData.setContentInfo(object.getString("contentInfo"));
                            historyData.setDate(object.getString("date"));
                            historyData.setMessage(object.getString("message"));
                            historyData.setModule(object.getString("module"));
                            historyData.setUsrName(object.getString("usrName"));
                            historyData.setSubtext(object.getString("subtext"));
                            notificationHistoryDataArrayList.add(historyData);
                        }

                    }
                }


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
                    layParent.setVisibility(View.GONE);
                    layNoRecord.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Notification data not found !", Toast.LENGTH_SHORT).show();

                    return;

                }
                if (status.equals("FAILURE")) {
                    layParent.setVisibility(View.GONE);
                    layNoRecord.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Notification data not found !", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (notificationHistoryDataArrayList.size() == 0) {
                    layParent.setVisibility(View.GONE);
                    layNoRecord.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Notification data not found !", Toast.LENGTH_SHORT).show();

                    return;
                }


                //set adapter
                layParent.setVisibility(View.VISIBLE);
                layNoRecord.setVisibility(View.GONE);


                NotificationHistoryDataRecyAdapter adapter = new NotificationHistoryDataRecyAdapter(context, notificationHistoryDataArrayList);
                notificationRecyView.setAdapter(adapter);

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
