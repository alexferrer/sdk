package com.reconinstruments.glancedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.glance.GlanceDetectionListener;
import com.reconinstruments.os.hardware.glance.HUDGlanceManager;
import com.reconinstruments.os.hardware.glance.RemovalDetectionListener;


public class GlanceDemo extends Activity implements GlanceDetectionListener, RemovalDetectionListener {

    private static final String TAG = "GlanceDemo";
    private HUDGlanceManager mManager = null;
    TextView mTextView = null;
    Button mGlanceButton = null;
    Button mRemovalButton = null;
    boolean mGlanceStarted = false;
    boolean mRemovalStarted = false;
    boolean mRegisterGlanceOnResume = false;
    boolean mRegisterRemovalOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_glance_demo);

        mTextView = (TextView) findViewById(R.id.text);
        mGlanceButton = (Button) findViewById(R.id.glance_btn);
        mRemovalButton = (Button) findViewById(R.id.removal_btn);

        mManager = (HUDGlanceManager) HUDOS.getHUDService(HUDOS.HUD_GLANCE_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: glance started: " + mGlanceStarted + " re-register glance on resume: " + mRegisterGlanceOnResume);
        if (mGlanceStarted || mRegisterGlanceOnResume) {
            mRegisterGlanceOnResume = false;
            Log.d(TAG, "Glance started. Registering glance detection");
            int ret = mManager.registerGlanceDetection(this);
            if (ret == HUDGlanceManager.EVENT_SUCCESS) {
                mGlanceStarted = true;
                mGlanceButton.setText("Stop Glance");
            }
        }
        Log.d(TAG, "onResume: removal started: " + mRemovalStarted + " re-register removal on resume: " + mRegisterRemovalOnResume);
        if (mRemovalStarted || mRegisterRemovalOnResume) {
            mRegisterRemovalOnResume = false;
            Log.d(TAG, "Removal started. Registering removal detection");
            int ret = mManager.registerRemovalDetection(this);
            if (ret == HUDGlanceManager.EVENT_SUCCESS) {
                mRemovalStarted = true;
                mRemovalButton.setText("Stop Removal");
            }
        }
    }

    @Override
    public void onDetectEvent(boolean b) {
        Log.d(TAG, "onDetectEvent: " + (b ? "at Display" : "at Ahead"));
        String text = b ? "Display" : "Ahead";
        if (text != null) {
            Bundle bundle = new Bundle();
            bundle.putString("Text", text);
            Message msg = mHandler.obtainMessage(UPDATE_TEXT);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGlanceStarted) {
            mManager.unregisterGlanceDetection(this);
            mGlanceStarted = false;
            mGlanceButton.setText("Start Glance");
        }
        if (mRemovalStarted) {
            mManager.unregisterRemovalDetection(this);
            mRemovalStarted = false;
            mRemovalButton.setText("Start Removal");
        }
    }

    public void startGlance(View view) {
        if (mGlanceStarted) {
            mManager.unregisterGlanceDetection(this);
            mGlanceStarted = false;
            mGlanceButton.setText("Start Glance");
        } else {
            int event = mManager.registerGlanceDetection(this);
            Log.d(TAG, "Last event: " + event);
            if (event == HUDGlanceManager.EVENT_UNCALIBRATED) {
                // Set to true to re-register on resume
                mRegisterGlanceOnResume = true;
                mRegisterRemovalOnResume = true;
                Intent i = new Intent("com.reconinstruments.jetappsettings.glancecalibrate");
                startActivity(i);
            } else if (event == HUDGlanceManager.EVENT_ERROR) {
                Log.d(TAG, "Error event");
            } else if (event == HUDGlanceManager.EVENT_SUCCESS){
                Log.d(TAG, "SUCCESS!");
                mGlanceStarted = true;
                mGlanceButton.setText("Stop Glance");
            }
        }
    }

    public void startRemoval(View view) {
        Log.d(TAG, "startRemoval: " + mRemovalStarted);
        if (mRemovalStarted) {
            mManager.unregisterRemovalDetection(this);
            mRemovalStarted = false;
            mRemovalButton.setText("Start Removal");
        } else {
            int event = mManager.registerRemovalDetection(this);
            Log.d(TAG, "Last removal event: " + event);
            if (event == HUDGlanceManager.EVENT_ERROR) {
                Log.d(TAG, "Error event");
            } else if (event == HUDGlanceManager.EVENT_SUCCESS){
                Log.d(TAG, "SUCCESS!");
                mRemovalStarted = true;
                mRemovalButton.setText("Stop Removal");
            }
        }
    }

    @Override
    public void onRemovalEvent(boolean b) {
        Log.d(TAG, "onRemovalEvent: " + (b ? "removed" : "on head"));
        String text = null;
        if (b) {
            text = "Removed";
        } else {
            text = "On Head";
        }
        Log.d(TAG, "Text: " + text);

        if (text != null) {
            Bundle bundle = new Bundle();
            bundle.putString("Text", text);
            Message msg = mHandler.obtainMessage(UPDATE_TEXT);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private static final int UPDATE_TEXT = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    Bundle b = msg.getData();
                    String text = b.getString("Text");
                    mTextView.setText(text);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}