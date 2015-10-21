package com.reconinstruments.activitystatusdemo;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.reconinstruments.app.activity.HUDActivityStatus;

public class MainActivity extends Activity
{
    private Intent mServiceIntent;

    private TextView statusText;
    private TextView typeText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = (TextView) findViewById(R.id.activity_status_text);
        typeText   = (TextView) findViewById(R.id.activity_type_text);

        statusText.setText("Service Created");
        typeText.setText("");

        // Start service stuff
        mServiceIntent = new Intent(this, ActivityService.class);
        mServiceIntent.setData(Uri.parse("SHEEEEE"));
        startService(mServiceIntent);
    }
}
