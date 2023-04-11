package com.routeassociation.pojo;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.routeassociation.MainActivity;
import com.routeassociation.R;
import java.util.List;
import java.util.Random;

/**
 * Created by sonal on 6/12/17.
 */

public class MyFirebaseService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    String user_id = "0";
    String date = "0";
    String hal_id = "0";
    String M_view = "0";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        SharedPreferences settings = getSharedPreferences("REG_TOKEN", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", token);
        editor.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Boolean IsForground = CheckAppIsRunningForground(MyFirebaseService.this);
            if (IsForground) {
                //App is FourGround
                sendNotification(remoteMessage.getData().toString());

            } else {
                sendNotification(remoteMessage.getNotification().getBody());
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }


    private void sendNotification(String msg) {


        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", msg);
        intent.putExtra("timeline", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(100), intent,
                    PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(100), intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        long when = System.currentTimeMillis();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this);
        mNotifyBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000});
        boolean lollipop = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        if (lollipop) {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.notification);

            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setStyle(
                            new NotificationCompat.BigTextStyle()
                                    .bigText(msg))
                    .setContentText(msg)
                    .setColor(Color.TRANSPARENT)
                    .setSmallIcon(R.drawable.notification)
                    //    .setLargeIcon(largeIcon)
                    .setWhen(when).setAutoCancel(true)
                    .setSound(sound)
                    .setContentIntent(pendingIntent);


        } else {
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.notification);
            mNotifyBuilder = new NotificationCompat.Builder(this)
                    .setStyle(
                            new NotificationCompat.BigTextStyle()
                                    .bigText(msg))
                    .setContentTitle(getString(R.string.app_name)).
                            setContentText(msg)
                    .setSmallIcon(R.drawable.notification)


                    //.setLargeIcon(largeIcon)
                    .setColor(Color.TRANSPARENT)

                    .setWhen(when).setAutoCancel(true)
                    .setSound(sound)
                    .setContentIntent(pendingIntent);

        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mNotifyBuilder.build());

    }


    public static boolean CheckAppIsRunningForground(Context mcontext) {

        ActivityManager am = (ActivityManager) mcontext
                .getSystemService(mcontext.ACTIVITY_SERVICE);

        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equalsIgnoreCase("com.techlead.parentanalytics")) {
            return true;
        } else {
            return false;
        }
    }
}
//f9BeMraWA84:APA91bEVBhd9p0DRmw8ApZ1IT0CP3VrqCeQ551xE9tco_5tElaDus9L7tcKGIvRn5I1hq9lc9M_I-k99110fiyGGyPgSHfMR3Ha5Kp4tCqzxLFaUGBUO_hnDUt82ihxzdcptEBOlP_zs