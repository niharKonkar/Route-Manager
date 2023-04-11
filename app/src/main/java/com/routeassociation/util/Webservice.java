package com.routeassociation.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Webservice extends Thread {

    public String urlString;
    public byte[] b = new byte[4096 * 16];
    public String output;
    public Context context;
    public int usrId = 0;


    public Webservice(String urlString, Context context) {
        try {
            this.context = context;

            //load login details
            SharedPreferences loginDetails = context.getSharedPreferences("user", 0);
            String params = loginDetails.getString("params", null);

            if (params == null  || params.equals("")) {
            }else {
                JSONArray jsonArray = new JSONArray(params);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String status = jsonObject.getString("status");
                if (status.equals("SUCCESS")) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                    JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(0);

                    usrId = jsonObject2.getInt("usrId");

                }
            }


            StringBuilder sb = new StringBuilder();
            sb.append(urlString);
            sb.append("&USER_ID=").append(usrId);
            sb.append("&APP_NAME=").append(URLEncoder.encode("RouteManager", "UTF-8"));
            this.urlString = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String callRestfulWebService() {

        URL url = null;
        try {
            url = new URL(urlString);


            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                output = readStream(in);
            } finally {
                urlConnection.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;

    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        output = callRestfulWebService();
        super.run();
    }
}