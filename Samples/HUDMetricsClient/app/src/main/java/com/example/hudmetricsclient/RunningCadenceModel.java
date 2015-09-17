package com.example.hudmetricsclient;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.metrics.HUDMetricsID;
import com.reconinstruments.os.metrics.HUDMetricsManager;
import com.reconinstruments.os.metrics.MetricsValueChangedListener;

public class RunningCadenceModel implements MetricsValueChangedListener{
    private final static String TAG = RunningCadenceModel.class.getName();
    RunningCadenceActivity mActivity;

    HUDMetricsManager mHUDMetricsManager = null;

    public RunningCadenceModel(RunningCadenceActivity runningcadenceActivity){
        mActivity = runningcadenceActivity;
        mHUDMetricsManager = (HUDMetricsManager)HUDOS.getHUDService(HUDOS.HUD_METRICS_SERVICE);
        if (mHUDMetricsManager == null) {
            Log.e(TAG, "Failed to get HUDMetricsManager!");
        }
    }

    @Override
    public void onMetricsValueChanged(int metricID, float value, long changeTime, boolean isValid) {
        Log.d(TAG,"onMetricsValueChanged: metricID="+metricID+" value="+value+" changeTime="+changeTime);
        mActivity.UpdateValueChangeText(metricID, value);
    }

    public void onResume() {
        try {
            mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.RUNNING_CADENCE);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        try {
            mHUDMetricsManager.unregisterMetricsListener(this, HUDMetricsID.RUNNING_CADENCE);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}