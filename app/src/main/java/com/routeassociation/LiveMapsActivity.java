package com.routeassociation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.EtaData;
import com.routeassociation.util.Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LiveMapsActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng mLatLng;
    private String mVehNumber;
    private String mLocation;
    private Context context;
    private Timer timer;
    private Integer mVehId;
    private TextView locationDetails;
    private ImageView imgRefreshMap;

    private boolean etaEnabled = false;

    // ETA
    private static final long INTERVAL = 1000 * 30;
    private static final long FASTEST_INTERVAL = 1000 * 15;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9999;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private EtaData etaData;
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid"};
    private boolean boolIsTypeSet = false;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void showMapTypeSelectorDialog() {

        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 0:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                boolIsTypeSet = true;
                                break;
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                boolIsTypeSet = true;
                                break;

                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_maps);


        // Getting Current Application Context
        context = getApplicationContext();

        //check internet connection
        CheckInternet checkInternet = new CheckInternet();
        boolean checkConnection = checkInternet.checkConnection(context);
        if (checkConnection) {

        } else {
            Toast.makeText(context,
                    "Please check your internet connection or try again later!", Toast.LENGTH_LONG).show();
        }


        // Refresh Map Option
        imgRefreshMap = (ImageView) findViewById(R.id.imgRefreshMap);
        imgRefreshMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMapTypeSelectorDialog();
            }
        });


        // Set Location Details
        locationDetails = (TextView) findViewById(R.id.locationDetails);
        locationDetails.setText("eTechSchool - Let's Build Smart Schools !!");


        // Process GPS only if eta is enabled
        if (etaEnabled == true) {

            // Check if GPS is on
            try {
                int off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                if (off == 0) {
                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(onGPS);

                }
            } catch (Exception e) {
                Toast.makeText(context, "Please check your GPS settings.", Toast.LENGTH_LONG).show();
            }


            createLocationRequest();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        // Check Network State
        if (networkState() == false) {
            Toast.makeText(this, "No Internet Connection !!", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        // Get Intent Parameters

        Intent i = getIntent();
        Bundle b = i.getBundleExtra("data");
        String lat = b.getString("lat");
        String lng = b.getString("lng");
        mVehNumber = b.getString("vehNumber");
        mLocation = b.getString("location");
        mLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        mVehId = b.getInt("vehId");

        Toast.makeText(this, mVehNumber + " | " + mLocation, Toast.LENGTH_LONG).show();

        // Setup Map
        setUpMapIfNeeded();

        // Timer Setup for Refreshing the data
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateData();
            }
        }, 10000, 15000);//put here time 1000 milliseconds=1 second

        // Execute Eta Only if etaEnables flag is true
        if (etaEnabled == true) {
            new EtaUpdateTask().execute();
        }

    }

    private void updateData() {
        // Check for the internet connectivity
        if (networkState() == false) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "No Internet Connection !!", Toast.LENGTH_LONG).show();
                }});
            return;
        }

        // Exeucte Map Update on Thread
        try {
            new MapUpdateTask().execute();

            // If eta is enabled, execute Eta Update Task
            if (etaEnabled == true) {
                new EtaUpdateTask().execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                return;
            }
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(this,"GPS Location Permission Denied !!", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }catch(Exception e)
        {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        }catch(Exception e)
        {

        }
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class EtaUpdateTask extends AsyncTask<Void, Void, EtaData> {

        private EtaData getDistanceInfo(double lat1, double lng1, double lat2, double lng2) {

            EtaData etaData = new EtaData();

            StringBuilder stringBuilder = new StringBuilder();
            Double dist = 0.0;
            Double durationNumber = 0.0;
            String durationText = "";
            try {
                String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lng1 + "&destination=" + lat2 + "," + lng2  + "&mode=driving&sensor=false";

                HttpPost httppost = new HttpPost(url);

                HttpClient client = new DefaultHttpClient();
                HttpResponse response;
                stringBuilder = new StringBuilder();


                response = client.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject = new JSONObject(stringBuilder.toString());

                JSONArray array = jsonObject.getJSONArray("routes");

                JSONObject routes = array.getJSONObject(0);

                JSONArray legs = routes.getJSONArray("legs");

                JSONObject steps = legs.getJSONObject(0);

                JSONObject distance = steps.getJSONObject("distance");

                JSONObject duration = steps.getJSONObject("duration");

                Log.i("Distance", distance.toString());
                dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

                durationText = duration.getString("text");

                etaData.setDistance(dist.toString());
                etaData.setTime(durationText);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return etaData;
        }

        @Override
        protected EtaData doInBackground(Void... params) {
            EtaData etaDataLocal = null;
            try {
                etaDataLocal = getDistanceInfo(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude(),mLatLng.latitude,mLatLng.longitude);


            } catch (Exception e) {

            }

            return etaDataLocal;

        }

        @Override
        protected void onPostExecute(EtaData etaDataLocal) {
            try{
                etaData = etaDataLocal;
               // Toast.makeText(context, etaData.getTime(), Toast.LENGTH_LONG).show();

                locationDetails.setText("School Bus is " + etaDataLocal.getTime() + " [" + etaDataLocal.getDistance() +" Km(s)]" + " away.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MapUpdateTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject obj = null;
            try {
                Util util = new Util(context);
                obj = util.getLiveDataForVehicle(mVehId);
                if (obj.getString("status").equals("FAILURE")) {
                    return null;
                }
            } catch (Exception e) {

            }

            return obj;

        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            try {
                mMap.clear();
                mLatLng = new LatLng(Double.parseDouble(obj.getString("lat")), Double.parseDouble(obj.getString("lng")));
                mLocation = obj.getString("address");

                float zoom = 11;
                try {
                    zoom = mMap.getCameraPosition().zoom;
                }catch(Exception e)
                {

                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, zoom));
                mMap.addMarker(new MarkerOptions().position(mLatLng).title(mVehNumber + " | " + mLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                Toast.makeText(context, "Data updated \n" + mVehNumber + " | " + mLocation + "\n" + obj.getString("timestamp"), Toast.LENGTH_LONG).show();


                // Update Location
                if(etaEnabled == false)
                {
                    locationDetails.setText(mLocation + " @ " + obj.getString("timestamp"));
                }

                // Add Current Location and School Icons
                if(mCurrentLocation!=null) {
                    LatLng curLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(curLatLng).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.curlocation)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        if(timer == null)
        {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateData();
                }
            }, 10, 15000);//put here time 1000 milliseconds=1 second

        }

        try {
            if(etaEnabled == true) {
                if (mGoogleApiClient.isConnected()) {
                    startLocationUpdates();
                }
            }
        }catch(Exception e)
        {

        }
    }

    @Override
    public void onPause() {

        super.onPause();
        stopLocationUpdates();
        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
        try {
            mGoogleApiClient.disconnect();
        }catch(Exception e)
        {

        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        try {
            mGoogleApiClient.connect();
        }catch(Exception e)
        {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer!=null) {
            timer.cancel();
            timer = null;
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setUpMap();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setTrafficEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 19));

        mMap.addMarker(new MarkerOptions().position(mLatLng).title(mVehNumber + " | " + mLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_livemaps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemMapType :
                showMapTypeSelectorDialog();
            default :
                return super.onOptionsItemSelected(item);
        }
    }
}
