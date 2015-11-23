package com.reconinstruments.bluetoothledemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressLint("NewApi")
public class BLEActivity extends Activity
{
    private final static String TAG = "BluetoothLETest";

    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

    private TextView leNameText;
    private TextView leAddressText;
    private TextView leStatusText;

    private String leDeviceName;
    private String leDeviceAddress;

    private boolean isConnected = false;

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
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            }
            else { Log.w(TAG, "onServicesDiscovered received: " + status); }
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
    };

    private void broadcastUpdate(final String action)
    {
        Log.d(TAG, "!!!!!!! ------> sendBroadcast(" + action + ")");
        final Intent intent = new Intent(action);
       //sendBroadcast(intent);
        mGattUpdateReceiver.onReceive(this, intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic)
    {
        Log.d(TAG, "!!!!!!! ------> sendBroadcast(" + action + ")");
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.
        // Data parsing is carried out as per profile specifications.
        if(UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid()))
        {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0)
            {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            }
            else
            {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        }
        else
        {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0)
            {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data){ stringBuilder.append(String.format("%02X ", byteChar)); }
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        //sendBroadcast(intent);
        mGattUpdateReceiver.onReceive(this, intent);
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a result of read or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if(ACTION_GATT_CONNECTED.equals(action))
            {
                isConnected = true;
                leStatusText.setText("- Connected -");
                Log.d(TAG, "mGattUpdateReceiver(ACTION_GATT_CONNECTED)");
            }
            else if(ACTION_GATT_DISCONNECTED.equals(action))
            {
                isConnected = false;
                leStatusText.setText("- Disconnected -");
                Log.d(TAG, "mGattUpdateReceiver(ACTION_GATT_DISCONNECTED)");
            }
            else if(ACTION_GATT_SERVICES_DISCOVERED.equals(action))
            {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothGatt.getServices());
                Log.d(TAG, "mGattUpdateReceiver(ACTION_GATT_SERVICES_DISCOVERED)");
            }
            else if(ACTION_DATA_AVAILABLE.equals(action))
            {
                Log.d(TAG, "mGattUpdateReceiver(ACTION_DATA_AVAILABLE)");
            }
        }
    };

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices)
    {
        if (gattServices == null){ return; }

        Log.d(TAG, "SERVICE (0x180D): " + 0x180d);

        for(BluetoothGattService bGS : gattServices)
        {
            Log.d(TAG, "GATT SERVICE ---> type: " + bGS.getType() + ", uuid: " + bGS.getUuid().toString().split("[-]")[0]);
        }
    }
}
