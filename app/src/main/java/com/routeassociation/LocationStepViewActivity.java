package com.routeassociation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.routeassociation.adapter.SchoolBusEventsRecyAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.SchoolEventsData;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LocationStepViewActivity extends AppCompatActivity {

    private Button btnMap;
    private  List<SchoolEventsData> locationList;
    private String vehName,routeName;
    private TextView txtRouteName,txtVehNum;
    private Util util;
    private int vehId;
    private LinearLayout layNoRecordFound;
    private ImageView imgSchoolBus;
    private Context context;
    private ProgressDialog progDailog;
    private String response,status;
    private RecyclerView schoolEventsRecyView;
    private JSONArray dataArray;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(LocationStepViewActivity.this,
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

        setContentView(R.layout.activity_location_listview);
        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Route Manager");
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
            CheckInternet checkInternet = new CheckInternet();
            boolean checkConnection= checkInternet.checkConnection(context);
            if (checkConnection) {

            } else {
                Toast.makeText(LocationStepViewActivity.this,
                        "connection not found...plz check connection", Toast.LENGTH_LONG).show();
            }




            //get reference
            btnMap = (Button) findViewById(R.id.btnNavigate);
            txtVehNum = (TextView) findViewById(R.id.txtVehNum);
            txtRouteName = (TextView) findViewById(R.id.txtRouteName);
            layNoRecordFound =(LinearLayout)findViewById(R.id.layNoRecordFound);
            schoolEventsRecyView = (RecyclerView) findViewById(R.id.schoolEventRecyView);
            schoolEventsRecyView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            schoolEventsRecyView.setLayoutManager(layoutManager);
            imgSchoolBus = (ImageView) findViewById(R.id.imgSchoolBus);
            //play gif
            Glide.with(context)
                    .load(R.drawable.school_busg)
                    .asGif()
                    .crossFade()
                    .into(imgSchoolBus);

            //get data
            Intent intent = getIntent();
            if (intent != null) {
                vehName = intent.getStringExtra("vehName");
                routeName = intent.getStringExtra("routeName");
                vehId = intent.getIntExtra("vehId",0);

                txtRouteName.setText("" + routeName);
                txtVehNum.setText("" + vehName);

            }


            //fetch school bus events
            util = new Util(context);
            LocationStepViewActivity.LoadSchoolEventsData data = new LocationStepViewActivity.LoadSchoolEventsData();
            data.execute(null,null);


            //navigate to map
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LocationStepViewActivity.this, LocationDetailsMapsActivity.class);
                    startActivity(intent);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class LoadSchoolEventsData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog  =  new ProgressDialog(context);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();

        }
        @Override
        protected String doInBackground(String... aurl) {

            try {
                response =  util.getSchoolBusEvents(vehId);
                if(response != null) {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object1 = jsonArray.getJSONObject(0);
                    status = object1.getString("status");
                    locationList = new ArrayList<SchoolEventsData>();
                    if (status.equals("SUCCESS")) {
                        JSONObject object2 = jsonArray.getJSONObject(1);

                      dataArray = object2.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject object = dataArray.getJSONObject(i);
                            SchoolEventsData schoolEventsData = new SchoolEventsData();
                            schoolEventsData.setEventName(object.getString("sbeEvent"));
                            schoolEventsData.setEventTimestamp(object.getString("sbeTimeStamp"));
                            schoolEventsData.setEventAlertFlag(object.getString("sbeAlertFlag"));
                            locationList.add(schoolEventsData);

                        }

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            try{
                progDailog.dismiss();
                if(response == null)
                {
                    Toast.makeText(context, "School bus events not found !", Toast.LENGTH_SHORT).show();
                    layNoRecordFound.setVisibility(View.VISIBLE);
                    return;

                }
                if(status.equals("FAILURE"))
                {
                    Toast.makeText(context, "School bus events not found !", Toast.LENGTH_SHORT).show();
                    layNoRecordFound.setVisibility(View.VISIBLE);
                    return;
                }
                if(dataArray.length() == 0)
                {
                    Toast.makeText(context, "School bus events not found !", Toast.LENGTH_SHORT).show();
                    layNoRecordFound.setVisibility(View.VISIBLE);
                    return;
                }


                // set adapter
                SchoolBusEventsRecyAdapter adapter = new SchoolBusEventsRecyAdapter(context,locationList);
                schoolEventsRecyView.setAdapter(adapter);

            }catch (Exception e)
            {
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
