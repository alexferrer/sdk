package com.reconinstruments.activitystatusdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class MainActivity extends Activity implements ServiceConnection
{
    private Intent mService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView topText = (TextView) findViewById(R.id.top_text);
        TextView botText = (TextView) findViewById(R.id.bottom_text);

        // Bind to ActivityService
        mService = new Intent(this, ActivityService.class);
        bindService(mService, this, 0);

        // If the service was running before we launched this application
        // then we stop the service from running in the background.
        if(ActivityService.isRunning)
        {
            stopService(mService);
            unbindService(this);
            topText.setText("Service: STOPPED");
            botText.setText("(Run the app again to START the service)");
        }
        else // If no activity status service is running, we start one.
        {
            startService(mService);
            topText.setText("Service: STARTED");
            botText.setText("(Run the app again to STOP the service)");
        }
    }

    @Override
    public void onDestroy()
    {
        unbindService(this);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service){ }

    @Override
    public void onServiceDisconnected(ComponentName name){ }
}
