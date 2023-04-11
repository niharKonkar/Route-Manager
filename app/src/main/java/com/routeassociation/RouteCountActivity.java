package com.routeassociation;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.routeassociation.adapter.RouteCountRecyAdapter;
import com.routeassociation.pojo.RouteDetails;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RouteCountActivity extends AppCompatActivity implements RouteCountRecyInterface, AdapterView.OnItemSelectedListener {

    private Context context;
    private ArrayList<RouteDetails> routeDetailsArrayList, filteredRoutesArrayList;
    RouteCountRecyAdapter adapter;
    private Dialog dialog;
    private Button routeButton;
    private EditText routeCount;
    private ProgressDialog progDailog;
    private String response, params;
    private Util util;
    private int gegId, orgId, depId;
    private String pickupDrop = "P";
    private String date;
    private Spinner pickupDropSpinner;
    private static final String[] pickupDropArray = {"Pickup", "Drop"};

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(RouteCountActivity.this,
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
        setContentView(R.layout.activity_route_count);

        try{
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Route Count");
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

            context = this;
            util = new Util(context);
            filteredRoutesArrayList = new ArrayList<>();
            routeButton = findViewById(R.id.routeCountName);
            routeCount = findViewById(R.id.routeCount);
            pickupDropSpinner = findViewById(R.id.pickupDropSpinner);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RouteCountActivity.this,
                    android.R.layout.simple_spinner_item,pickupDropArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pickupDropSpinner.setAdapter(adapter);
            pickupDropSpinner.setOnItemSelectedListener(this);

            //load login details
            SharedPreferences loginDetails = getSharedPreferences("user", 0);
            params = loginDetails.getString("params", null);

            JSONArray jsonArray = new JSONArray(params);
            JSONObject jsonObject9 = jsonArray.getJSONObject(0);
            String status = jsonObject9.getString("status");
            if (status.equals("SUCCESS")) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);

                orgId = jsonObject2.getInt("orgId");
                depId = jsonObject2.getInt("depId");

            }

            SharedPreferences prefs = getSharedPreferences("RouteDetails", MODE_PRIVATE);
            String resRouteDetails = prefs.getString("response", null);
            if (resRouteDetails != null) {
                routeDetailsArrayList = new ArrayList<>();
                JSONArray array = new JSONArray(resRouteDetails);
                for (int i = 0; i < array.length(); i++) {

                    RouteDetails details = new RouteDetails();

                    JSONObject jsonObject = array.getJSONObject(i);
                    details.setRouteName(jsonObject.getString("routeName"));
                    details.setRouteId(jsonObject.getInt("routeId"));
                    routeDetailsArrayList.add(details);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                pickupDrop = "P";
                break;
            case 1:
                pickupDrop = "D";
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public void openRoutesDetails(View view){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_choose_route);

        RecyclerView recyclerView = dialog.findViewById(R.id.routesRecyclerView);
        adapter = new RouteCountRecyAdapter(context, routeDetailsArrayList, this);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter.notifyDataSetChanged();

        EditText editText = dialog.findViewById(R.id.searchRoute);
        //to call a method whenever there is some change on the EditText
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void filter(String text) {

        filteredRoutesArrayList.clear();

        //looping through existing elements
        for (RouteDetails r : routeDetailsArrayList) {
            //if the existing elements contains the search input
            if (r.getRouteName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filteredRoutesArrayList.add(r);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filteredRoutesArrayList);
    }

    @Override
    public void recyclerViewListClicked(String routeName){
        routeButton.setText(routeName);
        dialog.dismiss();
        Toast.makeText(context, routeName + " selected.", Toast.LENGTH_SHORT).show();
    }

    public void saveRouteCount(View view){

        String routeName = routeButton.getText().toString();
        int rCount = 0;
        if(!routeCount.getText().toString().equals("")){
            rCount = Integer.parseInt(routeCount.getText().toString());
        }else {
            Toast.makeText(context, "Please enter the route count.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean doesRouteExist = false;

        if(rCount > 100){
            Toast.makeText(context, "Count should not be greater than 100.", Toast.LENGTH_LONG).show();
            return;
        }

        for(RouteDetails r: routeDetailsArrayList){
            if(routeName.equals(r.getRouteName())){
                doesRouteExist = true;
                gegId = r.getRouteId();
                break;
            }
        }

        if(doesRouteExist){
            //load route analysis
            StoreRouteCountTask storeRouteCountTask = new StoreRouteCountTask();
            storeRouteCountTask.execute(null, null);
        }else {
            Toast.makeText(context, "Please select the route.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    class StoreRouteCountTask extends AsyncTask<String, String, String> {
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
                response = util.saveRouteRasCount(orgId, depId, gegId, pickupDrop, Integer.parseInt(routeCount.getText().toString()));

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


                if (response != null) {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object1 = jsonArray.getJSONObject(0);
                    String status = object1.getString("status");
                    if (status.equals("SUCCESS")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Route count successfully saved!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {
                        Toast.makeText(context, "Something went wrong, Please try again later..", Toast.LENGTH_SHORT).show();
                        return;

                    }

                } else {
                    Toast.makeText(context, "Something went wrong, Please try again later..", Toast.LENGTH_SHORT).show();
                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
