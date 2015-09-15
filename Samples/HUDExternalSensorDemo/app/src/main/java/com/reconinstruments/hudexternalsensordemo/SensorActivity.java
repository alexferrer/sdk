package com.reconinstruments.hudexternalsensordemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnection;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnectionParams;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnectionParams.ExternalSensorType;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorListener;
import com.reconinstruments.os.hardware.extsensor.HUDExternalSensorManager;

import java.util.List;

public class SensorActivity extends Activity implements ExternalSensorListener {

    private final static String TAG = "ExternalSensorDemoS";
    private HUDExternalSensorManager mHUDExternalSensorManager;
    private int mUid = -1;
    Button mConnectButton;
    Button mCalibrateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mConnectButton = (Button)findViewById(R.id.connectButton);
        mCalibrateButton = (Button)findViewById(R.id.calibrateButton);
        mCalibrateButton.setVisibility(View.INVISIBLE);

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
        try {
            savedListConnectionParams = mHUDExternalSensorManager.getSavedSensorConnectionParams(type);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (savedListConnectionParams != null) {
            for (ExternalSensorConnectionParams connectionParams : savedListConnectionParams) {
                if (connectionParams.getUID() == mUid) {
                    if (connectionParams.getSensorType() == ExternalSensorType.BIKE_POWER) {
                        mCalibrateButton.setVisibility(View.VISIBLE);
                        mCalibrateButton.setEnabled(isConnected);
                    }
                    break;
                }
            }
        }
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

    public void doCalibrate(View view) {
        boolean res = mHUDExternalSensorManager.startZeroOffsetCalibration(mUid);
        if (res) {
            Log.d(TAG, "Doing calibration: " + res);
            mCalibrateButton.setTextColor(Color.YELLOW);
        } else {
            Log.d(TAG, "Failed to do calibration!");
            mCalibrateButton.setTextColor(Color.RED);
        }
    }

    private static final int CONNECTED = 0;
    private static final int DISCONNECTED = 1;
    private static final int CONNECT_FAILED = 2;
    private static final int ZERO_CALIBRATE = 3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case CONNECTED:
                    mConnectButton.setTextColor(Color.GREEN);
                    if (message.arg1 == 1) {
                        Log.d(TAG, "Power sensor, enabling calibration button");
                        mCalibrateButton.setEnabled(true);
                        mCalibrateButton.setVisibility(View.VISIBLE);
                    }
                    break;
                case DISCONNECTED: {
                    mConnectButton.setTextColor(Color.BLACK);
                    if (message.arg1 == 1) {
                        Log.d(TAG, "Power sensor, disabling calibration button");
                        mCalibrateButton.setEnabled(false);
                        mCalibrateButton.setVisibility(View.VISIBLE);
                    }
                }
                break;
                case CONNECT_FAILED: {
                    mConnectButton.setTextColor(Color.RED);
                    if (message.arg1 == 1) {
                        Log.d(TAG, "Power sensor, disabling calibration button");
                        mCalibrateButton.setEnabled(false);
                        mCalibrateButton.setVisibility(View.VISIBLE);
                    }
                }
                break;
                case ZERO_CALIBRATE: {
                    if (message.arg1 == 1) {
                        mCalibrateButton.setTextColor(Color.GREEN);
                    } else {
                        mCalibrateButton.setTextColor(Color.RED);
                    }
                }
                break;
                default:
                    Log.e(TAG, "Invalid message received!");
                    break;
            }
        }
    };

    @Override
    public void onSensorConnected(ExternalSensorConnection sensorConnection) {
        Log.d(TAG, "Sensor " + sensorConnection + " connected");
        Message msg = mHandler.obtainMessage(CONNECTED);
        msg.arg1 = 0;
        ExternalSensorConnectionParams cp = sensorConnection.getSensorConnectionParams();
        Log.d(TAG, "cp: " + cp.getSensorType());
        if (cp.getSensorType() == ExternalSensorType.BIKE_POWER) {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    @Override
    public void onSensorDisconnected(ExternalSensorConnection sensorConnection) {
        Log.d(TAG, "Sensor " + sensorConnection + " disconnected");
        Message msg = mHandler.obtainMessage(DISCONNECTED);
        msg.arg1 = 0;
        ExternalSensorConnectionParams cp = sensorConnection.getSensorConnectionParams();
        Log.d(TAG, "cp: " + cp.getSensorType());
        if (cp.getSensorType() == ExternalSensorType.BIKE_POWER) {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    @Override
    public void onSensorConnectFailure(ExternalSensorConnection sensorConnection) {
        Log.d(TAG, "Sensor " + sensorConnection + " failed to connect");
        Message msg = mHandler.obtainMessage(CONNECT_FAILED);
        msg.arg1 = 0;
        ExternalSensorConnectionParams cp = sensorConnection.getSensorConnectionParams();
        Log.d(TAG, "cp: " + cp.getSensorType());
        if (cp.getSensorType() == ExternalSensorType.BIKE_POWER) {
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }

    @Override
    public void onSensorDiscovered(ExternalSensorConnectionParams connectionParams) {
        Log.d(TAG, "onSensorDiscovered: " + connectionParams.getUID());
    }

    @Override
    public void onDiscoveredSensorLost(ExternalSensorConnectionParams connectionParams) {
    }

    @Override
    public void onZeroCalibrationResult(boolean success, ExternalSensorConnection sensorConnection) {
        Message msg = mHandler.obtainMessage(ZERO_CALIBRATE);
        msg.arg1 = 0;
        ExternalSensorConnectionParams cp = sensorConnection.getSensorConnectionParams();
        if (cp.getSensorType() == ExternalSensorType.BIKE_POWER &&
            sensorConnection.getUID() == mUid && success) {
            Log.d(TAG, "Got result: " + success);
            msg.arg1 = 1;
        }
        mHandler.sendMessage(msg);
    }
}
