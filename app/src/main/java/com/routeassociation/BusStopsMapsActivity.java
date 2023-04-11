package com.routeassociation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.routeassociation.pojo.BusStop;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BusStopsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<BusStop> busStopArrayList;
    private double lat, lng;
    private Marker marker, userMarker;
    private TextView busStopDetails;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_stops_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lat = getIntent().getDoubleExtra("lat", 0.0);
        lng = getIntent().getDoubleExtra("lng", 0.0);

        busStopDetails = findViewById(R.id.busStopDetails);

        String json = getIntent().getStringExtra("busStopArray");
        Type type = new TypeToken<List<BusStop>>(){}.getType();
        Gson gson = new Gson();
        busStopArrayList = gson.fromJson(json, type);

        Collections.sort(busStopArrayList, new Comparator<BusStop>() {
            @Override
            public int compare(BusStop bo1, BusStop bo2) {
                return ((bo1.getDistance() > bo2.getDistance()) || (bo1.getDistance() == bo2.getDistance()) ? 1 : -1);
            }
        });
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

        // Add a marker in Sydney and move the camera
        LatLng userPosition = new LatLng(lat, lng);
        userMarker = mMap.addMarker(new MarkerOptions().position(userPosition).title("User Position").icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.ic_location_origin))));
        userMarker.showInfoWindow();

        int i = 0;
        for (BusStop busStop : busStopArrayList){
            LatLng bStop = new LatLng(busStop.getBusStopLat(), busStop.getBusStopLng());
            marker = mMap.addMarker(new MarkerOptions().position(bStop).title(busStop.getBusStopName()).icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.bus_stop))));
            marker.setTag(busStop);

            if(i == 0){
                marker.showInfoWindow();
            }
            i++;
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                BusStop stop = (BusStop) marker.getTag();
                LatLng bStop;
                marker.showInfoWindow();
                if(stop != null){
                    busStopDetails.setText("Selected Stop Name : " + stop.getBusStopName() + "\nDistance : " + stop.getDistance() + " km");
                    bStop = new LatLng(stop.getBusStopLat(), stop.getBusStopLng());
                }else {
                    busStopDetails.setText("Your nearest bus stop : \n" + busStopArrayList.get(0).getBusStopName() + "\nDistance : " + busStopArrayList.get(0).getDistance() + " km");
                    bStop = new LatLng(lat, lng);
                }
                final CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(bStop)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(60)// Sets the tilt of the camera to 30 degrees
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                return true;
            }
        });

        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userPosition)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(60)// Sets the tilt of the camera to 30 degrees
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
        busStopDetails.setText("Your nearest bus stop : \n" + busStopArrayList.get(0).getBusStopName() + "\nDistance : " + busStopArrayList.get(0).getDistance() + " km");

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    //convert drawable to bitmap image
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void goBack(View view) {
        finish();
    }

    public void recenter(View view) {

        userMarker.showInfoWindow();
        LatLng userPosition = new LatLng(lat, lng);
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userPosition)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(60)// Sets the tilt of the camera to 30 degrees
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        busStopDetails.setText("Your nearest bus stop : \n" + busStopArrayList.get(0).getBusStopName() + "\nDistance : " + busStopArrayList.get(0).getDistance() + " km");
    }
}
