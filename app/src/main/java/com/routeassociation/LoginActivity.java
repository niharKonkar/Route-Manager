package com.routeassociation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText uName, uPassword;
    private Button btnLogin;
    private String name, password;
    private int count = 0;
    private Context context;
    private Util util;
    private UserLoginTask mAuthTask = null;
    private boolean doubleBackToExitPressedOnce;
    private String status;
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private String result;
    private String params, androidId;
    private int usrId;
    private int ugpId;

    public boolean checkConnection(Context context) {
        boolean flag = false;
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = connectivityManager.getActiveNetworkInfo();

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                System.out.println(info.getTypeName());
                flag = true;
            }
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                System.out.println(info.getTypeName());
                flag = true;
            }
        } catch (Exception exception) {
            System.out.println("Exception at network connection....."
                    + exception);
        }
        return flag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            boolean checkConnection = checkConnection(this);
            if (checkConnection) {

            } else {
                Toast.makeText(LoginActivity.this,
                        "connection not found...plz check connection", Toast.LENGTH_LONG).show();
            }

            //check login details
            SharedPreferences loginDetails = getSharedPreferences("user", 0);
            final String finalResult = loginDetails.getString("params", "");
            Log.d("finalResult", "" + finalResult);

            if (finalResult.isEmpty() == false) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("params", finalResult);
                startActivity(intent);
            }

            //get reference
            context = this;
            uName = (EditText) findViewById(R.id.uName);
            uPassword = (EditText) findViewById(R.id.upassword);
            btnLogin = (Button) findViewById(R.id.btnLogin);

            //auto login after filling pwd
            uPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        btnLogin.performClick();
                        return true;
                    }
                    return false;
                }
            });

            //login
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean Flag = false;
                    name = uName.getText().toString().trim();
                    password = uPassword.getText().toString().trim();
                    uName.setError(null);
                    uPassword.setError(null);


                    if (name.equals("")) {
                        setErrorToEditText(uName, "Please enter username !!");
                        return;
                    }
                    if (password.equals("")) {
                        setErrorToEditText(uPassword, "Please enter password !!");
                        return;

                    }
                    if (name.equals("") && password.equals("")) {
                        setErrorToEditText(uName, "Please enter username !!");
                        setErrorToEditText(uPassword, "Please enter password !!");
                        return;
                    }
                    if (!Flag) {

                        mAuthTask = new UserLoginTask(name, password);
                        mAuthTask.execute((Void) null);

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //login
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private boolean error = false;
        private final String mEmail;
        private final String mPassword;
        public String finalResult;
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(context);
            pd.setTitle("Processing...");
            pd.setMessage("Please wait.");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();

        }

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {

                util = new Util(context);
                result = util.authenticateZicomUser(mEmail, mPassword);

            } catch (Exception e1) {
                e1.printStackTrace();
                error = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            pd.dismiss();

            try {

                if(result == null)
                {
                    Toast.makeText(context, "Error to save Android Id  !", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String status = jsonObject.getString("status");
                if (status.equals("SUCCESS")) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                    JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                    JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                    ugpId = jsonObject2.getInt("ugpId");
                    usrId = jsonObject2.getInt("usrId");

                    //check authentication for manager/admin
                    if (ugpId == 2 || ugpId == 3) {
                        SharedPreferences loginDetails = getSharedPreferences("user", 0);
                        loginDetails.edit().putString("params", result).commit();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("params", finalResult);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        SharedPreferences tokenDetails = getSharedPreferences("REG_TOKEN", 0);
                        androidId = tokenDetails.getString("token", "");
                        Log.d("TOKEN", "" + androidId);


                        //save data for notification

                        String response = util.setRouteManagerAndroidRegId(usrId, androidId);
                        Log.d("response", "" + response);

                        if (response == null) {

                            Toast.makeText(context, "Error to save Android Id  !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(context, "Authentication failed !!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } else {

                    Toast.makeText(context, "Please check your login details !", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (error) {

                    Toast.makeText(context, "Please check your internet connection..", Toast.LENGTH_SHORT).show();
                    return;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    protected void setErrorToEditText(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
        editText.requestFocus();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit ", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }
}
