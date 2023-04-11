package com.routeassociation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.routeassociation.pojo.CheckInternet;

public class DriverInfoActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRouteInfo);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        toolbar.setTitle("Driver Information");

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        if(networkState()==false)
        {
            Toast.makeText(this, "No Internet Connection !!", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        context = this;

        //check internet connection
        CheckInternet checkInternet = new CheckInternet();
        boolean checkConnection = checkInternet.checkConnection(context);
        if (checkConnection) {

        } else {
            Toast.makeText(context,
                    "Please check your internet connection or try again later!", Toast.LENGTH_LONG).show();
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
}
