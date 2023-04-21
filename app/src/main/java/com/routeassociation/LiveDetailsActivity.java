package com.routeassociation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.routeassociation.adapter.LiveRecyAdapter;
import com.routeassociation.pojo.BusTypeDetails;
import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.GpsTransactionLive;
import com.routeassociation.util.Constants;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class LiveDetailsActivity extends AppCompatActivity {
    ListView listView;
    JSONArray array;
    String params;
    SwipeRefreshLayout swipeView;

    private EditText searchText;
    private Spinner busTypeSpinner;

    GoogleCloudMessaging gcm;
    String regid;
    Context context;
    String vehNumber = "-";
    String vehName = "-";
    String busType;
    int vehId = 0;

    private static final String TAG = "GCMDemo";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final float MILLI_SEC_PER_DAY = 1000 * 60 * 60 * 24;
    public static final long STOPPED_STATUS_THRESHOLD = 319;
    public static final long STOPPED_STATUS_THRESHOLD_WM = 60;
    private final String SENDER_ID = "916335551651";
    private Integer usrId;
    private Integer ugpId;
    private Integer rasId;
    private Timer timer;
    private int gpsCutoff = 0;
    private int running = 0;
    private int stationary = 0;
    private int off = 0;
    private int orgId, depId;
    private ArrayList<GpsTransactionLive> liveList = new ArrayList<GpsTransactionLive>();
    private ArrayList<GpsTransactionLive> runningList = new ArrayList<GpsTransactionLive>();
    private ArrayList<GpsTransactionLive> idleList = new ArrayList<GpsTransactionLive>();
    private ArrayList<GpsTransactionLive> gpsCutList = new ArrayList<GpsTransactionLive>();
    private ArrayList<GpsTransactionLive> offList = new ArrayList<GpsTransactionLive>();
    private boolean isInfinityFleet = false;
    private RecyclerView recyView;
    private ProgressDialog progDailog;
    private String[] departments;
    private int[] depIds;
    private Util util;
    private int tempSelectedDept = 0;
    private int selectedDept = 0;
    private int departmentId;

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
    protected void onResume() {
        super.onResume();
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    fetchAndShowData();
                }
            }, 10, 45000);//put here time 1000 milliseconds=1 second

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        try {
            context = this;

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Live Data");
            toolbar.setTitleTextColor(Color.WHITE);

            ImageButton depImageButton = toolbar.findViewById(R.id.depImgBtn);
            depImageButton.setOnClickListener(v -> {
                if (departments != null && departments.length > 0) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Select Department");

                    String[] list = {"All"};
                    String[] departmentList = new String[list.length + departments.length];

                    // copy the separate arrays into the combined array
                    System.arraycopy(list, 0, departmentList, 0, list.length);
                    System.arraycopy(departments, 0, departmentList, list.length, departments.length);

                    alert.setSingleChoiceItems(departmentList, selectedDept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                departmentId = 0;
                                tempSelectedDept = which;
                            } else {
                                departmentId = depIds[which - 1];
                                tempSelectedDept = which;
                            }
                        }
                    });

                    alert.setPositiveButton("OK", (dialog, which) -> {
                        if (departmentId != depId) {
                            depId = departmentId;
                            selectedDept = tempSelectedDept;
                            fetchAndShowData();
                        }
                    });

                    alert.setNegativeButton("Cancel", null);

                    AlertDialog show = alert.create();
                    show.show();

                }
            });

            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

            }
            //set white back icon to toolbar
            final Drawable upArrow = getResources().getDrawable(R.drawable.back);
            upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            //check internet connection
            CheckInternet checkInternet = new CheckInternet();
            boolean checkConnection = checkInternet.checkConnection(context);
            if (checkConnection) {

            } else {
                Toast.makeText(context,
                        "Please check your internet connection or try again later!", Toast.LENGTH_LONG).show();
            }

            //load login details
            SharedPreferences loginDetails = getSharedPreferences("user", 0);
            params = loginDetails.getString("params", null);

            JSONArray jsonArray = new JSONArray(params);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String status = jsonObject.getString(Constants.responseStatus);
            if (status.equals("SUCCESS")) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);

                usrId = jsonObject2.getInt("usrId");
                ugpId = jsonObject2.getInt("ugpId");
                orgId = jsonObject2.getInt("orgId");
                rasId = 0;

                if (ugpId == 2){
                    depId = 0;
                    depImageButton.setVisibility(View.VISIBLE);

                }else {
                    depId = jsonObject2.getInt("depId");
                    depImageButton.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (isInfinityFleet) {
            Toast.makeText(this, "Welcome to InfinityFleet !!", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Welcome to eTechSchoolBus !!", Toast.LENGTH_LONG).show();
        }

        //get search edit text reference
        searchText = findViewById(R.id.searchText);

        //get util class reference
        util = new Util(context);

        busTypeSpinner = findViewById(R.id.busTypeSpinner);
        final ArrayList<BusTypeDetails> busTypeDetailsArrayList = new ArrayList<>();
        busTypeDetailsArrayList.add(new BusTypeDetails(1, "All", "A"));
        busTypeDetailsArrayList.add(new BusTypeDetails(2, "Running", "R"));
        busTypeDetailsArrayList.add(new BusTypeDetails(3, "Idle", "I"));
        busTypeDetailsArrayList.add(new BusTypeDetails(4, "GPS Cut", "G"));
        busTypeDetailsArrayList.add(new BusTypeDetails(5, "Off", "O"));

        ArrayAdapter<BusTypeDetails> spinnerData = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, busTypeDetailsArrayList);
        busTypeSpinner.setAdapter(spinnerData);

        busType = busTypeDetailsArrayList.get(0).getTypeCode();

        busTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                busType = busTypeDetailsArrayList.get(position).getTypeCode();

                ArrayList<GpsTransactionLive> originalVehList = getLiveVehicleList();
                setBusListToRView(originalVehList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                busType = busTypeDetailsArrayList.get(0).getTypeCode();
            }
        });

        recyView = (RecyclerView) findViewById(R.id.recyView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyView.setLayoutManager(layoutManager);

        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeView);

        swipeView.setEnabled(false);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            public void onRefresh() {
                swipeView.setRefreshing(true);
                fetchAndShowData();
                swipeView.setRefreshing(false);
            }
        });


        recyView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);

            }
        });

        if (!networkState()) {
            Toast.makeText(this, "No Internet Connection !!", Toast.LENGTH_LONG).show();
            return;
        }

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

        if (ugpId == 2) {
            //LoadDepartment AsyncTask
            new LoadDepartment().execute();
        } else {
            fetchAndShowData();
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fetchAndShowData();
            }
        }, 10, 45000);//put here time 1000 milliseconds=1 second

    }

    void filter(String text){

        ArrayList<GpsTransactionLive> originalVehList = getLiveVehicleList();
        ArrayList<GpsTransactionLive> temp = new ArrayList<>();
        for(GpsTransactionLive gpsTransactionLive: originalVehList){
            if(gpsTransactionLive.getVehNumber().toLowerCase().contains(text.toLowerCase())){
                temp.add(gpsTransactionLive);
            }
        }

        //update recycler view
        LiveRecyAdapter liveRecyAdapter = new LiveRecyAdapter(context, temp);
        recyView.setAdapter(liveRecyAdapter);

    }

    public void fetchAndShowData() {
        try {
            if (rasId == null) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Something went wrong !! Please check the Internet Connection and ReLogin. Contact the school administrator if the problem persists.", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }

            if (rasId != 0) {
                loadDataForRasId(rasId);
            } else {
                loadData();
            }
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "No Internet Connection !!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public ArrayList<GpsTransactionLive> getLiveVehicleList() {
        ArrayList<GpsTransactionLive> originalVehList;

        switch (busType) {
            default:
            case "A":
                originalVehList = liveList;
                break;
            case "R":
                originalVehList = runningList;
                break;
            case "I":
                originalVehList = idleList;
                break;
            case "G":
                originalVehList = gpsCutList;
                break;
            case "O":
                originalVehList = offList;
                break;
        }
        return originalVehList;
    }

    // New Method added to to extract information about GPS Transaction Data of Routes assigned to Ras Id
    public void loadDataForRasId(int rasId) {

        if (networkState() == false) {
            Toast.makeText(this, "No Internet Connection !!", Toast.LENGTH_LONG).show();
            return;
        }

        // Get Json Data by using webservices
        Util util = new Util(context);
        JSONArray array;
        JSONObject jsonObject;
        jsonObject = util.getLiveDataForRasId(rasId);
        try {
            array = jsonObject.getJSONArray("data");
        } catch (Exception e) {
            String status = "Data not available.";
            try {
                status = jsonObject.getString(Constants.responseStatus);
            } catch (Exception e1) {
                // Do nothing
            }

            Toast.makeText(this, status, Toast.LENGTH_LONG).show();
            return;
        }

        // Load List
        liveList.clear();
        runningList.clear();
        idleList.clear();
        gpsCutList.clear();
        offList.clear();

        // Statistics Init
        gpsCutoff = 0;
        running = 0;
        stationary = 0;
        off = 0;

        for (int i = 0; i < array.length(); i++) {
            try {
                GpsTransactionLive live = new GpsTransactionLive();
                live.setGpsLat(array.getJSONObject(i).get("Lat").toString());
                live.setGpsLng(array.getJSONObject(i).get("Lng").toString());
                live.setGpsTimeStamp(array.getJSONObject(i).get("Timestamp").toString());
                live.setVehNumber(array.getJSONObject(i).get("VehNumber").toString());
                live.setSpeed(array.getJSONObject(i).get("Speed").toString());
                live.setLocation(array.getJSONObject(i).get("Location").toString());
                live.setVehName(array.getJSONObject(i).get("VehName").toString());
                live.setViolation(array.getJSONObject(i).get("Violations").toString());

                live.setVehId(Integer.valueOf(array.getJSONObject(i).get("VehId").toString()));

                String stp = array.getJSONObject(i).get("Stoppage").toString();
                try {
                    Double d = Double.parseDouble(stp);
                    stp = String.valueOf(d.intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    live.setRouteName(array.getJSONObject(i).get("RouteName").toString());
                    live.setRouteType(array.getJSONObject(i).get("RouteType").toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                live.setStoppage(stp);
                live.setStatus(array.getJSONObject(i).get("Status").toString());
                liveList.add(live);

                Date dtTimestamp;
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                try {
                    dtTimestamp = format.parse(live.getGpsTimeStamp());
                } catch (Exception e) {
                    dtTimestamp = new Date();
                }

                int speed = Integer.valueOf(live.getSpeed());
                long diff = (long) ((((float)(new Date()).getTime() - dtTimestamp.getTime()) / MILLI_SEC_PER_DAY) * 24.0f * 60.0f);
//                diff = diff / 3600000;
                int statusIndex = speed > 0 ? 0 : 1;
                statusIndex = diff > (ugpId == 1 ? STOPPED_STATUS_THRESHOLD_WM : STOPPED_STATUS_THRESHOLD) ? 3 : statusIndex;
//                if (diff > 4) {
//                    offList.add(live);
//                    off++;
//                } else {
//
//                    if (speed == -1 || live.getLocation().trim().equals("(9999.9999,9999.9999)")) {
//                        gpsCutList.add(live);
//                        gpsCutoff++;
//                    } else if (speed == 0) {
//                        idleList.add(live);
//                        stationary++;
//                    } else {
//                        runningList.add(live);
//                        running++;
//                    }
//                }

                //CASE 0: GREEN-RUNNING
                //CASE 1: YELLOW-IDLE
                //CASE 2: ORANGE-GPS CUT OFF
                //CASE 3: RED-OFF

                switch (statusIndex) {
                    default:
                    case 0:
                        runningList.add(live);
                        running++;
                        break;

                    case 1:
                        idleList.add(live);
                        stationary++;
                        break;

                    case 2:
                        gpsCutList.add(live);
                        gpsCutoff++;
                        break;

                    case 3:
                        offList.add(live);
                        off++;
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        runOnUiThread(new Runnable() {
            public void run() {
                // Set Status Objects
                TextView loff = (TextView) findViewById(R.id.liveOff);
                loff.setText("Off : " + String.valueOf(off));
                TextView lGpsoff = (TextView) findViewById(R.id.liveGpsOff);
                lGpsoff.setText("GPS Cut : " + String.valueOf(gpsCutoff));
                TextView lStationary = (TextView) findViewById(R.id.liveStationary);
                lStationary.setText("Idle : " + String.valueOf(stationary));
                TextView lRunning = (TextView) findViewById(R.id.liveRunning);
                lRunning.setText("Running : " + String.valueOf(running));

                ArrayList<GpsTransactionLive> originalVehList = getLiveVehicleList();
                setBusListToRView(originalVehList);
            }
        });
    }

    public void setBusListToRView(ArrayList<GpsTransactionLive> vehList) {
        // Adapter, List, Array Linking
        LiveRecyAdapter adapter = new LiveRecyAdapter(context, vehList);
        recyView.setAdapter(adapter);

        Animation anim = new AlphaAnimation(0.5f, 1.0f);
        anim.setDuration(80); //You can manage the blinking time with this parameter
        anim.setStartOffset(1);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(2);
        recyView.startAnimation(anim);
    }


    public void loadData() {

        if (!networkState()) {
            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(context, "No Internet Connection !!", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        Util util = new Util(context);

        JSONArray array;
//        if (params.startsWith("TOKEN")) {
//            String[] strs = params.split(":");
//            String token = strs[1];
//
//            array = util.getTokenLiveData(token);
//
//        } else {
//            // Normal Operation | User Login
//
////            if (ugpId == 1 || ugpId == 2)
////                depId = 0;
//
//            array = util.getLiveData(orgId, depId);
//        }

        array = util.getLiveData(orgId, depId);

        if (array != null && array.length() > 0) {

            liveList.clear();
            runningList.clear();
            idleList.clear();
            gpsCutList.clear();
            offList.clear();

            gpsCutoff = 0;
            running = 0;
            stationary = 0;
            off = 0;

            for (int i = 0; i < array.length(); i++) {
                try {
                    GpsTransactionLive live = new GpsTransactionLive();
                    live.setGpsLat(array.getJSONObject(i).get("Lat").toString());
                    live.setGpsLng(array.getJSONObject(i).get("Lng").toString());
                    live.setGpsTimeStamp(array.getJSONObject(i).get("Timestamp").toString());
                    live.setVehNumber(array.getJSONObject(i).get("VehNumber").toString());
                    live.setSpeed(array.getJSONObject(i).get("Speed").toString());
                    live.setLocation(array.getJSONObject(i).get("Location").toString());
                    live.setVehName(array.getJSONObject(i).get("VehName").toString());
                    live.setViolation(array.getJSONObject(i).get("Violations").toString());
                    live.setVehId(Integer.valueOf(array.getJSONObject(i).get("VehId").toString()));
                    String stp = array.getJSONObject(i).get("Stoppage").toString();
                    try {
                        Double d = Double.parseDouble(stp);
                        stp = String.valueOf(d.intValue());
                    } catch (Exception e) {

                    }

                    try {
                        live.setRouteName(array.getJSONObject(i).get("RouteName").toString());
                        live.setRouteType(array.getJSONObject(i).get("RouteType").toString());

                    } catch (Exception e) {

                    }

                    live.setStoppage(stp);
                    live.setStatus(array.getJSONObject(i).get("Status").toString());
                    liveList.add(live);

                    Date dtTimestamp;
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    try {
                        dtTimestamp = format.parse(live.getGpsTimeStamp());
                    } catch (Exception e) {
                        dtTimestamp = new Date();
                    }

                    long diff = (new Date()).getTime() - dtTimestamp.getTime();
                    diff = diff / 3600000;
                    if (diff > 4) {
                        offList.add(live);
                        off++;
                    } else {
                        int speed = Integer.valueOf(live.getSpeed());
                        if (speed == -1 || live.getLocation().trim().equals("(9999.9999,9999.9999)")) {
                            gpsCutList.add(live);
                            gpsCutoff++;
                        } else if (speed == 0) {
                            idleList.add(live);
                            stationary++;
                        } else {
                            runningList.add(live);
                            running++;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(new Runnable() {
                public void run() {

                    // Set Status Objects
                    TextView loff = (TextView) findViewById(R.id.liveOff);
                    loff.setText("Off : " + String.valueOf(off));
                    TextView lGpsoff = (TextView) findViewById(R.id.liveGpsOff);
                    lGpsoff.setText("GPS Cut : " + String.valueOf(gpsCutoff));
                    TextView lStationary = (TextView) findViewById(R.id.liveStationary);
                    lStationary.setText("Idle : " + String.valueOf(stationary));
                    TextView lRunning = (TextView) findViewById(R.id.liveRunning);
                    lRunning.setText("Running : " + String.valueOf(running));

                    ArrayList<GpsTransactionLive> originalVehList = getLiveVehicleList();
                    setBusListToRView(originalVehList);
                }
            });
        } else {
            runOnUiThread(() -> Toast.makeText(context, "Vehicle Details not found...", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onPause() {

        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        } else if (id == R.id.action_refresh) {
//            loadData();
//            return true;
//        } else
        if (id == android.R.id.home) {
            this.startActivity(new Intent(LiveDetailsActivity.this,
                    MainActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public class LoadDepartment extends AsyncTask<String, String, String> {
        private String response;

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

                if (response == null || response.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Unable to parse...", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONArray array = new JSONArray(response);
                JSONObject statusObj = array.getJSONObject(0);
                JSONObject data = array.getJSONObject(1);

                String status = statusObj.getString(Constants.responseStatus);
                if (!status.equalsIgnoreCase("SUCCESS")) {
                    Toast.makeText(getApplicationContext(), data.getString("data"), Toast.LENGTH_LONG).show();
                    return;
                }

                JSONArray jsonArray = data.getJSONArray("data");
                int length = jsonArray.length();
                departments = new String[length];
                depIds = new int[length];
                for (int i = 0; i < length; i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    departments[i] = jo.getString("depName");
                    depIds[i] = jo.getInt("depId");
                }

                fetchAndShowData();

            } catch (Exception e) {
                e.printStackTrace();
                fetchAndShowData();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = util.getDepartments(orgId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
