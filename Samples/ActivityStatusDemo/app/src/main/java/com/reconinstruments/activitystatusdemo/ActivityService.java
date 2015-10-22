package com.reconinstruments.activitystatusdemo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.reconinstruments.app.activity.HUDActivityStatus;

public class ActivityService extends IntentService
{
    public static boolean isRunning = false;
    private BroadcastReceiver mReceiver;
    private NotificationManager mNotificationManager;

    public ActivityService()
    {
        super("ActivityService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                int type   = intent.getIntExtra(HUDActivityStatus.ACTIVITY_TYPE,   HUDActivityStatus.TYPE_UNKNOWN);
                int status = intent.getIntExtra(HUDActivityStatus.ACTIVITY_STATUS, HUDActivityStatus.STATUS_UNKNOWN);
                createNotification("Activity: " + getTypeString(type) + ", Status: " + getStatusString(status));
            }
        };
        registerReceiver(mReceiver, new IntentFilter(HUDActivityStatus.ACTIVITY_INTENT_FILTER));

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        createNotification("Activity Status Service: STARTED");
        ActivityService.isRunning = true;

        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        createNotification("Activity Status Service: STOPPED");
        unregisterReceiver(mReceiver);
        ActivityService.isRunning = false;
        super.onDestroy();
    }

    private String getStatusString(int status)
    {
        switch(status)
        {
            case HUDActivityStatus.STATUS_NOACTIVITY :
                return "NO-ACTIVITY";
            case HUDActivityStatus.STATUS_ONGOING :
                return "ON-GOING";
            case HUDActivityStatus.STATUS_PAUSED :
                return "PAUSED";
            default:
                return "UNKNOWN";
        }
    }

    private String getTypeString(int type)
    {
        switch(type)
        {
            case HUDActivityStatus.TYPE_CYCLING :
                return "CYCLING";
            case HUDActivityStatus.TYPE_RUNNING :
                return "RUNNING";
            case HUDActivityStatus.TYPE_SKI :
                return "SKIING";
            default:
                return "UNKNOWN";
        }
    }

    private void createNotification(String text)
    {
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Activity Status Service")
                .setSmallIcon(R.drawable.icon_checkmark)
                .setContentText(text)
                .build();
        mNotificationManager.notify(0, notification);
    }

    @Override
    protected void onHandleIntent(Intent intent){ }
}
