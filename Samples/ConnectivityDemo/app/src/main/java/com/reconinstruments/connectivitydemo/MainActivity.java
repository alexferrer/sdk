package com.reconinstruments.connectivitydemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.os.connectivity.IHUDConnectivity;

public class MainActivity extends Activity implements View.OnClickListener, IHUDConnectivity
{
    private static final String TAG = "MainActivity";
    private HUDConnectivityManager mHUDConnectivityManager = null;

    private Button mConnectDevice;
    private Button mDownloadFile;
    private Button mUploadFile;
    private Button mDownloadImage;
    private Button mHUDConnectedTV;
    private Button mLocalWebTV;
    private Button mRemoteWebTV;

    private final int MMUL    = 0xFFFFFFFF;
    private final int MRED    = 0xFFFF0000;
    private final int MORANGE = 0xFFFF6600;
    private final int MGREEN  = 0xFFFFFF00;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Button + Other graphical components
        mDownloadFile = (Button) findViewById(R.id.download_file_button);
        mUploadFile = (Button) findViewById(R.id.upload_file_button);
        mDownloadImage = (Button) findViewById(R.id.load_image_button);
        mConnectDevice = (Button) findViewById(R.id.connected_device);
        mHUDConnectedTV = (Button) findViewById(R.id.hud_connected);
        mLocalWebTV = (Button) findViewById(R.id.local_web_connected);
        mRemoteWebTV = (Button) findViewById(R.id.remote_web_connected);

        mDownloadFile.setOnClickListener(this);
        mUploadFile.setOnClickListener(this);
        mDownloadImage.setOnClickListener(this);
        mConnectDevice.setOnClickListener(this);

        mDownloadFile.setEnabled(false);
        mUploadFile.setEnabled(false);
        mDownloadImage.setEnabled(false);


        //Get an instance of HUDConnectivityManager
        mHUDConnectivityManager = (HUDConnectivityManager) HUDOS.getHUDService(HUDOS.HUD_CONNECTIVITY_SERVICE);
        if(mHUDConnectivityManager == null)
        {
            Log.e(TAG, "Failed to get HUDConnectivityManager");
            finish();
        }

        setColorFilter(mHUDConnectedTV, MRED);
        setColorFilter(mLocalWebTV, MRED);
        setColorFilter(mRemoteWebTV, MRED);
        if(mHUDConnectivityManager.isHUDConnected())
        {
            Log.d(TAG, "HUD is connected.");
            mHUDConnectedTV.setText("HUD Connected");
        }
        else
        {
            Log.d(TAG, "HUD is disconnected.");
            mHUDConnectedTV.setText("HUD Disconnected");
        }

        // !!!! -------- IMPORTANT NOTICE -------- !!!! //
        //Note: This following line is necessary for HUDConnectivityManager to run properly
        System.load("/system/lib/libreconinstruments_jni.so");
        // !!!! -------- IMPORTANT NOTICE -------- !!!! //
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //registering the IHUDConnectivity to HUDConnectivityManager
        mHUDConnectivityManager.register(this);
    }

    @Override
    public void onStop(){
        //unregistering the IHUDConnectivity from HUDConnectivityManager
        mHUDConnectivityManager.unregister(this);
        super.onStop();
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = null;
        if (v == mDownloadImage)
        {
            intent = new Intent(this, ImageDownloadActivity.class);
            intent.putExtra(ImageDownloadActivity.EXTRA_IMAGE_URL, "http://www.reconinstruments.com/wp-content/themes/recon/img/jet/slide-3.jpg");
            startActivity(intent);
        }
        else if (v == mDownloadFile)
        {
            intent = new Intent(this, FileDownloadActivity.class);
            startActivity(intent);
        }
        else if (v == mUploadFile)
        {
            intent = new Intent(this, FileUploadActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionStateChanged(ConnectionState state)
    {
        Log.d(TAG,"onConnectionStateChanged : state:" + state);
        switch (state) {
            case LISTENING:
                mHUDConnectedTV.setText("HUD Listening");
                setColorFilter(mHUDConnectedTV, MRED);
                break;
            case CONNECTED:
                mHUDConnectedTV.setText("Phone Connected");
                setColorFilter(mHUDConnectedTV, MGREEN);
                break;
            case CONNECTING:
                mHUDConnectedTV.setText("Phone Connecting");
                setColorFilter(mHUDConnectedTV, MORANGE);
                break;
            case DISCONNECTED:
                mHUDConnectedTV.setText("Phone Disconnected");
                setColorFilter(mHUDConnectedTV, MRED);
                break;
            default:
                Log.e(TAG,"onConnectionStateChanged() with unknown state:" + state);
                break;
        }
    }

    @Override
    public void onNetworkEvent(NetworkEvent networkEvent, boolean hasNetworkAccess)
    {
        Log.d(TAG, "onNetworkEvent : networkEvent:" + networkEvent + " hasNetworkAccess:" + hasNetworkAccess);
        switch (networkEvent) {
            case LOCAL_WEB_GAINED:
                setColorFilter(mLocalWebTV, MGREEN);
                break;
            case LOCAL_WEB_LOST:
                setColorFilter(mLocalWebTV, MRED);
                break;
            case REMOTE_WEB_GAINED:
                setColorFilter(mRemoteWebTV, MGREEN);
                break;
            case REMOTE_WEB_LOST:
                setColorFilter(mRemoteWebTV, MRED);
                break;
            default:
                Log.e(TAG,"onNetworkEvent() with unknown networkEvent:" + networkEvent);
                break;
        }
        mDownloadFile.setEnabled(hasNetworkAccess);
        mUploadFile.setEnabled(hasNetworkAccess);
        mDownloadImage.setEnabled(hasNetworkAccess);
    }

    @Override
    public void onDeviceName(String deviceName)
    {
        Log.d(TAG,"onDeviceName : deviceName:" + deviceName);
        mConnectDevice.setText(deviceName);
    }

    private void setColorFilter(View view, int color)
    {
        view.getBackground().setColorFilter(new LightingColorFilter(MMUL, color));
    }
}
