package com.routeassociation;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.pojo.DriverDetails;
import com.routeassociation.pojo.IncidentCategoryDetails;
import com.routeassociation.pojo.RouteDetailsInc;
import com.routeassociation.pojo.VehicleDetails;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadIncidentActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(UploadIncidentActivity.this,
                    MainActivity.class));
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Button btnSubmit;
    private TextView txtIncDate;
    private LinearLayout layDate;
    private EditText edtDesc;
    private RadioGroup rgPickDrop;
    private Spinner catSpinner, driverSpinner, vehicleSpinner, routeSpinner;
    private Context context;
    private Util util;
    private ArrayList<String> catList;
    private ProgressDialog progDailog;
    private ArrayList<VehicleDetails> vehList;
    private ArrayList<RouteDetailsInc> routeDetailslist;

    private int day, month, year, mHour, mMinute, mSec, catId = 0;
    private ArrayList<DriverDetails> driverDetailsArrayList;
    private int orgId, depId, userId, drvId, vehId, gegId;
    private String incDate, incDesc, category, pickupDrop = "P";
    private TextView txtAppDate;
    private ArrayList<IncidentCategoryDetails> incidentCategoryDetailsArrayList;
    private AutoCompleteTextView acTextVehicle, acTextRoute, acTextDriver;
    private Map<String, Integer> vehMap;
    private Map<String, Integer> routeMap, drvMap;

    // Added By Sagar
    private EditText etInvKms;
    private int kms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_incident);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Upload Incident");
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

            //get reference
            context = this;
            util = new Util(context);

            CheckInternet checkInternet = new CheckInternet();
            boolean checkConnection = checkInternet.checkConnection(context);
            if (checkConnection) {

            } else {
                Toast.makeText(context,
                        "connection not found...plz check connection", Toast.LENGTH_LONG).show();
            }


            //get login details
            SharedPreferences loginDetails = context.getSharedPreferences("user", 0);
            String params = loginDetails.getString("params", null);

            JSONArray jsonArray = new JSONArray(params);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            final String status = jsonObject.getString("status");
            if (status.equals("SUCCESS")) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                orgId = jsonObject2.getInt("orgId");
                depId = jsonObject2.getInt("depId");
                userId = jsonObject2.getInt("usrId");


            }

            btnSubmit = (Button) findViewById(R.id.btnSubmit);
            txtIncDate = (TextView) findViewById(R.id.txtIncDate);
            edtDesc = (EditText) findViewById(R.id.edtDesc);
            rgPickDrop = (RadioGroup) findViewById(R.id.rgPickDrop);
            catSpinner = (Spinner) findViewById(R.id.catSpinner);
            //  driverSpinner = (Spinner) findViewById(R.id.driverSpinner);
            // vehicleSpinner = (Spinner) findViewById(R.id.vehicleSpinner);
            // routeSpinner = (Spinner) findViewById(R.id.routeSpinner);
            txtAppDate = (TextView) findViewById(R.id.txtAppDate);
            acTextRoute = (AutoCompleteTextView) findViewById(R.id.acTextRoute);
            acTextVehicle = (AutoCompleteTextView) findViewById(R.id.acTextVehicle);
            acTextDriver = (AutoCompleteTextView) findViewById(R.id.acTextDriver);

            etInvKms = (EditText)findViewById(R.id.incKms);


            //set current date
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH) + 1;
            day = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mSec = c.get(Calendar.SECOND);


            Date curDate = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = df.format(curDate);


            txtAppDate.setText(currentDate);
            txtIncDate.setText(currentDate);


            //load incident categories
            LoadIncidentCategories loadIncidentCategories = new LoadIncidentCategories();
            loadIncidentCategories.execute(null, null);

            //load driver
            loadDriverOnSpinner();

            //load vehicles
            LoadVehicleData loadVehicleData = new LoadVehicleData();
            loadVehicleData.execute(null, null);

            //load routes
            LoadRoutes loadRoutes = new LoadRoutes();
            loadRoutes.execute(null, null);

            rgPickDrop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId == R.id.rbPickup) {
                        pickupDrop = "P";
                    } else if (checkedId == R.id.rbDrop) {
                        pickupDrop = "D";
                    }
                }
            });
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckInternet checkInternet = new CheckInternet();
                    boolean checkConnection = checkInternet.checkConnection(context);
                    if (checkConnection) {

                    } else {
                        Toast.makeText(context,
                                "connection not found...plz check connection", Toast.LENGTH_LONG).show();
                    }

                    incDate = txtIncDate.getText().toString().trim();
                    incDesc = edtDesc.getText().toString().trim();

                    //check vehicle selection
                    if (vehMap.get(acTextVehicle.getText().toString()) == null) {
                        Toast.makeText(context, "Please select the vehicle!", Toast.LENGTH_SHORT).show();
                        return;

                    } else {
                        vehId = vehMap.get(acTextVehicle.getText().toString());
                    }


                    //check route selection
                    if (routeMap.get(acTextRoute.getText().toString()) == null) {
                        Toast.makeText(context, "Please select the route!", Toast.LENGTH_SHORT).show();
                        return;

                    } else {
                        gegId = routeMap.get(acTextRoute.getText().toString());
                    }

                    //check driver selection
                    if (drvMap.get(acTextDriver.getText().toString()) == null) {
                        Toast.makeText(context, "Please select the driver!", Toast.LENGTH_SHORT).show();
                        return;

                    } else {
                        drvId = drvMap.get(acTextDriver.getText().toString());
                    }

                    if (catId == 0) {

                        Toast.makeText(context, "Please select incident category!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (category == null || category.equals("")) {

                        Toast.makeText(context, "Please select incident category!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (incDesc == null || incDesc.equals("")) {
                        Toast.makeText(context, "Incident description should not be blank!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (incDate == null || incDate.equals("")) {
                        Toast.makeText(context, "Incident date should not be blank!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (drvId == 0) {
                        Toast.makeText(context, "Please select driver!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (gegId == 0) {
                        Toast.makeText(context, "Please select route!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (vehId == 0) {
                        Toast.makeText(context, "Please select vehicle!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(etInvKms.getText().toString().isEmpty())
                    {
                        kms = 0;
                    }
                    else
                    {
                        kms = Integer.valueOf(etInvKms.getText().toString());
                    }

                    //upload
                    UploadIncident uploadIncident = new UploadIncident();
                    uploadIncident.execute(null, null);


                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //set reg date
    public void setDate(View view) {
        showDialog(999);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, myDateListener, year, month, day);
            datePickerDialog.show();
            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH) + 1;
            int year = c.get(Calendar.YEAR);
            int day = c.get(Calendar.DAY_OF_MONTH);
            datePickerDialog.getDatePicker().updateDate(year, month, day);
            // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            year = arg1;
            month = arg2;
            day = arg3;

            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mSec = c.get(Calendar.SECOND);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            showDate(year, month, day, mHour, mMinute, mSec);

                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    };

    private void showDate(final int year, final int month, final int day, final int mHour, final int mMinute, final int mSec) {
        try {
            String selectedDate = new StringBuilder().append(year).append("-")
                    .append(month).append("-").append(day).append(" ").append(mHour).append(":").append(mMinute).append(":").append(mSec).toString();
            txtIncDate.setText("  " + selectedDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LoadIncidentCategories extends AsyncTask<String, String, String> {
        private String resCatList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  progDailog = new ProgressDialog(context);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();*/
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                //  progDailog.dismiss();

                if (resCatList != null) {
                    JSONArray resArray = new JSONArray(resCatList);
                    JSONObject statusObj = resArray.getJSONObject(0);
                    String status = statusObj.getString("status");
                    if (status.equals("SUCCESS")) {
                        JSONObject dataObject = resArray.getJSONObject(1);
                        JSONArray dataArray = dataObject.getJSONArray("data");
                        incidentCategoryDetailsArrayList = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {

                            JSONObject object = dataArray.getJSONObject(i);
                            IncidentCategoryDetails categoryDetails = new IncidentCategoryDetails();
                            categoryDetails.setIcaId(object.getInt("icaId"));
                            categoryDetails.setIcsName(object.getString("icaDescription"));
                            incidentCategoryDetailsArrayList.add(categoryDetails);


                        }

                        if (incidentCategoryDetailsArrayList.size() > 0) {

                            //set adapter
                            ArrayAdapter<IncidentCategoryDetails> spinnerData = new ArrayAdapter<IncidentCategoryDetails>(UploadIncidentActivity.this, R.layout.support_simple_spinner_dropdown_item, incidentCategoryDetailsArrayList);
                            spinnerData.setDropDownViewResource(R.layout.spinner_text);
                            catSpinner.setAdapter(spinnerData);

                            catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    catId = incidentCategoryDetailsArrayList.get(position).getIcaId();
                                    category = incidentCategoryDetailsArrayList.get(position).getIcsName();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                resCatList = util.getIncidentCatList(orgId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class UploadIncident extends AsyncTask<String, String, String> {
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

                if (response != null) {
                    JSONArray resArray = new JSONArray(response);
                    JSONObject statusObj = resArray.getJSONObject(0);
                    String status = statusObj.getString("status");
                    if (status.equals("SUCCESS")) {
                        Toast.makeText(context, "Incident uploaded successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadIncidentActivity.this, IncidentDetailsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(context, "Failed to upload incident!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(context, "Failed to upload incident!", Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to upload incident!", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                response = util.addIncident(orgId, depId, gegId, drvId, vehId, pickupDrop, category, incDesc, userId, incDate, catId,kms);

            } catch (Exception e) {


                e.printStackTrace();
                Toast.makeText(context, "Failed to upload incident!", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
    }

    //load driver
    public void loadDriverOnSpinner() {
        try {
            String resDriver = util.getDriverList(orgId, depId);
            if (resDriver != null) {
                JSONArray jsonArray = new JSONArray(resDriver);
                JSONObject object1 = jsonArray.getJSONObject(0);
                String status = object1.getString("status");
                if (status.equals("SUCCESS")) {
                    drvMap = new HashMap<String, Integer>();
                    driverDetailsArrayList = new ArrayList<DriverDetails>();
                    JSONObject object2 = jsonArray.getJSONObject(1);
                    JSONArray array = object2.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        DriverDetails driverDetails = new DriverDetails();
                        JSONObject object = array.getJSONObject(i);
                        driverDetails.setDriverName(object.getString("driverName"));
                        driverDetails.setDriverId(object.getInt("driverId"));
                        driverDetails.setDriverAddress(object.getString("driverAddress"));
                        driverDetails.setDriverContact(object.getString("driverContact"));

                        drvMap.put(object.getString("driverName"), object.getInt("driverId"));
                        driverDetailsArrayList.add(driverDetails);
                    }

                    //set adapter
                    if (driverDetailsArrayList.size() > 0) {
                        ArrayAdapter<DriverDetails> adapter =
                                new ArrayAdapter<DriverDetails>(UploadIncidentActivity.this, android.R.layout.simple_list_item_1, driverDetailsArrayList);
                        acTextDriver.setAdapter(adapter);
                    } else {
                        Toast.makeText(context, "No data available for driver!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                   /* ArrayAdapter<DriverDetails> spinnerData = new ArrayAdapter<DriverDetails>(context, R.layout.support_simple_spinner_dropdown_item, driverDetailsArrayList);
                    driverSpinner.setAdapter(spinnerData);*/

                }


            } else {
                Toast.makeText(context, "No data available for driver!", Toast.LENGTH_SHORT).show();
                return;
            }
            driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //selectedDriver = driverSpinner.getSelectedItem().toString();
                    drvId = driverDetailsArrayList.get(position).getDriverId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //load vehicle data
    class LoadVehicleData extends AsyncTask<String, String, String> {
        private JSONArray response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(context);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            // progDailog.show();

        }

        @Override
        protected String doInBackground(String... aurl) {

            try {
                response = util.getAllVehicles(orgId, 0);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            try {
                //  progDailog.dismiss();
                if (response == null) {
                    Toast.makeText(context, "Vehicles not found !", Toast.LENGTH_LONG).show();
                } else {

                    vehList = new ArrayList<VehicleDetails>();
                    vehMap = new HashMap<String, Integer>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            VehicleDetails vehicleDetails = new VehicleDetails();
                            vehicleDetails.setVehId(jsonObject.getInt("vehId"));
                            vehicleDetails.setVehName(jsonObject.getString("vehNumber"));
                            // map.put(jsonObject.getString("vehNumber"), jsonObject.getInt("vehId"));
                            vehMap.put(jsonObject.getString("vehNumber"), jsonObject.getInt("vehId"));

                            vehList.add(vehicleDetails);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //set adapter
                    if (vehList.size() > 0) {
                        ArrayAdapter<VehicleDetails> adapter =
                                new ArrayAdapter<VehicleDetails>(UploadIncidentActivity.this, android.R.layout.simple_list_item_1, vehList);
                        acTextVehicle.setAdapter(adapter);
                    } else {
                        Toast.makeText(context, "Vehicles not found!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    /*vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            vehId = vehList.get(position).getVehId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });*/

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //load routes
    class LoadRoutes extends AsyncTask<String, String, String> {
        private JSONArray response;

        @Override

        protected void onPreExecute() {
            super.onPreExecute();

            progDailog = new ProgressDialog(UploadIncidentActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            // progDailog.show();

        }

        @Override
        protected String doInBackground(String... aurl) {

            try {
                response = util.getRoutes(orgId, depId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            try {
                //  progDailog.dismiss();

                if (response != null) {

                    routeDetailslist = new ArrayList<>();

                    routeMap = new HashMap<>();
                    for (int i = 0; i < response.length(); i++) {

                        RouteDetailsInc details = new RouteDetailsInc();
                        JSONObject jsonObject = response.getJSONObject(i);
                        details.setRouteId(jsonObject.getInt("routeId"));
                        details.setRouteName(jsonObject.getString("routeName"));
                        routeMap.put(jsonObject.getString("routeName"), jsonObject.getInt("routeId"));

                        routeDetailslist.add(details);
                    }


                    //set adapter
                    if (routeDetailslist.size() > 0) {

                        ArrayAdapter<RouteDetailsInc> adapter = new ArrayAdapter<RouteDetailsInc>(
                                context,
                                android.R.layout.simple_list_item_1,
                                routeDetailslist);
                        acTextRoute.setAdapter(adapter);

                    } else {
                        Toast.makeText(context, "Route details not found !", Toast.LENGTH_SHORT).show();
                        return;
                    }
                   /* if (routeDetailslist.size() != 0) {

                        ArrayAdapter<RouteDetailsInc> arrayAdapter = new ArrayAdapter<RouteDetailsInc>(
                                context,
                                android.R.layout.simple_list_item_1,
                                routeDetailslist);

                        arrayAdapter.setDropDownViewResource(R.layout.spinner_text);
                        routeSpinner.setAdapter(arrayAdapter);

                        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                gegId = routeDetailslist.get(position).getRouteId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else {
                        Toast.makeText(context, "Route details not found !", Toast.LENGTH_SHORT).show();
                        return;
                    }*/


                } else {
                    Toast.makeText(context, "Route details not found !", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
