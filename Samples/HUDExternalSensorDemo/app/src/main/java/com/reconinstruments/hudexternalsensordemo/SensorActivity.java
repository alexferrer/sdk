package com.reconinstruments.hudexternalsensordemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnection;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnectionParams;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorListener;
import com.reconinstruments.os.hardware.extsensor.HUDExternalSensorManager;

import java.util.List;

public class SensorActivity extends Activity implements ExternalSensorListener {

    private final static String TAG = "ExternalSensorDemoS";
    private HUDExternalSensorManager mHUDExternalSensorManager;
    private int mUid = -1;
    Button mConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mConnectButton = (Button)findViewById(R.id.connectButton);

        Intent intent = getIntent();
        mUid = intent.getIntExtra("UID", -1);

        if (mUid == -1) {
            Log.d(TAG, "Failed to get UID of the sensor");
            finish();
        } else {
            mHUDExternalSensorManager = (HUDExternalSensorManager)HUDOS.getHUDService(HUDOS.HUD_EXTERNAL_SENSOR_SERVICE);
            init();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHUDExternalSensorManager.unregisterListener(this);
    }

    private void init() {
        mHUDExternalSensorManager.registerListener(this);

        boolean isConnected = mHUDExternalSensorManager.isSensorConnected(mUid);

        // Check to see if the sensor is connected or not
        if (isConnected) {
            mConnectButton.setTextColor(Color.GREEN);
        } else {
            mConnectButton.setTextColor(Color.BLACK);
        }

        ExternalSensorConnectionParams.ExternalSensorNetworkType type = mHUDExternalSensorManager.getHUDNetworkType();
        List<ExternalSensorConnectionParams> savedListConnectionParams = null;
        try
        {
            savedListConnectionParams = mHUDExternalSensorManager.getSavedSensorConnectionParams(type);
        }
        catch (RuntimeException e) { e.printStackTrace(); }
    }

    public void doConnect(View view) {
        Log.d(TAG, "doConnect: " + mUid);
        if (mHUDExternalSensorManager.isSensorConnected(mUid)) {
            Log.d(TAG, "Disconnecting...");
            mHUDExternalSensorManager.disconnectSensor(mUid);
        } else {
            Log.d(TAG, "Connecting...");
            mHUDExternalSensorManager.connectSensor(mUid);
        }
    }

    @Override
    public void onSensorConnected(ExternalSensorConnection sensorConnection) {
        Log.d(TAG, "Sensor " + sensorConnection + " connected");
//        ExternalSensorConnectionParams cp = sensorConnection.getSensorConnectionParams();
    }

    @Override
    public void onSensorDisconnected(ExternalSensorConnection sensorConnection) {
        Log.d(TAG, "Sensor " + sensorConnection + " disconnected");
//        ExternalSensorConnectionParams cp = sensorConnection.getSensorConnectionParams();
    }

    @Override
    public void onSensorConnectFailure(ExternalSensorConnection sensorConnection) {
        Log.d(TAG, "Sensor " + sensorConnection + " failed to connect");
//        ExternalSensorConnectionParams cp = sensorConnection.getSensorConnectionParams();
    }
}
