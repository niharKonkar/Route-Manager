package com.routeassociation;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.routeassociation.pojo.MarkerData;


import java.util.ArrayList;


public class LocationDetailsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng mLatLng;
    private ArrayList<MarkerData> markersArray;
    private LatLngBounds.Builder builder;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        try{
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

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


        builder = new LatLngBounds.Builder();

        //marker list
        markersArray = new ArrayList<MarkerData>();

        MarkerData markerData = new MarkerData();
        markerData.setLat( 18.445089);
        markerData.setLng(73.868980);
        markerData.setFlag(false);
        markerData.setTitle("swarget");

        markersArray.add(markerData);

        MarkerData markerData3 = new MarkerData();
        markerData3.setLat(	18.501059);
        markerData3.setLng(73.862686);
        markerData3.setTitle("bibewadi");
        markerData3.setFlag(true);
        markersArray.add(markerData3);

        MarkerData markerData4 = new MarkerData();
        markerData4.setLat(	18.551059);
        markerData4.setLng(73.862686);
        markerData4.setTitle("bv");
        markerData4.setFlag(false);
        markersArray.add(markerData4);

        MarkerData markerData2 = new MarkerData();
        markerData2.setLat(18.5074);
        markerData2.setLng(73.8077);
        markerData2.setFlag(true);
        markerData2.setTitle("katraj");

        markersArray.add(markerData2);

        for(int i =0 ;i<markersArray.size();i++) {



            if(markersArray.get(i).isFlag() == true) {

                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.loc2);
                mLatLng = new LatLng(markersArray.get(i).getLat(), markersArray.get(i).getLng());
                MarkerOptions markerOptions = new MarkerOptions().position(mLatLng)
                        .title(markersArray.get(i).getTitle())
                        .snippet(""+markersArray.get(i).getLat()+","+
                                markersArray.get(i).getLng())
                        .icon(icon);

                mMap.addMarker(markerOptions);
                builder.include(mLatLng);

            }else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.loc1);
                mLatLng = new LatLng(markersArray.get(i).getLat(), markersArray.get(i).getLng());
                MarkerOptions markerOptions = new MarkerOptions().position(mLatLng)
                        .title(markersArray.get(i).getTitle())
                        .snippet(""+markersArray.get(i).getLat()+","+
                                markersArray.get(i).getLng())
                        .icon(icon);

                mMap.addMarker(markerOptions);
                builder.include(mLatLng);
            }



            // set bound to show all markers in single view
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.20);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);

        }

    }

}
