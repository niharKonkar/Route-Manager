package com.routeassociation.pojo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by techlead on 29/12/17.
 */

public class CheckInternet {

    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    public CheckInternet() {
    }
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
}

