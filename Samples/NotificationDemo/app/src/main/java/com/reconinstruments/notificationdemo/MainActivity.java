package com.reconinstruments.notificationdemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    private final String TAG = "NotificationDemo";

    Button mPassiveButton;
    Button mInteractiveButton;
    Button mPassive5Button;
    Button mInteractive5Button;
    Button mPIPIPButton;

    NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPassiveButton      = (Button) findViewById(R.id.INTERACTIVE);
        mInteractiveButton  = (Button) findViewById(R.id.PASSIVE);
        mPassive5Button     = (Button) findViewById(R.id.INTERACTIVE_5);
        mInteractive5Button = (Button) findViewById(R.id.PASSIVE_5);
        mPIPIPButton        = (Button) findViewById(R.id.PIPIP);

        mPassiveButton.setOnClickListener(this);
        mInteractiveButton.setOnClickListener(this);
        mPassive5Button.setOnClickListener(this);
        mInteractive5Button.setOnClickListener(this);
        mPIPIPButton.setOnClickListener(this);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.PASSIVE:
                passive(0);
                break;
            case R.id.INTERACTIVE:
                interactive(0);
                break;
            case R.id.PASSIVE_5:
                for(int i = 0; i < 5; i++){ passive(i); }
                break;
            case R.id.INTERACTIVE_5:
                for(int i = 0; i < 5; i++){ interactive(i); }
                break;
            case R.id.PIPIP:
                passive(0);
                interactive(1);
                passive(2);
                interactive(3);
                passive(4);
                break;
             default:
                Log.e(TAG, "Unknown View ID");
                break;
        }
    }

    private void passive(int i) {
        Log.d(TAG, "passive(" + i + ")");
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("passive title " + i)
                .setSmallIcon(R.drawable.ic_launcher_share)
                .setContentText("passive text " + i)
                        //.setTicker("passive ticker " + i)
                .build();
        mNotificationManager.notify(0, notification);
    }

    private void interactive(int i) {
        Log.d(TAG, "interactive(" + i + ")");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(Settings.ACTION_SETTINGS), 0);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("interactive title " + i)
                .setSmallIcon(R.drawable.ic_launcher_share)
                .setContentText("interactive text " + i)
                .setContentIntent(pendingIntent)
                .build();
        mNotificationManager.notify(0, notification);
    }


}
