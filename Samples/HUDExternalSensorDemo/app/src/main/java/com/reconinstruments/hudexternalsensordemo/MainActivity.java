package com.reconinstruments.hudexternalsensordemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnection;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnectionParams;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorListener;
import com.reconinstruments.os.hardware.extsensor.HUDExternalSensorManager;

import java.util.List;

public class MainActivity extends Activity implements ExternalSensorListener {

    private final static String TAG = "ExternalSensorDemoA";
    Button mScanButton;
    Button mAntButton;
    Button mBleButton;

    LinearLayout mListLinearLayout;

    SparseArray<Button> mButtons = new SparseArray<Button>();
    private boolean mDiscovering = false;

    public static HUDExternalSensorManager mHUDExternalSensorManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScanButton = (Button)findViewById(R.id.scanButton);
        mScanButton.setOnClickListener(mScanButtonListener);
        mAntButton = (Button)findViewById(R.id.ant);
        mBleButton = (Button)findViewById(R.id.ble);
        mListLinearLayout = (LinearLayout)findViewById(R.id.listLinerLayout);
        mContext = this;
        mHUDExternalSensorManager = (HUDExternalSensorManager)HUDOS.getHUDService(HUDOS.HUD_EXTERNAL_SENSOR_SERVICE);
        init();
    }

    private void init() {
        synchronized (mButtons){
            int size = mButtons.size();
            for (int i =0; i < size ; i++) {
                int uid = mButtons.keyAt(i);
                Button button = mButtons.get(uid);
                if (button != null) {
                    removeView(uid);
                }
            }
        }

        if (mDiscovering) {
            mScanButton.setTextColor(Color.GREEN);
        } else {
            mScanButton.setTextColor(Color.BLACK);
        }

        ExternalSensorConnectionParams.ExternalSensorNetworkType type = mHUDExternalSensorManager.getHUDNetworkType();
        if (type == ExternalSensorConnectionParams.ExternalSensorNetworkType.ANT) {
            mBleButton.setEnabled(true);
            mAntButton.setEnabled(false);
        } else {
            mAntButton.setEnabled(true);
            mBleButton.setEnabled(false);
        }

        Log.d(TAG, "Registering listener");
        mHUDExternalSensorManager.registerListener(this);

        // Retrieve the list of saved devices
        List<ExternalSensorConnectionParams> savedListConnectionParams = null;
        try {
            savedListConnectionParams = mHUDExternalSensorManager.getSavedSensorConnectionParams(type);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (savedListConnectionParams != null) {
            for (ExternalSensorConnectionParams connectionParams : savedListConnectionParams) {
                // Create the button for the saved device
                addView(connectionParams.getName(), connectionParams.getUID());

                // Check if it's a connected sensor
                boolean isConnected = false;
                try {
                    isConnected = mHUDExternalSensorManager.isSensorConnected(connectionParams.getUID());
                    Log.d(TAG, "Sensor [" + connectionParams.getName() + "] - connected: " + connectionParams.isConnected());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }

                // Color the button depending on its connected state
                if (isConnected) {
                    changeColor(connectionParams.getUID(), Color.GREEN);
                } else {
                    changeColor(connectionParams.getUID(), Color.MAGENTA);
                }
            }
        } else {
            Log.d(TAG, "No saved list...");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHUDExternalSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setAnt(View view) {
        // Stop the BLEService in limo_apps
        stopService(new Intent("RECON_THE_BLE_SERVICE"));

        mHUDExternalSensorManager.setHUDNetworkType(ExternalSensorConnectionParams.ExternalSensorNetworkType.ANT);

        // Send intent to reconnect all ANT+ sensors from limo_apps
        startService(new Intent("RECON_ANT_SERVICE"));
        init();

        mScanButton.setTextColor(Color.BLACK);
        mDiscovering = false;
    }

    public void setBle(View view) {
        // Send intent to disconnect all ANT+ sensors from limo_apps
        Intent intent = new Intent("RECON_ANT_SERVICE");
        intent.putExtra("disconnect_all", true);
        startService(intent);

        // Stop ANT+ service in limo_apps
        stopService(new Intent("RECON_ANT_SERVICE"));

        mHUDExternalSensorManager.setHUDNetworkType(ExternalSensorConnectionParams.ExternalSensorNetworkType.BLE);

        // Start the BLEService in limo_apps
        startService(new Intent("RECON_THE_BLE_SERVICE"));
        init();

        mScanButton.setTextColor(Color.BLACK);
        mDiscovering = false;
    }

    public void changeColor(Button button, int color) {
        button.setTextColor(color);
    }

    public void changeColor(int uid, int color) {
        new ChangeColor(uid, color).execute();
    }

    public class ChangeColor extends AsyncTask<Void, Void, Void> {
        int color;
        int uid;

        public ChangeColor(int uid, int color) {
            this.uid = uid;
            this.color = color;
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Button button = mButtons.get(uid);
            if (button != null) {
                changeColor(button, color);
            }
        }
    }
    public void addView(final String name, final int uid) {
        if (mButtons.get(uid) != null) {
            return;
        }
        Button button = new Button(MainActivity.this);
        button.setText(name);
        button.setId(uid);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setTextColor(Color.BLACK);
        button.setOnClickListener(mListOnClickListener);
        mListLinearLayout.addView(button);
        mButtons.put(uid, button);
    }

    public void addAsyncView(final String name, final int uid) {
        new AddView(name,uid).execute();
    }

    public class AddView extends AsyncTask<Void,Void,Void> {
        String name;
        int uid;

        public AddView(String name, int uid) {
            this.name = name;
            this.uid = uid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if(mButtons.get(uid) != null){
                return;
            }
            Button button = new Button(MainActivity.this);
            button.setText(name);
            button.setId(uid);
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            button.setTextColor(Color.BLACK);
            button.setOnClickListener(mListOnClickListener);
            mListLinearLayout.addView(button);
            mButtons.put(uid, button);
        }

    };

    public void removeView(int uid) {
        Button button = mButtons.get(uid);
        if (button != null) {
            mButtons.remove(uid);
            mListLinearLayout.removeView(button);
        }
    }

    @Override
    public void onSensorConnected(ExternalSensorConnection sensorConnection) {
        Log.d(TAG,"onSensorConnected");
    }

    @Override
    public void onSensorDisconnected(ExternalSensorConnection sensorConnection) {
        Log.d(TAG,"onSensorDisconnected");
    }

    @Override
    public void onSensorConnectFailure(ExternalSensorConnection sensorConnection) {
        Log.d(TAG, "onSensorConnectFailure");
    }

    @Override
    public void onSensorDiscovered(ExternalSensorConnectionParams connectionParams) {
        Log.d(TAG, "onSensorDiscovered:" + connectionParams.getUID());
        addAsyncView(connectionParams.getName(), connectionParams.getUID());
    }

    @Override
    public void onDiscoveredSensorLost(ExternalSensorConnectionParams connectionParams) {
        //removeView(connectionParams.getUID());
    }

    @Override
    public void onZeroCalibrationResult(boolean b, ExternalSensorConnection cp) {
        Log.d(TAG, "onZeroCalibrationResult: " + b);
    }

    View.OnClickListener mScanButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(mScanButton)) {
                try {
                    if (mDiscovering) {
                        Log.d(TAG, "Stopping discovery");
                        mHUDExternalSensorManager.stopDiscovery();
                        mDiscovering = false;
                        mScanButton.setTextColor(Color.BLACK);
                    } else {
                        Log.d(TAG, "Starting discovery");
                        mHUDExternalSensorManager.startDiscovery();
                        mDiscovering = true;
                        mScanButton.setTextColor(Color.GREEN);
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    View.OnClickListener mListOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, SensorActivity.class);
            intent.putExtra("UID", view.getId());
            mContext.startActivity(intent);
        }
    };
}
