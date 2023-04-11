package com.routeassociation;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.adapter.RoutesDetailsRecyclerAdapter;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.RouteDetails;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private Dialog dialog;
    private boolean doubleBackToExitPressedOnce;
    private ConstraintLayout vehicleDetailsLayout, routeDetailsLayout;
    private LottieAnimationView vehicleAnimationView, routeAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(null);

            try {

                //get reference
                context = this;
                dialog = new Dialog(context);

                //check permission
                isPermissionGranted();

                //check internet
                CheckInternet checkInternet = new CheckInternet();
                boolean checkConnection = checkInternet.checkConnection(context);
                if (checkConnection) {

                    vehicleDetailsLayout = findViewById(R.id.vehicleDetailsLayout);
                    routeDetailsLayout = findViewById(R.id.routeDetailsLayout);
                    vehicleAnimationView = findViewById(R.id.vehicleAnimationView);
                    routeAnimationView = findViewById(R.id.routeAnimationView);

                    vehicleAnimationView.setAnimation(R.raw.bus);
                    vehicleAnimationView.enableMergePathsForKitKatAndAbove(true);

                    routeAnimationView.setAnimation(R.raw.route);
                    routeAnimationView.enableMergePathsForKitKatAndAbove(true);

                    vehicleDetailsLayout.setOnClickListener(view -> {
                        Intent intent = new Intent(MainActivity.this, LiveDetailsActivity.class);
                        startActivity(intent);
});

                    routeDetailsLayout.setOnClickListener(view -> {
                        Intent intent = new Intent(MainActivity.this, RoutesDetailsAndInfoActivity.class);
                        startActivity(intent);
                    });

                } else {
                    Toast.makeText(MainActivity.this,
                            "connection not found...plz check connection", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);

                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }


    @Override
    public void onBackPressed() {
        try {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //communication
        if(id == R.id.btnCommunication)
        {
            Intent intent = new Intent(MainActivity.this, SelectCommunicationDetailsActivity.class);
            startActivity(intent);
        }

        //incident
        if(id == R.id.btnIncident)
        {
            Intent intent = new Intent(MainActivity.this, IncidentDetailsActivity.class);
            startActivity(intent);
        }

        //system log
        if (id == R.id.btnSystemLog) {
            Intent intent = new Intent(MainActivity.this, SystemLogActivity.class);
            startActivity(intent);
        }

        //add driver
        if (id == R.id.btnAddDriver) {
            Intent intent = new Intent(MainActivity.this, AddDriverActivity.class);
            startActivity(intent);

        }

        //live data
        if (id == R.id.btnLiveData) {
            Intent intent = new Intent(MainActivity.this, LiveDetailsActivity.class);
            startActivity(intent);

        }

        //Route Count
        if (id == R.id.btnRouteCount) {
            Intent intent = new Intent(MainActivity.this, RouteCountActivity.class);
            startActivity(intent);
        }

        //Route Count
        if (id == R.id.btnViewBusStops) {
            Intent intent = new Intent(MainActivity.this, BusStopsGetLocationActivity.class);
            startActivity(intent);
        }

        //Timeline
        if (id == R.id.btnTimeline) {
            Intent intent = new Intent(MainActivity.this, TimelineActivity.class);
            startActivity(intent);

        }

        //analysis
        if (id == R.id.btnRealtimeAnalysis) {
            Intent intent = new Intent(MainActivity.this, RealTimePickDropAnalysisActivity.class);
            startActivity(intent);

        }

        //upload documents
        if (id == R.id.btnUploadDocuments) {
            Intent intent = new Intent(MainActivity.this, UploadDocumentsActivity.class);
            startActivity(intent);
        }

        //upload incidence
        if (id == R.id.btnUploadIncidence) {
            Intent intent = new Intent(MainActivity.this, UploadIncidenceActivity.class);
            startActivity(intent);
        }

        //notifications
        if (id == R.id.btnNotification) {
            Intent intent = new Intent(MainActivity.this, NotificationHistoryActivity.class);
            startActivity(intent);

        }

        if (id == R.id.btnCamera) {
            Intent intent = new Intent(MainActivity.this, CameraStatusActivity.class);
            startActivity(intent);

        }

        //refresh
        if (id == R.id.btnRefresh) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
        //about
        else if (id == R.id.btnAbout) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://techlead-india.com"));
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error to load school website !", Toast.LENGTH_SHORT).show();
            }

        }
        //logout
        else if (id == R.id.btnLogout) {


            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_logout);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
            dialog.setCanceledOnTouchOutside(false);

            TextView btnLogout = (TextView) dialog.findViewById(R.id.btnLogout);
            TextView btnExit = (TextView) dialog.findViewById(R.id.btnCancel);

            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();

                }
            });
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();

                    //login details
                    SharedPreferences loginDetails = getSharedPreferences("user", 0);
                    loginDetails.edit().putString("params", "").commit();

                    //RouteDetails
                    SharedPreferences routeDetailsPrefs = getSharedPreferences("RouteDetails", MODE_PRIVATE);
                    routeDetailsPrefs.edit().putString("response", "").commit();

                    Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                    startActivity(intent);
                }
            });

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
