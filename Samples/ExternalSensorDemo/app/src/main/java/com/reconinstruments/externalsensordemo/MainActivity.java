package com.reconinstruments.externalsensordemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.extsensor.HUDExternalSensorManager;
import com.reconinstruments.os.hardware.extsensor.ExternalSensorConnectionParams;

import java.util.List;

public class MainActivity extends Activity {

    private final static String TAG = "ExternalSensorDemoA";
    Button mAntButton;
    Button mBleButton;

    LinearLayout mListLinearLayout;

    SparseArray<Button> mButtons = new SparseArray<Button>();

    public static HUDExternalSensorManager mHUDExternalSensorManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAntButton = (Button)findViewById(R.id.ant);
        mBleButton = (Button)findViewById(R.id.ble);
        mListLinearLayout = (LinearLayout)findViewById(R.id.listLinerLayout);
        mContext = this;
        mHUDExternalSensorManager = (HUDExternalSensorManager) HUDOS.getHUDService(HUDOS.HUD_EXTERNAL_SENSOR_SERVICE);
        int id = HUDOS.HUD_CONNECTIVITY_SERVICE;
        init();
    }

    private void init() {
        synchronized (mButtons)
        {
            int size = mButtons.size();
            for(int i = 0; i < size; i++)
            {
                int uid = mButtons.keyAt(i);
                Button button = mButtons.get(uid);
                if(button != null){ removeView(uid); }
            }
        }

        // Show which sensor type is in use (ANT+ or BLE)
        ExternalSensorConnectionParams.ExternalSensorNetworkType type = mHUDExternalSensorManager.getHUDNetworkType();
        mAntButton.setEnabled(type == ExternalSensorConnectionParams.ExternalSensorNetworkType.ANT);
        mBleButton.setEnabled(type == ExternalSensorConnectionParams.ExternalSensorNetworkType.BLE);

        // Retrieve the list of saved devices
        List<ExternalSensorConnectionParams> savedListConnectionParams = null;
        try{ savedListConnectionParams = mHUDExternalSensorManager.getSavedSensorConnectionParams(type); }
        catch(RuntimeException e){ e.printStackTrace(); }

        if(savedListConnectionParams != null)
        {
            for(ExternalSensorConnectionParams connectionParams : savedListConnectionParams)
            {
                // Create the button for the saved device
                addView(connectionParams.getName(), connectionParams.getSensorType().name(), connectionParams.getUID());

                // Check if it's a connected sensor
                boolean isConnected = false;
                try
                {
                    isConnected = mHUDExternalSensorManager.isSensorConnected(connectionParams.getUID());
                    Log.d(TAG, "Sensor [" + connectionParams.getName() + "] - connected: " + connectionParams.isConnected());
                }
                catch(RuntimeException e){ e.printStackTrace(); }

                // Color the button depending on its connected state
                if(isConnected){ setButtonTextColorByID(connectionParams.getUID(), Color.GREEN); }
                else           { setButtonTextColorByID(connectionParams.getUID(), Color.RED);   }
            }
        }
        else { Log.d(TAG, "No saved list..."); }
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setButtonTextColorByID(int uid, int color)
    {
        Button button = mButtons.get(uid);
        if(button != null){ button.setTextColor(color); }
    }

    public void addView(final String name, final String type, final int uid) {
        if(mButtons.get(uid) != null){ return; }
        Button button = new Button(MainActivity.this);
        button.setText(name + " [" + type + "]");
        button.setId(uid);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setTextColor(Color.BLACK);
        button.setOnClickListener(mListOnClickListener);

        mListLinearLayout.addView(button);
        mButtons.put(uid, button);
    }

    public void removeView(int uid) {
        Button button = mButtons.get(uid);
        if (button != null) {
            mButtons.remove(uid);
            mListLinearLayout.removeView(button);
        }
    }

    View.OnClickListener mListOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view)
        {
            Intent intent = new Intent(mContext, SensorActivity.class);
            intent.putExtra("UID", view.getId());
            mContext.startActivity(intent);
        }
    };
}
