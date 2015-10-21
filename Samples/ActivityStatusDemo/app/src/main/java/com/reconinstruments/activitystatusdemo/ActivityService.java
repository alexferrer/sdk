package com.reconinstruments.activitystatusdemo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.reconinstruments.app.activity.HUDActivityStatus;

public class ActivityService extends IntentService
{
    private BroadcastReceiver mReceiver;
    private NotificationManager mNotificationManager;
    private Intent mIntent;

    public ActivityService(){ super("ActivityService"); }

    @Override
    protected void onHandleIntent(Intent intent){ }

    @Override
    public void onCreate() {
        super.onCreate();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int type = intent.getIntExtra(HUDActivityStatus.ACTIVITY_TYPE, HUDActivityStatus.TYPE_UNKNOWN);
                int status = intent.getIntExtra(HUDActivityStatus.ACTIVITY_STATUS, HUDActivityStatus.STATUS_UNKNOWN);
                createNotification("Activity - " + getTypeString(type), "Status: " + getStatusString(status));
            }
        };
        mIntent = registerReceiver(mReceiver, new IntentFilter(HUDActivityStatus.ACTIVITY_INTENT_FILTER));

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /*
        (new Thread(new Runnable() {

            @Override
            public void run() {
                while (!Thread.interrupted())
                    try {
                        createNotification("Activity Status Service", "STATE: RUNNING");
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                    }
            }
        })).start();
        */
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private String getStatusString(int status)
    {
        switch(status)
        {
            case HUDActivityStatus.STATUS_NOACTIVITY :
                return "STATUS_NOACTIVITY";
            case HUDActivityStatus.STATUS_ONGOING :
                return "STATUS_ONGOING";
            case HUDActivityStatus.STATUS_PAUSED :
                return "STATUS_PAUSED";
            default:
                return "STATUS_UNKNOWN";
        }
    }

    private String getTypeString(int type)
    {
        switch(type)
        {
            case HUDActivityStatus.TYPE_CYCLING :
                return "TYPE_CYCLING";
            case HUDActivityStatus.TYPE_RUNNING :
                return "TYPE_RUNNING";
            case HUDActivityStatus.TYPE_SKI :
                return "TYPE_SKI";
            default:
                return "TYPE_UNKNOWN";
        }
    }

    private void createNotification(String title, String content)
    {
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setSmallIcon(R.drawable.icon_checkmark)
                .setContentText(content)
                .build();
        mNotificationManager.notify(0, notification);
    }
}
