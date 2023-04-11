package com.routeassociation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.pojo.CheckInternet;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

public class SendNotificationActivity extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        if (item.getItemId() == android.R.id.home) {
            this.startActivity(new Intent(SendNotificationActivity.this,
                    MainActivity.class));
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private Context context;
    private Util util;
    private Button btnSendMessage;
    private EditText edtMessage;
    private RadioGroup rgPickDrop;
    private int orgId, depId, gegId;
    private TextView txtRouteName,txtDepName;
    private String pickupDrop="P", message, routeName,depName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Send Notification");
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
        context = this;
        util = new Util(context);

        //check internet connection
        CheckInternet checkInternet = new CheckInternet();
        boolean checkConnection = checkInternet.checkConnection(context);
        if (checkConnection) {

        } else {
            Toast.makeText(context,
                    "Please check your internet connection or try again later!", Toast.LENGTH_LONG).show();
        }


        Intent intent = getIntent();
        if (intent != null) {
            orgId = intent.getIntExtra("orgId", orgId);
            depId = intent.getIntExtra("depId", depId);
            gegId = intent.getIntExtra("gegId", gegId);
            routeName = intent.getStringExtra("routeName");
            depName = intent.getStringExtra("depName");

        }

        txtDepName = (TextView)findViewById(R.id.txtDepName);
        txtRouteName = (TextView) findViewById(R.id.txtRouteName);
        btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
        edtMessage = (EditText) findViewById(R.id.edtMessage);
        rgPickDrop = (RadioGroup) findViewById(R.id.rgPickDrop);

        //set value
        txtRouteName.setText("Route: " + routeName);
        txtDepName.setText("Department: "+depName);

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
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    message = edtMessage.getText().toString().trim();
                    if (message == null || message.equals("") || message.equals("null")) {
                        Toast.makeText(context, "Message should not be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.execute(null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public class SendMessage extends AsyncTask<String, String, String> {
        private String response;
        private ProgressDialog progDailog;

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
                    Toast.makeText(context, "Failed to send notification!", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONArray array = new JSONArray(response);
                JSONObject statusObj = array.getJSONObject(0);
                JSONObject data = array.getJSONObject(1);

                String status = statusObj.getString("status");
                if (!status.equalsIgnoreCase("SUCCESS")) {
                    Toast.makeText(context, "Failed to send notification!", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Toast.makeText(context, "Notification sent successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, SelectCommunicationDetailsActivity.class);
                    ((Activity) context).startActivity(intent);
                    ((Activity) context).finish();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = util.sendCommunication(orgId, depId, gegId, pickupDrop, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
