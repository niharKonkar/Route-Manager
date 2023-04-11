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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.routeassociation.adapter.RealTimeAnalysisDataRecyAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.RealTimePickDropData;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RealTimePickDropAnalysisActivity extends AppCompatActivity {
    private LinearLayout layNoRecord, layParent;
    private RecyclerView analysisRecyView;
    private Util util;
    private ProgressDialog progDailog;
    private Context context;
    private String response, status, params;
    private JSONArray dataArray;
    private int usrId, orgId, depId;
    private String ggdGroupCode = "1A";
    private Spinner groupCodeSpinner;
    private ArrayList<RealTimePickDropData> realTimePickDropDataArrayList;
    private RadioButton rbOk, rbCritical, rbTotal;
    private RadioGroup rgSelect;
    private String selectedStatus = "T";
    private Switch btnSwitch;
    private boolean colorFlag = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(RealTimePickDropAnalysisActivity.this,
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
        setContentView(R.layout.activity_real_time_pick_drop_analysis);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Realtime Analysis");
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
                orgId = jsonObject2.getInt("orgId");
                depId = jsonObject2.getInt("depId");
            }

            context = this;
            util = new Util(context);
            layParent = (LinearLayout) findViewById(R.id.layParent);
            layNoRecord = (LinearLayout) findViewById(R.id.layNoRecord);
            groupCodeSpinner = (Spinner) findViewById(R.id.groupCodeSpinner);
            rbCritical = (RadioButton) findViewById(R.id.rbCritical);
            rbOk = (RadioButton) findViewById(R.id.rbOk);
            rbTotal = (RadioButton) findViewById(R.id.rbTotal);
            rgSelect = (RadioGroup) findViewById(R.id.rgSelect);
            btnSwitch = (Switch)findViewById(R.id.btnSwitch);

            ArrayList<String> groupCodeList = new ArrayList<String>();
            groupCodeList.add("1A");
            groupCodeList.add("1B");
            groupCodeList.add("2A");
            groupCodeList.add("2B");
            groupCodeList.add("3A");
            groupCodeList.add("3B");
            groupCodeList.add("4A");
            groupCodeList.add("4B");
            groupCodeList.add("5A");
            groupCodeList.add("5B");

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_list_item_1,
                    groupCodeList);

            groupCodeSpinner.setAdapter(arrayAdapter);

            groupCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ggdGroupCode = groupCodeSpinner.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            analysisRecyView = (RecyclerView) findViewById(R.id.analysisRecyView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            analysisRecyView.setLayoutManager(layoutManager);


            //check internet
            CheckInternet checkInternet = new CheckInternet();
            boolean checkConnection = checkInternet.checkConnection(context);
            if (checkConnection) {

            } else {
                Toast.makeText(RealTimePickDropAnalysisActivity.this,
                        "connection not found...plz check connection", Toast.LENGTH_LONG).show();
            }


            rgSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.rbOk) {
                        selectedStatus = "O";
                       /* if (ggdGroupCode != null) {
                            RealTimePickDropAnalysisActivity.LoadRealTimeAnalysisData timeAnalysisData = new RealTimePickDropAnalysisActivity.LoadRealTimeAnalysisData();
                            timeAnalysisData.execute(null, null);
                        }*/
                    } else if (checkedId == R.id.rbCritical) {
                        selectedStatus = "C";
                       /* if (ggdGroupCode != null) {
                            RealTimePickDropAnalysisActivity.LoadRealTimeAnalysisData timeAnalysisData = new RealTimePickDropAnalysisActivity.LoadRealTimeAnalysisData();
                            timeAnalysisData.execute(null, null);
                        }*/
                    } else if (checkedId == R.id.rbTotal) {
                        selectedStatus = "T";
                       /* if (ggdGroupCode != null) {
                            RealTimePickDropAnalysisActivity.LoadRealTimeAnalysisData timeAnalysisData = new RealTimePickDropAnalysisActivity.LoadRealTimeAnalysisData();
                            timeAnalysisData.execute(null, null);
                        }*/
                    }

                }
            });

            //switch
            btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(btnSwitch.isChecked())
                    {
                        colorFlag = true;

                    }else {
                        colorFlag =  false;
                    }
                }
            });

            Button btnOk = (Button) findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //load data
                    RealTimePickDropAnalysisActivity.LoadRealTimeAnalysisData timeAnalysisData = new RealTimePickDropAnalysisActivity.LoadRealTimeAnalysisData();
                    timeAnalysisData.execute(null, null);
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class LoadRealTimeAnalysisData extends AsyncTask<String, String, String> {
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
                if (response != null) {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object1 = jsonArray.getJSONObject(0);
                    status = object1.getString("status");
                    realTimePickDropDataArrayList = new ArrayList<RealTimePickDropData>();
                    if (status.equals("SUCCESS")) {
                        JSONObject object2 = jsonArray.getJSONObject(1);
                        dataArray = object2.getJSONArray("data");


                        for (int i = 0; i < dataArray.length(); i++) {

                            JSONObject object = dataArray.getJSONObject(i);
                            RealTimePickDropData realTimePickDropData = new RealTimePickDropData();

                            if (selectedStatus.equals("T")) {
                                realTimePickDropData.setGeofenceName(object.getString("geofenceName"));
                                realTimePickDropData.setLocation(object.getString("location"));
                                realTimePickDropData.setRouteName(object.getString("routeName"));
                                realTimePickDropData.setTimestamp(object.getString("timestamp"));
                                realTimePickDropData.setVehicleNumber(object.getString("vehicleNumber"));
                                realTimePickDropData.setStatus(object.getString("status"));

                                realTimePickDropDataArrayList.add(realTimePickDropData);
                            } else if (selectedStatus.equals("O"))

                            {
                                if (object.getString("status").equals("OK")) {
                                    realTimePickDropData.setGeofenceName(object.getString("geofenceName"));
                                    realTimePickDropData.setLocation(object.getString("location"));
                                    realTimePickDropData.setRouteName(object.getString("routeName"));
                                    realTimePickDropData.setTimestamp(object.getString("timestamp"));
                                    realTimePickDropData.setVehicleNumber(object.getString("vehicleNumber"));
                                    realTimePickDropData.setStatus(object.getString("status"));

                                    realTimePickDropDataArrayList.add(realTimePickDropData);
                                }
                            } else if (selectedStatus.equals("C")) {
                                if (object.getString("status").equals("CRITICAL")) {
                                    realTimePickDropData.setGeofenceName(object.getString("geofenceName"));
                                    realTimePickDropData.setLocation(object.getString("location"));
                                    realTimePickDropData.setRouteName(object.getString("routeName"));
                                    realTimePickDropData.setTimestamp(object.getString("timestamp"));
                                    realTimePickDropData.setVehicleNumber(object.getString("vehicleNumber"));
                                    realTimePickDropData.setStatus(object.getString("status"));

                                    realTimePickDropDataArrayList.add(realTimePickDropData);
                                }
                            }

                        }

                        //set adapter

                        if (realTimePickDropDataArrayList.size() != 0) {
                            layParent.setVisibility(View.VISIBLE);
                            layNoRecord.setVisibility(View.GONE);

                            if(colorFlag == false) {


                                rbOk.setText("Ok Count : " + object2.getString("okCount"));
                                rbOk.setTextColor(getResources().getColor(R.color.green));
                                rbCritical.setText("Critical Count : " + object2.getString("criticalCount"));
                                rbCritical.setTextColor(getResources().getColor(R.color.red));
                                rbTotal.setText("Total Count : " + object2.getString("totalCount"));
                            }else {
                                rbOk.setText("Ok Count : " + object2.getString("okCount"));
                                rbOk.setTextColor(getResources().getColor(R.color.red));
                                rbCritical.setText("Critical Count : " + object2.getString("criticalCount"));
                                rbCritical.setTextColor(getResources().getColor(R.color.green));
                                rbTotal.setText("Total Count : " + object2.getString("totalCount"));

                            }


                            RealTimeAnalysisDataRecyAdapter adapter = new RealTimeAnalysisDataRecyAdapter(context, realTimePickDropDataArrayList,colorFlag,selectedStatus);
                            analysisRecyView.setAdapter(adapter);
                        } else {
                            Toast.makeText(context, "Analysis data not found", Toast.LENGTH_SHORT).show();
                            layNoRecord.setVisibility(View.VISIBLE);
                            layParent.setVisibility(View.GONE);
                            return;
                        }


                    } else {
                        Toast.makeText(context, "Analysis data not found", Toast.LENGTH_SHORT).show();
                        layNoRecord.setVisibility(View.VISIBLE);
                        layParent.setVisibility(View.GONE);
                        return;
                    }
                } else {
                    Toast.makeText(context, "Analysis data not found", Toast.LENGTH_SHORT).show();
                    layNoRecord.setVisibility(View.VISIBLE);
                    layParent.setVisibility(View.GONE);
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                response = util.getRealTimeAnalysisData(orgId, depId, ggdGroupCode);
                return response;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
