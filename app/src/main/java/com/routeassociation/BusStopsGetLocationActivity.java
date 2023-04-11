package com.routeassociation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;
import com.routeassociation.pojo.BusStop;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusStopsGetLocationActivity extends AppCompatActivity {

    private EditText latitude, longitude, userAddress;
    private ArrayList<BusStop> busStopArrayList;
    String response;
    private ProgressDialog progDailog;
    private Context context;
    private Util util;
    private Integer orgId;
    private Integer depId;
    private double lat = 0.0, lng = 0.0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(BusStopsGetLocationActivity.this,
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
        setContentView(R.layout.activity_bus_stops_get_location);

        context = this;
        util = new Util(context);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("View Bus Stops");
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

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        userAddress = findViewById(R.id.userAddress);

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
    }

    public void proceedToGetBusStop(View view){

        if(userAddress.getText().toString().equals("")){
            Toast.makeText(context, "Please don't leave any field blank.", Toast.LENGTH_SHORT).show();
        }else {
//            lat = Double.parseDouble(latitude.getText().toString());
//            lng = Double.parseDouble(longitude.getText().toString());
            LatLng latLng = getLocationFromAddress(userAddress.getText().toString());

            if(latLng != null){
                lat = latLng.latitude;
                lng = latLng.longitude;
                new LoadBusStopData().execute();
            }else {
                Toast.makeText(context, "Could not get latitude and longitude for your address.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class LoadBusStopData extends AsyncTask<String, String, String> {
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
                response = util.getNearestBusStops(orgId, depId, lat, lng);

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

                busStopArrayList = new ArrayList<>();
                JSONArray jsonArray;
                if (response != null) {
                    jsonArray = new JSONArray(response);
                    JSONObject object1 = jsonArray.getJSONObject(0);
                    String status = object1.getString("status");

                    if (status.equals("SUCCESS")) {
                        JSONObject objectData = jsonArray.getJSONObject(1);

                        JSONArray dataArray1 = objectData.getJSONArray("data");

                        for (int i = 0; i < dataArray1.length(); i++) {
                            JSONObject object = dataArray1.getJSONObject(i);
                            BusStop busStop = new BusStop();
                            busStop.setBusStopName(object.getString("name"));
                            busStop.setBusStopLat(object.getDouble("lat"));
                            busStop.setBusStopLng(object.getDouble("lng"));
                            double distance = object.getDouble("distance");
                            busStop.setDistance(Math.round(distance * 100D) / 100D);

                            busStopArrayList.add(busStop);
                        }
                    }
                }

                if(busStopArrayList.size() >0){

                    Intent intent = new Intent(BusStopsGetLocationActivity.this, BusStopsMapsActivity.class);
                    Gson gson = new Gson();
                    String json = gson.toJson(busStopArrayList);
                    intent.putExtra("busStopArray", json);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    startActivity(intent);

                }else {
                    Toast.makeText(context, "There are no bus stops near your entered location.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Barcode.GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            if(location.hasLatitude() && location.hasLatitude()){
                return new LatLng(location.getLatitude(), location.getLongitude());
            }

            return null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
