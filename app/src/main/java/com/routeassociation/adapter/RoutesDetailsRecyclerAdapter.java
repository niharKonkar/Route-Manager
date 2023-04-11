package com.routeassociation.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.routeassociation.LocationStepViewActivity;
import com.routeassociation.MainActivity;
import com.routeassociation.MapsActivity;
import com.routeassociation.R;
import com.routeassociation.RouteAnalysisDetailsActivity;
import com.routeassociation.RoutesDetailsAndInfoActivity;
import com.routeassociation.pojo.ConductorDetails;
import com.routeassociation.pojo.DriverDetails;
import com.routeassociation.pojo.RfidData;
import com.routeassociation.pojo.RouteDetails;
import com.routeassociation.pojo.VehicleDetails;
import com.routeassociation.util.Util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RoutesDetailsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    private JSONArray response, rfidDataArray, res;
    private int DataCount;
    private ProgressDialog progDailog;
    private JSONObject result, result3;
    private String params;
    private RfidDataAdapter adapter;
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
    private  Util util;

    public RoutesDetailsRecyclerAdapter(Context context, ArrayList<RouteDetails> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mArrayList = data;
        this.mFilteredList = data;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_route_association, parent, false);
        RoutesDetailsRecyclerAdapter.MyHolder holder = new RoutesDetailsRecyclerAdapter.MyHolder(view);
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
                depId = RoutesDetailsAndInfoActivity.depId;

            }

            dialog2 = new Dialog(context);
            final RoutesDetailsRecyclerAdapter.MyHolder myHolder = (RoutesDetailsRecyclerAdapter.MyHolder) holder;
            final RouteDetails details = mArrayList.get(position);
            vehicleDetails = new VehicleDetails();

            if (ugpId == 1 || ugpId == 2 || ugpId == 3) {

                myHolder.btnAssignVehicle.setClickable(true);
                myHolder.btnDisassociate.setClickable(true);
                myHolder.btnAssignVehicle.setBackgroundResource(R.drawable.rect);
                myHolder.btnDisassociate.setBackgroundResource(R.drawable.rect);

            } else {

                myHolder.btnAssignVehicle.setClickable(false);
                myHolder.btnDisassociate.setClickable(false);
                myHolder.btnAssignVehicle.setEnabled(false);
                myHolder.btnDisassociate.setEnabled(false);
                myHolder.btnAssignVehicle.setBackgroundResource(R.drawable.rect_gray);
                myHolder.btnDisassociate.setBackgroundResource(R.drawable.rect_gray);
            }

            myHolder.layShowHide.setOnClickListener(v -> {
                if (myHolder.layExpLayout.getVisibility() == View.VISIBLE) {
                    myHolder.layExpLayout.setVisibility(View.GONE);
                    myHolder.imgArrow.setImageResource(R.drawable.arrow_down);
                } else {
                    myHolder.layExpLayout.setVisibility(View.VISIBLE);
                    myHolder.imgArrow.setImageResource(R.drawable.up_arrow);
                }
            });

            //bus capacity
            if (details.getBusCapacity().equals("null") || details.getBusCapacity() == null || details.getBusCapacity().equals("")) {
                ((MyHolder) holder).txtBusCapacity.setText("Vehicle Seat Capacity : - ");
            } else {
                ((MyHolder) holder).txtBusCapacity.setText("Vehicle Seat Capacity: " + details.getBusCapacity());
            }

            //pickup student count
            if (details.getPickupStudentCount().equals("") || details.getPickupStudentCount() == null || details.getPickupStudentCount().equals("null")) {
                ((MyHolder) holder).txtPickupStudentCount.setText("Pickup Student Count : -");

            } else {
                ((MyHolder) holder).txtPickupStudentCount.setText("Pickup Student Count : " + details.getPickupStudentCount());

            }

            //drop student count
            if (details.getDropStudentCount().equals("") || details.getDropStudentCount() == null || details.getDropStudentCount().equals("null")) {
                ((MyHolder) holder).txtDropStudentCount.setText("Drop Student Count : -");

            } else {
                ((MyHolder) holder).txtDropStudentCount.setText("Drop Student Count : " + details.getDropStudentCount());

            }
            //pickup teacher count
            if (details.getPickupTeacherCount().equals("") || details.getPickupTeacherCount() == null || details.getPickupTeacherCount().equals("null")) {
                ((MyHolder) holder).txtPickupTeacherCount.setText("Pickup Staff Count : -");

            } else {
                ((MyHolder) holder).txtPickupTeacherCount.setText("Pickup Staff Count : " + details.getPickupTeacherCount());

            }

            //drop teacher count
            if (details.getDropTeacherCount().equals("") || details.getDropTeacherCount() == null || details.getDropTeacherCount().equals("null")) {
                ((MyHolder) holder).txtDropTeacherCount.setText("Drop Staff Count : -");

            } else {
                ((MyHolder) holder).txtDropTeacherCount.setText("Drop Staff Count : " + details.getDropTeacherCount());

            }

            //show route details
            myHolder.btnRouteDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog2.setContentView(R.layout.dialog_choose_pick_drop_route_analysis);
                        dialog2.show();
                        Window window = dialog2.getWindow();
                        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);


                        final RadioGroup rgPickDrop = (RadioGroup) dialog2.findViewById(R.id.rgPickDrop);
                        rgPickDrop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if (checkedId == R.id.rbPickup) {
                                    pickDropRouteAnalysis = "P";
                                } else if (checkedId == R.id.rbDrop) {
                                    pickDropRouteAnalysis = "D";
                                }
                            }
                        });


                        Button btnOk = (Button) dialog2.findViewById(R.id.btnOk);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    dialog2.dismiss();
                                    if (pickDropRouteAnalysis == null || pickDropRouteAnalysis.equals("")) {
                                        Toast.makeText(context, "Please select the pick/drop to view route analysis!", Toast.LENGTH_SHORT).show();
                                    } else {

                                        Intent intent = new Intent(context, RouteAnalysisDetailsActivity.class);
                                        intent.putExtra("routeId", details.getRouteId());
                                        intent.putExtra("routeName", details.getRouteName());
                                        intent.putExtra("pickDropRouteAnalysis", pickDropRouteAnalysis);
                                        ((Activity) context).startActivity(intent);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {
                myHolder.main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myHolder.lay1.getVisibility() == View.VISIBLE || myHolder.lay2.getVisibility() == View.VISIBLE) {
                            myHolder.lay1.setVisibility(View.GONE);
                            myHolder.lay2.setVisibility(View.GONE);

                        } else {
                            myHolder.lay1.setVisibility(View.VISIBLE);
                            myHolder.lay2.setVisibility(View.VISIBLE);
                        }
                    }
                });


                //route name
                myHolder.routeName.setText("" + details.getRouteName());
                //route desc
                if (details.getConductorName().equals("null") || details.getConductorName().equals("") || details.getRouteDesc().equals("-") || details.getRouteDesc() == null) {
                    myHolder.routeDesc.setText("Route Description : - ");
                } else {
                    myHolder.routeDesc.setText("Route Description : " + details.getRouteDesc());
                }

                //pick up conductor name
                if (details.getConductorName().equals("null") || details.getConductorName() == null || details.getConductorName().equals("")) {
                    myHolder.associatedConductor.setText("Pickup Conductor : -");

                } else {

                    myHolder.associatedConductor.setText("Pickup Conductor : " + details.getConductorName());
                }

                //pickup driver name
                if (details.getDriverName() == null || details.getDriverName().equals("null") || details.getDriverName().equals("")) {
                    myHolder.associatedDriver.setText("Pickup Driver : - ");

                } else {
                    myHolder.associatedDriver.setText("Pickup Driver : " + details.getDriverName());
                }

                //drop driver name
                if (details.getDropDriverName() == null || details.getDropDriverName().equals("") || details.getDropDriverName().equals("null")) {
                    myHolder.associateDropdDriver.setText("Drop Driver : - ");
                } else {
                    myHolder.associateDropdDriver.setText("Drop Driver : " + details.getDropDriverName());

                }

                //drop conductor name
                if (details.getDropConductorName().equals("") || details.getDropConductorName().equals("null") || details.getDropConductorName() == null) {
                    myHolder.associatedDropConductor.setText("Drop Conductor : -");
                } else {
                    myHolder.associatedDropConductor.setText("Drop Conductor : " + details.getDropConductorName());
                }
                if (details.getVehNumber().equals("0")) {
                    myHolder.associatedVehicle.setText("Vehicle not assigned");
                    myHolder.btnDisassociate.setVisibility(View.GONE);
                } else {
                    myHolder.associatedVehicle.setText("" + details.getVehNumber());
                    myHolder.btnDisassociate.setVisibility(View.VISIBLE);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            //assign driver
            myHolder.btnAssignDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.setContentView(R.layout.dialog_confirm_change_driver);
                    dialog2.show();
                    Window window = dialog2.getWindow();
                    window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                    final RadioGroup rgGroup = (RadioGroup) dialog2.findViewById(R.id.rgGroup);
                    driverSpinner = (Spinner) dialog2.findViewById(R.id.driverSpinner);
                    loadDriverOnSpinner();
                    Button btnChange = (Button) dialog2.findViewById(R.id.btnChange);

                    //btnChange
                    btnChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                dialog2.dismiss();
                                if (drvId == 0) {
                                    Toast.makeText(context, "Please select valid driver for route association!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (rgGroup.getCheckedRadioButtonId() == R.id.rbPickup) {

                                    pickDrop = "P";
                                } else {
                                    pickDrop = "D";
                                }
                                String resChangeDriver = util.changeRouteDriverAssociation(orgId, depId, details.getRouteId(), drvId, pickDrop,userId);

                                if (resChangeDriver != null) {
                                    JSONObject object = new JSONObject(resChangeDriver);
                                    String status = object.getString("status");
                                    if (status.equals("SUCCESS")) {
                                        Toast.makeText(context, selectedDriver + "Associate successfully with" + details.getRouteName(), Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(context, MainActivity.class);
//                                        ((Activity) context).startActivity(intent);
//                                        ((Activity) context).finish();
                                        ((RoutesDetailsAndInfoActivity)context).refreshActivity();
                                    } else {
                                        Toast.makeText(context, "Fail to associate driver with route!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            //unassign driver
            myHolder.btnUnassignDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.setContentView(R.layout.dialog_confirm_disassociate_driver);
                    dialog2.show();
                    Window window = dialog2.getWindow();
                    window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                    final RadioGroup rgGroup = (RadioGroup) dialog2.findViewById(R.id.rgGroup);
                    TextView btnYes = (TextView) dialog2.findViewById(R.id.btnYes);
                    TextView btnNo = (TextView) dialog2.findViewById(R.id.btnNo);

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                            try {
                                if (rgGroup.getCheckedRadioButtonId() == R.id.rbPickup) {

                                    pickDrop = "P";
                                } else {
                                    pickDrop = "D";
                                }
                                String resChangeDriver = util.changeRouteDriverAssociation(orgId, depId, details.getRouteId(), 0, pickDrop,userId);

                                if (resChangeDriver != null) {
                                    JSONObject object = new JSONObject(resChangeDriver);
                                    String status = object.getString("status");
                                    if (status.equals("SUCCESS")) {
                                        Toast.makeText(context, "dis-associate driver successfully " + details.getRouteName(), Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(context, MainActivity.class);
//                                        ((Activity) context).startActivity(intent);
//                                        ((Activity) context).finish();
                                        ((RoutesDetailsAndInfoActivity)context).refreshActivity();
                                    } else {
                                        Toast.makeText(context, "Fail to associate driver with route!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });


                }
            });


            //assign vehicle
            myHolder.btnAssignVehicle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    dialog2.setContentView(R.layout.dialog_vehicle_list);
                    dialog2.show();
                    Window window = dialog2.getWindow();
                    window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                    edtSearch = (AutoCompleteTextView) dialog2.findViewById(R.id.edtSearch);
                    Button btnAssignVehicle = (Button) dialog2.findViewById(R.id.btnAssignVehicle);

                    geg = details.getRouteId();

                    //load vehicle list
                    try {
                        LoadVehicleData mAsyync = new LoadVehicleData();
                        mAsyync.execute(null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    //assign

                    btnAssignVehicle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                dialog2.dismiss();
                                vehId = map.get(edtSearch.getText().toString());

                                AssignVehicle mAsyync = new AssignVehicle();
                                mAsyync.execute(null, null);


                            } catch (Exception e) {
                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });


            //dis associate vehicle
            myHolder.btnDisassociate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        geg = details.getRouteId();


                        dialog2.setContentView(R.layout.dialog_confirm_disassociate_vehicle);
                        dialog2.show();
                        Window window = dialog2.getWindow();
                        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                        TextView btnYes = (TextView) dialog2.findViewById(R.id.btnYes);
                        TextView btnNo = (TextView) dialog2.findViewById(R.id.btnNo);

                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                                DisassociateVehicle mAsyync = new DisassociateVehicle();
                                mAsyync.execute(null, null);

                            }
                        });

                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                            }
                        });


                    } catch (Exception e) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });


            //show current location
            myHolder.btnLiveMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {


                        int vehId = details.getVehId();

                        String response = util.getLatestLocationByVehId(vehId);

                        if (response == null) {
                            Toast.makeText(context, "Vehicle location not found !", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        if (status.equals("SUCCESS")) {
                            double lat = object.getDouble("lat");
                            double lng = object.getDouble("lng");
                            String address = object.getString("address");

                            Intent intent = new Intent(context, MapsActivity.class);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lng", lng);
                            intent.putExtra("address", address);
                            intent.putExtra("vehNumber", details.getVehNumber());
                            intent.putExtra("vehId", vehId);
                            context.startActivity(intent);

                        } else {
                            Toast.makeText(context, "Vehicle location not found !", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            //call driver conductor
            myHolder.btnCallDrvCond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.setContentView(R.layout.dialog_confirm_call_driver_conductor);
                    dialog2.show();
                    Window window = dialog2.getWindow();
                    window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                    TextView txtDrvName = (TextView) dialog2.findViewById(R.id.txtDrvName);
                    ImageView btnCallDriver = (ImageView) dialog2.findViewById(R.id.btnCallDriver);

                    TextView txtCondName = (TextView) dialog2.findViewById(R.id.txtCondName);
                    ImageView btnCallCond = (ImageView) dialog2.findViewById(R.id.btnCallCond);

                    TextView txtDropDrvName = (TextView) dialog2.findViewById(R.id.txtDropDrvName);
                    ImageView btnCallDropDriver = (ImageView) dialog2.findViewById(R.id.btnCallDropDriver);

                    TextView txtDropCondName = (TextView) dialog2.findViewById(R.id.txtDropCondName);
                    ImageView btnCallDropCond = (ImageView) dialog2.findViewById(R.id.btnCallDropCond);

                    Button btnCancel = (Button) dialog2.findViewById(R.id.btnCancel);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });

                    //pickup driver
                    if (details.getDriverName().equals("") || details.getDriverName() == null) {
                        txtDrvName.setText("Pickup Driver : - ");
                    } else {
                        txtDrvName.setText("Pickup Driver : " + details.getDriverName() + "(" + details.getDriverNumber() + ")");
                    }
                    //pickup conductor
                    if (details.getConductorName().equals("") || details.getConductorName() == null) {
                        txtCondName.setText("Pickup Conductor : - ");
                    } else {
                        txtCondName.setText("Pickup Conductor : " + details.getConductorName() + "(" + details.getConductorNumber() + ")");
                    }

                    //drop driver
                    if (details.getDropDriverName().equals("") || details.getDropDriverName() == null) {

                        txtDropDrvName.setText("Drop Driver : - ");
                    } else {

                        txtDropDrvName.setText("Drop Driver : " + details.getDropDriverName() + "(" + details.getDropDiverNumber() + ")");
                    }

                    //drop conductor
                    if (details.getDropConductorName().equals("") || details.getDropConductorName() == null) {
                        txtDropCondName.setText("Drop Conductor : -");
                    } else {
                        txtDropCondName.setText("Drop Conductor : " + details.getDropConductorName() + "(" + details.getDropConductorNumber() + ")");
                    }


                    //call driver
                    btnCallDriver.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                            if (details.getDriverNumber().equals("") || details.getDriverNumber() == null) {
                                Toast.makeText(context, "Unable to call driver!", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + details.getDriverNumber()));
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                context.startActivity(intent);
                            }
                        }
                    });
                    //call conductor
                    btnCallCond.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                            if (details.getConductorNumber().equals("") || details.getConductorNumber() == null) {
                                Toast.makeText(context, "Unable to call conductor!", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + details.getConductorNumber()));
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                context.startActivity(intent);
                            }
                        }
                    });

                    //call drop driver
                    btnCallDropDriver.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();

                            if (details.getDropDiverNumber().equals("") || details.getDropDiverNumber() == null) {
                                Toast.makeText(context, "Unable to call driver!", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + details.getDropDiverNumber()));
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                context.startActivity(intent);
                            }
                        }
                    });

                    //call drop conductor
                    btnCallDropCond.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                            if (details.getDropConductorNumber().equals("") || details.getDropConductorNumber() == null) {
                                Toast.makeText(context, "Unable to call conductor!", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + details.getDropConductorNumber()));
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                context.startActivity(intent);
                            }
                        }
                    });
                }
            });

         /*   //show details student,teacher count and bus capacity

            myHolder.btnShowDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ShowCountDetailsAsPerRouteActivity.class);
                    intent.putExtra("routeName",details.getRouteName());
                    intent.putExtra("routeId",details.getRouteId());
                    ((Activity)context).startActivity(intent);

                }
            });*/

            //show current location
            myHolder.btnLiveMap2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {


                        int vehId = details.getVehId();

                        String response = util.getLatestLocationByVehId(vehId);

                        if (response == null) {
                            Toast.makeText(context, "Vehicle location not found !", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        if (status.equals("SUCCESS")) {
                            double lat = object.getDouble("lat");
                            double lng = object.getDouble("lng");
                            String address = object.getString("address");

                            Intent intent = new Intent(context, MapsActivity.class);
                            intent.putExtra("lat", lat);
                            intent.putExtra("lng", lng);
                            intent.putExtra("address", address);
                            intent.putExtra("vehNumber", details.getVehNumber());
                            intent.putExtra("vehId", vehId);
                            context.startActivity(intent);

                        } else {
                            Toast.makeText(context, "Vehicle location not found !", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


            //show events
            myHolder.btnTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Intent intent = new Intent(context, LocationStepViewActivity.class);
                        intent.putExtra("vehName", details.getVehNumber());
                        intent.putExtra("routeName", details.getRouteName());
                        intent.putExtra("vehId", details.getVehId());

                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //show events
            myHolder.btnTrack2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Intent intent = new Intent(context, LocationStepViewActivity.class);
                        intent.putExtra("vehName", details.getVehNumber());
                        intent.putExtra("routeName", details.getRouteName());
                        intent.putExtra("vehId", details.getVehId());

                        context.startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            //show rfid
            String showRfidFlag = details.getShowRfidFlag();
            if (showRfidFlag != null) {
                if (showRfidFlag.equals("Y")) {

                    //  ((MyHolder) holder).btnRfidData.setVisibility(View.VISIBLE);
                    ((MyHolder) holder).layShowRfidData.setVisibility(View.VISIBLE);
                    ((MyHolder) holder).layNotShowRfidData.setVisibility(View.GONE);
                } else {
                    // ((MyHolder) holder).btnRfidData.setVisibility(View.GONE);
                    ((MyHolder) holder).layShowRfidData.setVisibility(View.GONE);
                    ((MyHolder) holder).layNotShowRfidData.setVisibility(View.VISIBLE);
                }
            }


            myHolder.btnRfidData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        vehId = details.getVehId();
                        Log.d("Vehicle Id", "" + vehId);

                        new ShowRfidData().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            //unassign conductor
            myHolder.btnUnassignConductor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        geg = details.getRouteId();
                        dialog2.setContentView(R.layout.dialog_confirm_disassociate_conductor);
                        dialog2.show();
                        Window window = dialog2.getWindow();
                        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                        TextView btnYes = (TextView) dialog2.findViewById(R.id.btnYes);
                        TextView btnNo = (TextView) dialog2.findViewById(R.id.btnNo);
                        final RadioGroup rgGroup = (RadioGroup) dialog2.findViewById(R.id.rgGroup);


                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                                if (rgGroup.getCheckedRadioButtonId() == R.id.rbPickup) {

                                    pickDrop = "P";
                                } else {
                                    pickDrop = "D";
                                }
                                UnassignConductor unassignConductor = new UnassignConductor();
                                unassignConductor.execute(null, null);

                            }
                        });

                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //assign conductor
            myHolder.btnAssignConductor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        geg = details.getRouteId();
                        dialog2.setContentView(R.layout.dialog_confirm_change_maid);
                        dialog2.show();
                        Window window = dialog2.getWindow();
                        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                        final RadioGroup rgGroup = (RadioGroup) dialog2.findViewById(R.id.rgGroup);


                        maidSpinner = (Spinner) dialog2.findViewById(R.id.maidSpinner);

                        Button btnChange = (Button) dialog2.findViewById(R.id.btnChange);

                        //load conductor list
                        LoadConductorList loadConductorList = new LoadConductorList();
                        loadConductorList.execute(null, null);


                        maidSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                condId = conductorDetailsArrayList.get(position).getMaidId();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                        btnChange.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if (condId != 0) {
                                        if (rgGroup.getCheckedRadioButtonId() == R.id.rbPickup) {

                                            pickDrop = "P";
                                        } else {
                                            pickDrop = "D";
                                        }
                                        AssignConductor assignConductor = new AssignConductor();
                                        assignConductor.execute(null, null);

                                    } else {
                                        Toast.makeText(context, "Please select conductor!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            
          


         /*  /* myHolder.btnChangeDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    dialog2.setContentView(R.layout.dialog_confirm_change_driver);
                    dialog2.show();
                    Window window = dialog2.getWindow();
                    window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                    driverSpinner = (Spinner) dialog2.findViewById(R.id.driverSpinner);
                    loadDriverOnSpinner();
                    Button btnChange = (Button) dialog2.findViewById(R.id.btnChange);
                    Button btnCall = (Button) dialog2.findViewById(R.id.btnCall);

                    //btnChange
                    btnChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                dialog2.dismiss();
                                if (drvId == 0) {
                                    Toast.makeText(context, "Please select valid driver for route association!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String resChangeDriver = util.changeRouteDriverAssociation(orgId, depId, details.getRouteId(), drvId, "P");

                                if (resChangeDriver != null) {
                                    JSONObject object = new JSONObject(resChangeDriver);
                                    String status = object.getString("status");
                                    if (status.equals("SUCCESS")) {
                                        Toast.makeText(context, selectedDriver + "associate successfully with" + details.getRouteName(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, MainActivity.class);
                                        ((Activity) context).startActivity(intent);
                                        ((Activity) context).finish();
                                    } else {
                                        Toast.makeText(context, "Fail to associate driver with route!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    //btn call
                    btnCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                dialog2.dismiss();
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "8308990460"));
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
            });
            */

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //load driver
    public void loadDriverOnSpinner() {
        try {
            resDriver = util.getDriverList(orgId, depId);
            if (resDriver != null) {
                JSONArray jsonArray = new JSONArray(resDriver);
                JSONObject object1 = jsonArray.getJSONObject(0);
                String status = object1.getString("status");
                if (status.equals("SUCCESS")) {
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
                        driverDetailsArrayList.add(driverDetails);
                    }
                    ArrayAdapter<DriverDetails> spinnerData = new ArrayAdapter<DriverDetails>(context, R.layout.support_simple_spinner_dropdown_item, driverDetailsArrayList);
                    driverSpinner.setAdapter(spinnerData);

                }


            } else {
                Toast.makeText(context, "No data available for driver!", Toast.LENGTH_SHORT).show();
                return;
            }
            driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectedDriver = driverSpinner.getSelectedItem().toString();
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

    //get rfid data
    class ShowRfidData extends AsyncTask<String, Void, String> {
        ArrayList<RfidData> rfidDataArrayList;

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "Processing...", "loading rfid data...", true, false);
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_rfid_data);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
            dataCount = (TextView) dialog.findViewById(R.id.dataCount);

            recyRfidIdData = (RecyclerView) dialog.findViewById(R.id.recyRfidIdData);

            // recyRfidIdData.setLayoutManager(new LinearLayoutManager(VehDevStatusActivity.this));
            recyRfidIdData.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            recyRfidIdData.setLayoutManager(layoutManager);
            // recyRfidIdData.getRecycledViewPool().setMaxRecycledViews(0, 0);

        }

        @Override
        protected String doInBackground(String... params) {
            try {


                try {
                    rfidDataArrayList = new ArrayList<RfidData>();
                    response = util.getVehiclewiseRfidData(vehId);

                    // response.getString("status");
                    if (response != null) {

                        JSONObject jsonObject1 = response.getJSONObject(0);
                        String status = jsonObject1.getString("status");
                        if (status.equals("SUCCESS")) {
                            JSONObject jsonObject2 = response.getJSONObject(1);

                            rfidDataArray = jsonObject2.getJSONArray("data");

                            if (rfidDataArray == null) {

                                dialog.dismiss();
                                return null;
                            }


                            DataCount = rfidDataArray.length();
                            for (int i = 0; i < DataCount; i++) {
                                RfidData rfidData = new RfidData();
                                JSONObject jsonObject = rfidDataArray.getJSONObject(i);
                                rfidData.setRasName(jsonObject.getString("rasName"));
                                rfidData.setRasCard(jsonObject.getString("rasCard"));
                                rfidData.setRtnViolation(jsonObject.getString("rtnViolation"));
                                rfidData.setRtnTimestamp(jsonObject.getString("rtnTimestamp"));
                                rfidDataArrayList.add(rfidData);
                            }
                        } else {
                            progressDialog.dismiss();
                            //Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    } else {
                        progressDialog.dismiss();
                        //  Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String msg) {

            try {
                progressDialog.dismiss();
                if (response != null) {
                    dataCount.setText("TOTAL COUNT : " + DataCount);
                    adapter = new RfidDataAdapter(rfidDataArrayList);
                    recyRfidIdData.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(context, "Rfid data not found !", Toast.LENGTH_SHORT).show();
                }
                if (rfidDataArray == null) {
                    Toast.makeText(context, "Rfid data not found !", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AssignConductor extends AsyncTask<String, String, String> {
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
                if (resAssignCond == null || resAssignCond.equals("")) {
                    Toast.makeText(context, "Failed to assign conductor!", Toast.LENGTH_SHORT).show();

                    return;
                } else {
                    if (resAssignCond != null) {
                        JSONObject object = new JSONObject(resAssignCond);
                        String status = object.getString("status");
                        if (status.equals("SUCCESS")) {
                            Toast.makeText(context, "Successfully assigned conductor!", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, selectedConductor + "associate successfully with" + details.getRouteName(), Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(context, MainActivity.class);
//                            ((Activity) context).startActivity(intent);
//                            ((Activity) context).finish();
                            ((RoutesDetailsAndInfoActivity)context).refreshActivity();
                        } else {
                            Toast.makeText(context, "Failed to associate conductor with route!", Toast.LENGTH_SHORT).show();
                            return;
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

                resAssignCond = util.changeRouteConductorAssociation(orgId, depId, geg, condId, pickDrop);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class UnassignConductor extends AsyncTask<String, String, String> {
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
                if (resUnassignCond.equals("") || resUnassignCond == null) {
                    Toast.makeText(context, "Failed to unassign conductor!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (resUnassignCond != null) {
                        JSONObject object = new JSONObject(resUnassignCond);
                        String status = object.getString("status");
                        if (status.equals("SUCCESS")) {
                            Toast.makeText(context, "Successfully unassigned conductor!", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(context, selectedConductor + "associate successfully with" + details.getRouteName(), Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(context, MainActivity.class);
//                            ((Activity) context).startActivity(intent);
//                            ((Activity) context).finish();
                            ((RoutesDetailsAndInfoActivity)context).refreshActivity();
                        } else {
                            Toast.makeText(context, "Failed to unassign conductor with route!", Toast.LENGTH_SHORT).show();
                            return;
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

                resUnassignCond = util.changeRouteConductorAssociation(orgId, depId, geg, 0, pickDrop);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class LoadConductorList extends AsyncTask<String, String, String> {
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

                if (resConductorList == null || resConductorList.equals("")) {
                    Toast.makeText(context, "Unable to load conductors!", Toast.LENGTH_SHORT).show();
                } else {
                    JSONArray array = new JSONArray(resConductorList);
                    JSONObject object1 = array.getJSONObject(0);
                    conductorDetailsArrayList = new ArrayList<ConductorDetails>();
                    String status = object1.getString("status");
                    if (status.equals("SUCCESS")) {
                        JSONObject object2 = array.getJSONObject(1);
                        JSONArray jsonArray = object2.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ConductorDetails conductorDetails = new ConductorDetails();
                            JSONObject object = jsonArray.getJSONObject(i);
                            conductorDetails.setMaidId(object.getInt("maidId"));
                            conductorDetails.setMaidName(object.getString("maidName"));
                            conductorDetails.setMaidAddress(object.getString("maidAddress"));
                            conductorDetails.setMaidContact(object.getString("maidContact"));
                            conductorDetailsArrayList.add(conductorDetails);
                        }
                        if (conductorDetailsArrayList.size() != 0) {
                            ArrayAdapter<ConductorDetails> spinnerData = new ArrayAdapter<ConductorDetails>(context, R.layout.support_simple_spinner_dropdown_item, conductorDetailsArrayList);
                            maidSpinner.setAdapter(spinnerData);

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
                resConductorList = util.getMaidList(orgId, depId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    //disassociate vehicle
    class DisassociateVehicle extends AsyncTask<String, String, String> {
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
                result3 = util.updateVehiclesAssociations(orgId, depId, geg, 0, userId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            try {

                if (result3 == null) {
                    Toast.makeText(context, "Disassociation failed !", Toast.LENGTH_LONG).show();
                    return;
                }
                String result = result3.getString("status");

                if (result.equals("SUCCESS")) {
                    Toast.makeText(context, "Vehicle disassociated successfully !", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(context, MainActivity.class);
//                    context.startActivity(intent);
//                    ((Activity) context).finish();
                    ((RoutesDetailsAndInfoActivity)context).refreshActivity();
                } else {
                    Toast.makeText(context, "Disassociation failed !", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(context, MainActivity.class);
//                    context.startActivity(intent);
//                    ((Activity) context).finish();
                }

                progDailog.dismiss();
            } catch (Exception e) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    //load vehicle data
    class LoadVehicleData extends AsyncTask<String, String, String> {
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
                int userDepId;
                if (ugpId == 2) {
                    userDepId = 0;
                } else {
                    userDepId = depId;
                }

                res = util.getAllVehicles(orgId, userDepId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            try {
                if (res == null) {
                    Toast.makeText(context, "Vehicles not found !", Toast.LENGTH_LONG).show();
                } else {

                    liveList = new ArrayList<VehicleDetails>();
                    map = new HashMap<String, Integer>();
                    for (int i = 0; i < res.length(); i++) {
                        try {
                            JSONObject jsonObject = res.getJSONObject(i);
                            vehicleDetails = new VehicleDetails();
                            vehicleDetails.setVehId(jsonObject.getInt("vehId"));
                            vehicleDetails.setVehName(jsonObject.getString("vehNumber"));
                            map.put(jsonObject.getString("vehNumber"), jsonObject.getInt("vehId"));
                            liveList.add(vehicleDetails);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    arrayAdapter = new ArrayAdapter<VehicleDetails>(
                            context,
                            android.R.layout.simple_list_item_1,
                            liveList);

                    edtSearch.setAdapter(arrayAdapter);


                }
                progDailog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //assign vehicle
    class AssignVehicle extends AsyncTask<String, String, String> {
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
                result = util.updateVehiclesAssociations(orgId, depId, geg, vehId, userId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            try {
                if (result == null) {

                    Toast.makeText(context, "Association failed !", Toast.LENGTH_LONG).show();
                    return;

                }
                String status = result.getString("status");
                if (status.equals("SUCCESS")) {
                    Toast.makeText(context, "Vehicle assigned successfully !", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(context, MainActivity.class);
//                    context.startActivity(intent);
//                    ((Activity) context).finish();
                    ((RoutesDetailsAndInfoActivity)context).refreshActivity();
                } else {

                    Toast.makeText(context, "Association failed !", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(context, MainActivity.class);
//                    context.startActivity(intent);
//                    ((Activity) context).finish();

                }


                progDailog.dismiss();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        TextView routeId, routeName, associatedVehicle, associatedDriver, associatedConductor;

        Button btnLiveMap2, btnRouteDetails, btnAssignVehicle, btnDisassociate, btnLiveMap, btnRfidData, btnAssignConductor, btnUnassignConductor;
        FloatingActionButton main;

        private Button btnTrack, btnChangeDriver;
        private Button btnTrack2, btnCallDrvCond, btnUnassignDriver, btnAssignDriver;
        private LinearLayout layNotShowRfidData, layShowRfidData, lay1, lay2, layAssignUnassignVeh;
        private FloatingActionButton b4;
        private LinearLayout layExpLayout, layShowHide;
        private ImageView imgArrow;
        private TextView associateDropdDriver, associatedDropConductor, routeDesc;
        private Spinner btnSetting;
        private CheckBox checkBox;

        private TextView txtPickupTeacherCount, txtDropTeacherCount, txtPickupStudentCount, txtDropStudentCount, txtBusCapacity;

        public MyHolder(View itemView) {
            super(itemView);

           // btnSetting = (Spinner) itemView.findViewById(R.id.btnSetting);
            associatedDropConductor = (TextView) itemView.findViewById(R.id.associatedDropConductor);
            associateDropdDriver = (TextView) itemView.findViewById(R.id.associateDropdDriver);
            imgArrow = (ImageView) itemView.findViewById(R.id.imgArrow);
            layShowHide = (LinearLayout) itemView.findViewById(R.id.layShowHide);
            layExpLayout = (LinearLayout) itemView.findViewById(R.id.layExpLayout);
            layAssignUnassignVeh = (LinearLayout) itemView.findViewById(R.id.layAssignUnassignVeh);
            routeName = (TextView) itemView.findViewById(R.id.routeName);
            associatedVehicle = (TextView) itemView.findViewById(R.id.associatedVehicle);
            associatedConductor = (TextView) itemView.findViewById(R.id.associatedConductor);
            associatedDriver = (TextView) itemView.findViewById(R.id.associatedDriver);
            btnRfidData = (Button) itemView.findViewById(R.id.btnRfidData);
            btnAssignVehicle = (Button) itemView.findViewById(R.id.btnAssignVehicle);
            btnDisassociate = (Button) itemView.findViewById(R.id.btnDisassociate);
            btnLiveMap = (Button) itemView.findViewById(R.id.btnLiveMap);
            btnTrack2 = (Button) itemView.findViewById(R.id.btnTrack2);
            btnLiveMap2 = (Button) itemView.findViewById(R.id.btnLiveMap2);
            btnTrack = (Button) itemView.findViewById(R.id.btnTrack);
            layShowRfidData = (LinearLayout) itemView.findViewById(R.id.layShowRfidData);
            layNotShowRfidData = (LinearLayout) itemView.findViewById(R.id.layNotShowRfidData);
            lay1 = (LinearLayout) itemView.findViewById(R.id.lay1);
            lay2 = (LinearLayout) itemView.findViewById(R.id.lay2);
            main = (FloatingActionButton) itemView.findViewById(R.id.fb);
            btnCallDrvCond = (Button) itemView.findViewById(R.id.btnCall);
            btnAssignDriver = (Button) itemView.findViewById(R.id.btnAssignDriver);
            btnUnassignDriver = (Button) itemView.findViewById(R.id.btnUnassignDriver);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            routeDesc = (TextView) itemView.findViewById(R.id.routeDesc);
            btnAssignConductor = (Button) itemView.findViewById(R.id.btnAssignConductor);
            btnUnassignConductor = (Button) itemView.findViewById(R.id.btnUnassignConductor);
            txtBusCapacity = (TextView) itemView.findViewById(R.id.txtBusCapacity);
            txtPickupStudentCount = (TextView) itemView.findViewById(R.id.txtPickupStudentCount);
            txtPickupTeacherCount = (TextView) itemView.findViewById(R.id.txtPickupTeacherCount);
            txtDropTeacherCount = (TextView) itemView.findViewById(R.id.txtDropTeacherCount);

            txtDropStudentCount = (TextView) itemView.findViewById(R.id.txtDropStudentCount);
            //      btnShowDetails = (Button)itemView.findViewById(R.id.btnShowDetails);
            btnRouteDetails = (Button) itemView.findViewById(R.id.btnRouteDetails);
            //b4 = (FloatingActionButton) itemView.findViewById(R.id.b4);
            //   verticalStepView = (VerticalStepView) itemView.findViewById(R.id.verticalStepView);


        }
    }
}


