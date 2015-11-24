package com.reconinstruments.bluetoothledemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

@SuppressLint("NewApi")
public class BLEActivity extends Activity
{
    private final static String TAG = "BluetoothLETest";

    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private TextView leNameText;
    private TextView leAddressText;
    private TextView leStatusText;

    private String leDeviceName;
    private String leDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_main);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume()");

        Intent intent = getIntent();
        leDeviceName = intent.getStringExtra("name");
        leDeviceAddress = intent.getStringExtra("address");

        leNameText = (TextView) findViewById(R.id.leNameText);
        leAddressText = (TextView) findViewById(R.id.leAddressText);

        leStatusText = (TextView) findViewById(R.id.leStatusText);
        leStatusText.setText("- Connecting -");

        leNameText.setText(leDeviceName);
        leAddressText.setText(leDeviceAddress);

        // Attempt BLE Connection
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null)
        {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
            {
                Log.e(TAG, "BT Adapter is null or not enabled!");
            }
        }
        else { Log.e(TAG, "Unable to retrieve BluetoothManager"); }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(leDeviceAddress);
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause()");
        if (mBluetoothGatt != null)
        {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        super.onPause();
    }

    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                broadcastMessage("- Connected -");

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastMessage("- Disconnected -");
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                Log.i(TAG, "Services discovered.");
                broadcastMessage("- Services Discovered -");
            }
            else { Log.w(TAG, "onServicesDiscovered received: " + status); }
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                Log.i(TAG, "Data is available.");
                broadcastMessage("- Data Available -");
            }
        }
    };

    private void broadcastMessage(final String msg)
    {
        Message strMsg = new Message();
        strMsg.obj = msg;
        messageHandler.sendMessage(strMsg);
    }

    private Handler messageHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            leStatusText.setText((String) msg.obj);
        }
    };
}
