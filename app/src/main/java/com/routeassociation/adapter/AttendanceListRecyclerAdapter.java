package com.routeassociation.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.routeassociation.AttendanceListDetailsActivity;
import com.routeassociation.LocationStepViewActivity;
import com.routeassociation.R;
import com.routeassociation.pojo.ConductorDetails;
import com.routeassociation.pojo.DriverDetails;
import com.routeassociation.pojo.RouteDetails;
import com.routeassociation.pojo.Student;
import com.routeassociation.pojo.VehicleDetails;
import com.routeassociation.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AttendanceListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;

    private LayoutInflater inflater;

    private ArrayList<VehicleDetails> liveList;
    int count = 0;
    private Dialog dialog2;
    private ListView listVehicles;
    private int vehId, geg;
    private String vehNum;
    private int orgId, depId, userId;
    private VehicleDetails vehicleDetails;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private RecyclerView recyRfidIdData;
    private TextView dataCount;
    private String resDriver;
    private String response;
    private int DataCount;
    private ProgressDialog progDailog;
    private JSONObject result, result3;
    private String params;
    private AttendanceListRecyclerAdapter adapter;
    private AutoCompleteTextView edtSearch;
    private String selectedDriver;
    private ArrayAdapter<VehicleDetails> arrayAdapter;
    private ArrayList<RouteDetails> mArrayList;
    private ArrayList<RouteDetails> mFilteredList;
    private Map<String, Integer> map;
    private ArrayList<String> locationList;
    private ArrayList<DriverDetails> driverDetailsArrayList;
    private Spinner driverSpinner;
    private int drvId, ugpId;
    private Dialog dialog3;
    private String pickDrop = "P";
    String pickDropBoth = "P";
    private String resConductorList, resAssignCond, resUnassignCond;
    private ArrayList<ConductorDetails> conductorDetailsArrayList;
    private int condId;
    private Spinner maidSpinner;
    private String pickDropRouteAnalysis = "P";
    private Util util;
    private RecyclerView mRecyclerView;
    private int dayId;
    private int gegId;
    private  String pickupDrop;
    String currentFormattedDate;

    List<Student> studentList;
    List<String> attendanceIds;

    public AttendanceListRecyclerAdapter(Context context, ArrayList<RouteDetails> data,int depId) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mArrayList = data;
        this.mFilteredList = data;
        this.depId = depId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.routeassociation, parent, false);
        AttendanceListRecyclerAdapter.MyHolder holder = new AttendanceListRecyclerAdapter.MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {

            util = new Util(context);
            //get login details
            SharedPreferences loginDetails = context.getSharedPreferences("user", 0);
            params = loginDetails.getString("params", null);

            JSONArray jsonArray = new JSONArray(params);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            final String status = jsonObject.getString("status");
            if (status.equals("SUCCESS")) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(1);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("data");
                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                orgId = jsonObject2.getInt("orgId");
//                depId = jsonObject2.getInt("depId");
                userId = jsonObject2.getInt("usrId");
                ugpId = jsonObject2.getInt("ugpId");

            }

            dialog3 = new Dialog(context);

            final AttendanceListRecyclerAdapter.MyHolder myHolder = (AttendanceListRecyclerAdapter.MyHolder) holder;
            final RouteDetails details = mArrayList.get(position);
            vehicleDetails = new VehicleDetails();

            SimpleDateFormat sdfFromDate = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            currentFormattedDate = sdfFromDate.format(date);

            myHolder.studbtn.setOnClickListener(v -> {
                gegId = details.getRouteId();
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Select Route Type");
                String[] list = {"Pickup","Drop"};
                int checkedItem = 0;
                alert.setSingleChoiceItems(list, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                pickDrop = "P";
                                break;
                            case 1:
                                pickDrop = "D";
                                break;
                        }
                    }
                });
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new LoadAttendanceDetails().execute();
                    }
                });
                alert.setNegativeButton("Cancel",null);

                AlertDialog show = alert.create();
                show.show();
            });

            try {
                //route name
                myHolder.routeName.setText("" + details.getRouteName());

                //vehicle number
                myHolder.associatedVehicle.setText("" + details.getVehNumber());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class LoadAttendanceDetails extends AsyncTask<String, String, String> {
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
                response = util.attendanceList(orgId, depId, currentFormattedDate, gegId, pickDrop);
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
                    studentList = new ArrayList<>();
                    attendanceIds = new ArrayList<String>();

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject object = jsonArray.getJSONObject(0);
                        JSONObject jsonObject = jsonArray.getJSONObject(1);

                        String status = object.getString("status");
                        if (status.equals("SUCCESS")) {
                            JSONArray array = jsonObject.getJSONArray("data");
                            JSONObject studObject = array.getJSONObject(0);
                            JSONArray studArray = studObject.getJSONArray("routeAssetDetails");
                            for (int x = 0; x < studArray.length(); x++) {
                                JSONObject studNameObject = studArray.getJSONObject(x);
                                Student student = new Student();
                                student.setName(studNameObject.getString("RasName"));
                                student.setId(studNameObject.getInt("RasId"));
                                studentList.add(student);
                            }
                            JSONObject studObject1 = array.getJSONObject(1);
                            String idstr = studObject1.getString("assetAttdDetails");
                            attendanceIds = Arrays.asList(idstr.split(","));

                            if (attendanceIds.size() >0 && studentList.size() > 0) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                LayoutInflater inflater = LayoutInflater.from(context);
                                View content = inflater.inflate(R.layout.show_studattendancelist, null);
                                builder.setView(content);

                                Toolbar toolbar;
                                toolbar = content.findViewById(R.id.toolbar);
                                toolbar.setTitle("Student Details");
                                toolbar.setTitleTextColor(Color.WHITE);

                                mRecyclerView = (RecyclerView) content.findViewById(R.id.studAttList);

//                                studentList();

                                AlertDialog alertDialog = builder.show();

                                ImageButton cancelImgBtn = toolbar.findViewById(R.id.cancelImgBtn);
                                cancelImgBtn.setVisibility(View.VISIBLE);
                                cancelImgBtn.setOnClickListener(v1 -> {
                                    alertDialog.dismiss();
                                });
                                //set adapter
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                                mRecyclerView.setAdapter(new StudentAttendanceAdapter(context, studentList, attendanceIds));
                            }else {
                                Toast.makeText(context, "Attendance Details not Found...", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Attendance Details not found !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Attendance Details not found !", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView routeName, associatedVehicle;


        FloatingActionButton studbtn;

        public MyHolder(View itemView) {
            super(itemView);

            routeName = (TextView) itemView.findViewById(R.id.routeName);
            associatedVehicle = (TextView) itemView.findViewById(R.id.associatedVehicle);
            studbtn = (FloatingActionButton) itemView.findViewById(R.id.studbtn);
        }
    }

}