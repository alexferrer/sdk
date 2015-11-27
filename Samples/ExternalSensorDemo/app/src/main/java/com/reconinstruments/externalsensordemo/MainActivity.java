package com.reconinstruments.externalsensordemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnection;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorListener;
import com.reconinstruments.os.hardware.extsensor.HUDExternalSensorManager;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnectionParams;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements ExternalSensorListener
{
    private final static String TAG = "ExternalSensorDemo";
    private TextView sensorModeText;

    LinearLayout mListLinearLayout;
    private HashMap<Button, ExternalSensorConnectionParams> buttonParamHash = new HashMap<Button, ExternalSensorConnectionParams>();
    private HashMap<Integer, Button> uidButtonHash = new HashMap<Integer, Button>();

    public static HUDExternalSensorManager mExternalSensorManager;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorModeText = (TextView) findViewById(R.id.sensorModeText);
        mListLinearLayout = (LinearLayout)findViewById(R.id.listLinerLayout);

        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mExternalSensorManager = (HUDExternalSensorManager) HUDOS.getHUDService(HUDOS.HUD_EXTERNAL_SENSOR_SERVICE);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mExternalSensorManager.registerListener(this);

        // Get the current sensor mode in use (ANT+ or BLE)
        ExternalSensorConnectionParams.ExternalSensorNetworkType type = mExternalSensorManager.getHUDNetworkType();
        boolean isModeANT = (type == ExternalSensorConnectionParams.ExternalSensorNetworkType.ANT);
        sensorModeText.setText(isModeANT ? "ANT+" : "BLE");

        // Retrieve the list of saved devices
        List<ExternalSensorConnectionParams> savedListConnectionParams = null;
        try
        {
            savedListConnectionParams = mExternalSensorManager.getSavedSensorConnectionParams(type);
        }
        catch(RuntimeException e){ e.printStackTrace(); }

        if(savedListConnectionParams != null)
        {
            for(ExternalSensorConnectionParams params : savedListConnectionParams)
            {
                Log.d(TAG, "Device -> name: " + params.getName() + ", type: " + params.getSensorType().name());

                // If the new device is not yet displayed, add a button for the new device.
                if(!uidButtonHash.containsKey(params.getUID()))
                {
                    AddDeviceButton(params);
                }
            }
        }
        else { Log.d(TAG, "No saved list..."); }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mExternalSensorManager.unregisterListener(this);
    }

    private void AddDeviceButton(ExternalSensorConnectionParams params)
    {
        Button button = new Button(MainActivity.this);
        button.setText(params.getName() + (mExternalSensorManager.isSensorConnected(params.getUID()) ? " <ON>" : " <OFF>"));
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //button.setTextColor(Color.BLACK);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        button.setOnClickListener(onClickListener);

        mListLinearLayout.addView(button);
        buttonParamHash.put(button, params);
        uidButtonHash.put(params.getUID(), button);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            ExternalSensorConnectionParams params = buttonParamHash.get(view);
            if(mExternalSensorManager.isSensorConnected(params.getUID()))
            {
                Log.d(TAG, "Disconnecting Sensor: " + params.getName());
                createNotification("Disconnecting Sensor: " + params.getName());
                mExternalSensorManager.disconnectSensor(params.getUID());

            }
            else
            {
                Log.d(TAG, "Connecting Sensor: " + params.getName());
                createNotification("Connecting Sensor: " + params.getName());
                mExternalSensorManager.connectSensor(params.getUID());
            }
        }
    };

    public void createNotification(String text)
    {
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("External Sensor")
                .setSmallIcon(R.drawable.icon_warning)
                .setContentText(text)
                .build();
        mNotificationManager.notify(0, notification);
    }

    @Override
    public void onSensorConnected(ExternalSensorConnection sensorConnection)
    {
        Log.d(TAG, "Sensor " + sensorConnection + " connected");
        Button b = uidButtonHash.get(sensorConnection.getUID());
        updateSensorButton(sensorConnection.getUID(), buttonParamHash.get(b).getName() + " <ON>");
    }

    @Override
    public void onSensorDisconnected(ExternalSensorConnection sensorConnection)
    {
        Log.d(TAG, "Sensor " + sensorConnection + " disconnected");
        Button b = uidButtonHash.get(sensorConnection.getUID());
        updateSensorButton(sensorConnection.getUID(), buttonParamHash.get(b).getName() + " <OFF>");
    }

    @Override
    public void onSensorConnectFailure(ExternalSensorConnection sensorConnection)
    {
        Log.d(TAG, "Sensor " + sensorConnection + " failed to connect");
        Button b = uidButtonHash.get(sensorConnection.getUID());
        updateSensorButton(sensorConnection.getUID(), buttonParamHash.get(b).getName() + " <OFF>");
        createNotification(buttonParamHash.get(b).getName() + " failed to connect");
    }

    private void updateSensorButton(int uuid, String text) {
        Message msg = new Message();
        msg.arg1 = uuid;
        msg.obj  = text;
        messageHandler.sendMessage(msg);
    }

    private Handler messageHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            Button b = uidButtonHash.get(msg.arg1);
            b.setText((String) msg.obj);
        }
    };
}
