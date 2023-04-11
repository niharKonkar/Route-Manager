package com.routeassociation;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.util.Util;


import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LatLng mLatLng;
    private int vehId;
    private String mVehNumber;
    private String mLocation;
    private Context context;
    private Timer timer;
    private String response;
    private double lat, lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        try {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            // Main Code
            context = getApplicationContext();
            // Check Network State
            CheckInternet checkInternet = new CheckInternet();
            boolean checkConnection = checkInternet.checkConnection(context);
            if (checkConnection) {

            } else {
                Toast.makeText(MapsActivity.this,
                        "connection not found...plz check connection", Toast.LENGTH_LONG).show();
            }
            // Get Parameters

            Intent i = getIntent();
            lat = i.getDoubleExtra("lat", 0);
            lng = i.getDoubleExtra("lng", 0);

            mLocation = i.getStringExtra("address");
            vehId = i.getIntExtra("vehId", 0);
            mVehNumber = "" + vehId;
            mLatLng = new LatLng(lat, lng);

            Toast.makeText(this, mVehNumber + " | " + mLocation, Toast.LENGTH_LONG).show();

            //setUpMapIfNeeded();

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateData();
                }
            }, 30000, 30000);//put here time 1000 milliseconds=1 second


        } catch (Exception e) {
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
        } catch (Exception e) {
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

        } catch (Exception e) {
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
                                                  updateData();
                                              }
                                          },
                        30000, 30000);//put here time 1000 milliseconds=1 second

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateData() {
        if (networkState() == false) {
            Toast.makeText(this, "No Internet Connection !!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            new MapsActivity.MapUpdateTask().execute(null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean networkState() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnected()
                || manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnected()) {
            // connected to a network
            return true;
        }
        return false;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add a marker in Sydney and move the camera
        mMap.setTrafficEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
        mMap.addMarker(new MarkerOptions().position(mLatLng).title(mVehNumber + " | " + mLocation));

    }

    private class MapUpdateTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject obj = null;
            try {
                Util util = new Util(context);
                response = util.getLatestLocationByVehId(vehId);
                if (response == null) {
                    return null;
                }
            } catch (Exception e) {

            }

            return obj;

        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            try {

                if (response == null) {
                    Toast.makeText(context, "Vehicle location not found !", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject object = new JSONObject(response);
                String status = object.getString("status");
                if (status.equals("SUCCESS")) {
                    lat = object.getDouble("lat");
                    lng = object.getDouble("lng");
                    mLocation = object.getString("address");

                } else {
                    return;
                }

                mMap.clear();
                mLatLng = new LatLng(lat, lng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 19));
                mMap.addMarker(new MarkerOptions().position(mLatLng).title(mVehNumber + " | " + mLocation));
                mMap.setTrafficEnabled(true);
                Toast.makeText(context, "Location updated \n" + mVehNumber + " | " + mLocation, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
