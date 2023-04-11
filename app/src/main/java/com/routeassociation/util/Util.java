package com.routeassociation.util;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

public class Util {

    /* localhost */

  //  private String strMainUrl="http://192.168.15.8:8080/webservices/";
    //private String strMainUrl = "http://46.137.213.50/webservices/";
    private String strMainUrl = "http://www.etechtracker.com/webservices/";
    //private String strMainUrl = "http:/192.168.15.8:8080/webservices/";
    private boolean isDemoVersion = false;
    private Context context;

    public Util(Context context) {

        this.context = context;
    }

    //rfid data
    public JSONArray getVehiclewiseRfidData(Integer vehId) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("rfidAsset/getVehiclewiseAssetSwipes?");

        sb.append("vehId=").append(vehId);
        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(webservice.output.toString());
            //array = jsonObject.getJSONArray("data");
        } catch (Exception e) {

        }

        return jsonArray;
    }


    //get routes
    public JSONArray getRoutes(int org, int dep) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        // sb.append("geofence/getRoutesByOrgDep?");
        sb.append("geofence/getRoutesByOrgDepV2?");
        sb.append("org=").append(org);
        sb.append("&dep=").append(dep);

        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray array = null;
        try {
            JSONArray mainArray = new JSONArray(webservice.output);

            array = mainArray.getJSONObject(1).getJSONArray("data");
        } catch (Exception e) {
            e.printStackTrace();

        }

        return array;
    }

    //add driver
    public String addDriver(int org, int dep, String drvName, String drvContactNumber, String drvAddress, String image, String contentType, String fileName, String drvType) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("driver/addDriverNew?");
            sb.append("orgId=").append(org);
            sb.append("&depId=").append(dep);
            sb.append("&driverName=").append(drvName);
            sb.append("&driverContactNumber=").append(drvContactNumber);
            sb.append("&driverAddress=").append(URLEncoder.encode(drvAddress, "UTF-8"));
            sb.append("&imageString=").append(image);
            sb.append("&contentType=").append(contentType);
            sb.append("&fileName=").append(fileName);
            sb.append("&drvTypeMd=").append(drvType);
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //add driver
    public String sendCommunication(int org, int dep, int gegId,String pickupDrop, String message) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("alertSystem/getSendNotificationtoAllByRouteV2?");
            sb.append("orgId=").append(org);
            sb.append("&depId=").append(dep);
            sb.append("&gegId=").append(gegId);
            sb.append("&pickupDrop=").append(pickupDrop);
            sb.append("&message=").append(URLEncoder.encode(message, "UTF-8"));

            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
    //add incident
    public String addIncident(int org, int dep,int gegId,int drvId,int vehId ,String incPickupDropPd, String ncCat, String incDescription,int usrId, String incDate,int icaId,int kms) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("incident/addIncident?");
            sb.append("orgId=").append(org);
            sb.append("&depId=").append(dep);
            sb.append("&gegId=").append(gegId);
            sb.append("&drvId=").append(drvId);
            sb.append("&vehId=").append(vehId);
            sb.append("&incPickupDropPd=").append(incPickupDropPd);
            sb.append("&incCat=").append(URLEncoder.encode(ncCat, "UTF-8"));
            sb.append("&incDescription=").append(URLEncoder.encode(incDescription, "UTF-8"));
            sb.append("&usrId=").append(usrId);
            sb.append("&incDate=").append(URLEncoder.encode(incDate, "UTF-8"));
            sb.append("&icaId=").append(icaId);
            sb.append("&kms=").append(kms);
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //get system logs
    public String getSystemLogs(String fromDate, String toDate, int orgId, int depId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("systemLog/getSystemLogv1?");
            sb.append("from=").append(URLEncoder.encode(fromDate, "UTF-8"));
            sb.append("&to=").append(URLEncoder.encode(toDate, "UTF-8"));
            sb.append("&orgId=").append(orgId);
            sb.append("&depId=").append(depId);
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //get routes
    public String getRouteAnalysis(int org, int dep, int routeId, String pickDrop) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("schoolbusevents/getRouteAnalysis?");
            sb.append("orgId=").append(org);
            sb.append("&depId=").append(dep);
            sb.append("&gegId=").append(routeId);
            sb.append("&pickupDrop=").append(pickDrop);
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //set android reg id
    public String setRouteManagerAndroidRegId(int usr, String androiId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("users/setRouteManagerAndroidRegId?");
            sb.append("usr=").append(usr);
            sb.append("&androidId=").append(URLEncoder.encode(androiId, "UTF-8"));
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //fetch incident
    public String GetIncidentsByOrgDep(int orgId, int depId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("incident/getIncidentsByOrgDep?");
            sb.append("orgId=").append(orgId);
            sb.append("&depId=").append(depId);
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //change driver
    public String changeRouteDriverAssociation(int orgId, int depId, int gegId, int drvId, String pickDrop, int usrId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("driver/changeRouteDriverAssociation?");
            sb.append("orgId=").append(orgId);
            sb.append("&depId=").append(depId);
            sb.append("&gegId=").append(gegId);
            sb.append("&drvId=").append(drvId);
            sb.append("&pickDrop=").append(pickDrop);
        //    sb.append("&usrId=").append(usrId);

            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //change conductor
    //change driver
    public String changeRouteConductorAssociation(int orgId, int depId, int gegId, int drvId, String pickDrop) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("driver/changeRouteMaidAssociation?");
            sb.append("orgId=").append(orgId);
            sb.append("&depId=").append(depId);
            sb.append("&gegId=").append(gegId);
            sb.append("&drvId=").append(drvId);
            sb.append("&pickDrop=").append(pickDrop);

            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //driver list
    public String getDriverList(int orgId, int depId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("driver/getDrivers?");
            sb.append("orgId=").append(orgId);
            sb.append("&depId=").append(depId);
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //incident categories
    //driver list
    public String getIncidentCatList(int orgId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("incidentCategory/getIncidentCategoriesByOrg?");
            sb.append("orgId=").append(orgId);

            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //maid list
    public String getMaidList(int orgId, int depId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("driver/getMaids?");
            sb.append("orgId=").append(orgId);
            sb.append("&depId=").append(depId);
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //get events
    public String getSchoolBusEvents(int vehId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("schoolbusevents/getSchoolBusEvents?");
            sb.append("vehId=").append(vehId);
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //login
    public String authenticateZicomUser(String username, String password) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("users/authenticateZicomUserJson?");

        sb.append("pwd=").append(password);
        sb.append("&usrName=").append(username);

        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webservice.output;

    }

    //load vehicles
    public JSONArray getAllVehicles(int org, int dep) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("vehicle/getVehiclesByOrgDep?");
        sb.append("org=").append(org);
        sb.append("&dep=").append(dep);

        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray array = null;
        try {
            JSONArray mainArray = new JSONArray(webservice.output);

            array = mainArray.getJSONObject(1).getJSONArray("data");
        } catch (Exception e) {
            e.printStackTrace();

        }

        return array;
    }

    //assign / unassigned vehicle
    public JSONObject updateVehiclesAssociations(int org, int dep, int geg, int veh, int usr) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("geofence/updateVehAssociation?");
        sb.append("org=").append(org);
        sb.append("&dep=").append(dep);
        sb.append("&geg=").append(geg);
        sb.append("&veh=").append(veh);
        sb.append("&usr=").append(usr);
        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(webservice.output);

        } catch (Exception e) {

        }

        return jsonObject;
    }

    //lat lan
    public String getLatestLocationByVehId(Integer vehId) {
        StringBuilder sb = new StringBuilder();
        //    strMainUrl="http://192.168.39.1:8080/webservices/";
        sb.append(strMainUrl);
        sb.append("vehicle/getLatestLocationByVehId?");

        sb.append("vehId=").append(vehId);
        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();
        try {
            webservice.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webservice.output.toString();
    }

    //notification history
    public String getNotificationsDataV2(Integer usrId, Integer maxResults, String usrRasFlag) {

        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("alertSystem/getNotificationsDataV2?");

        sb.append("usr=").append(usrId);
        sb.append("&maxResults=").append(maxResults);
        sb.append("&usrRasFlag=").append(usrRasFlag);


        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webservice.output.toString();

    }

    //save andriod id
    public String setAndroidRegId(Integer usrId, String regId) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("users/setAndroidRegId?");

        sb.append("usr=").append(usrId);
        sb.append("&androidId=").append(regId);

        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webservice.output.toString();
    }

    //get live data
    public JSONArray getLiveData(Integer orgId, Integer depId) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("gpstransactionLive/getAllDataForZicom?");

        sb.append("orgId=").append(orgId);
        sb.append("&depId=").append(depId);

        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray array = null;
        try {
            JSONObject jsonObject = new JSONObject(webservice.output.toString());
            array = jsonObject.getJSONArray("data");
        } catch (Exception e) {

        }

        return array;
    }

    //get vehicle data
    public JSONArray getTokenLiveData(String token) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("vehicleTracker/vehicleDataByToken?");
        sb.append("token=").append(token);

        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray array = null;
        try {
            JSONArray mainArray = new JSONArray(webservice.output.toString());

            array = mainArray.getJSONObject(1).getJSONArray("data");
        } catch (Exception e) {

        }

        return array;
    }

    //get live data
    public JSONObject getLiveDataForRasId(Integer rasId) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("gpstransactionLive/getAllDataForAssetV2?");
        sb.append("rasId=").append(rasId);

        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(webservice.output.toString());

        } catch (Exception e) {

        }

        return jsonObject;
    }

    //get live location
    public JSONObject getLiveDataForVehicle(Integer vehId) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("vehicle/getLatestLocationByVehId?");
            sb.append("vehId=").append(vehId);


            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            JSONObject object = null;
            try {
                object = new JSONObject(webservice.output.toString());
            } catch (Exception e) {

            }

            return object;
        } catch (Exception e) {
            return null;
        }
    }


    //timeline data
    public String getTimelineData(Integer orgId, Integer depId, String sbeAlertFlag) {

        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("schoolbusevents/getSchoolBusEventsForOrgDepV2?");

        sb.append("orgId=").append(orgId);
        sb.append("&depId=").append(depId);
        sb.append("&sbeAlertFlag=").append(sbeAlertFlag);


        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webservice.output.toString();

    }

    //get department
    public String getDepartments(Integer orgId) {
        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("department/getDepList?org=").append(orgId);
        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();
        try {
            webservice.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webservice.output.toString();
    }

    //get real-time analysis data
    public String getRealTimeAnalysisData(Integer orgId, Integer depId, String ggdGroupCode) {

        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        //   sb.append("http://192.168.4.80:8080/webservices/");
        sb.append("geofence/getRealtimePickDropAnalysis?");

        sb.append("orgId=").append(orgId);
        sb.append("&depId=").append(depId);
        sb.append("&ggdGroupCode=").append(ggdGroupCode);


        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webservice.output.toString();

    }

    //get real-time analysis data
    public String getCameraAnalysisData(Integer orgId, Integer depId) {

        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        //   sb.append("http://192.168.4.80:8080/webservices/");
        sb.append("vehicle/getCameraAnalyticsByOrgDep?");

        sb.append("orgId=").append(orgId);
        sb.append("&depId=").append(depId);

        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webservice.output.toString();

    }

    //get routes
    public String saveRouteRasCount(int org, int dep, int routeId, String pickDrop, int count) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(strMainUrl);
            sb.append("routeRasCountHistory/saveRouteRasCount?");
            sb.append("orgId=").append(org);
            sb.append("&depId=").append(dep);
            sb.append("&gegId=").append(routeId);
            sb.append("&pickupDrop=").append(pickDrop);
            sb.append("&rccCount=").append(count);
            Webservice webservice = new Webservice(sb.toString(),context);
            webservice.start();

            try {
                webservice.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return webservice.output;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //get real-time analysis data
    public String getNearestBusStops(Integer orgId, Integer depId, double latitude, double longitude) {

        StringBuilder sb = new StringBuilder();
        sb.append(strMainUrl);
        sb.append("geofenceGroupDetails/getNearestBusStopsV1?");

        sb.append("orgId=").append(orgId);
        sb.append("&depId=").append(depId);
        sb.append("&lat=").append(latitude);
        sb.append("&lng=").append(longitude);

        Webservice webservice = new Webservice(sb.toString(),context);
        webservice.start();

        try {
            webservice.join(30000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webservice.output.toString();

    }
}