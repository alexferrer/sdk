package com.reconinstruments.metricsdemo;

import android.util.Log;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.metrics.HUDMetricsID;
import com.reconinstruments.os.metrics.HUDMetricsManager;
import com.reconinstruments.os.metrics.MetricsValueChangedListener;

public class ExternalSensorModel implements MetricsValueChangedListener{
    private final static String TAG = ExternalSensorModel.class.getName();
    ExternalSensorActivity mActivity;

    HUDMetricsManager mHUDMetricsManager = null;

    public ExternalSensorModel(ExternalSensorActivity extSensorActivity){
        mActivity = extSensorActivity;
        mHUDMetricsManager = (HUDMetricsManager)HUDOS.getHUDService(HUDOS.HUD_METRICS_SERVICE);
        if (mHUDMetricsManager == null) {
            Log.e(TAG, "Failed to get HUDMetricsManager");
        }
    }

    @Override
    public void onMetricsValueChanged(int metricID, float value, long changeTime, boolean isValid) {
        Log.d(TAG,"onMetricsValueChanged: metricID="+metricID+" value="+value+" changeTime="+changeTime);
        mActivity.UpdateValueChangeText(metricID, value);
    }

    public void onResume() {
        try {
            mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.HEART_RATE);
            mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.SPEED_CADENCE_CADENCE);
            mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.SPEED_CADENCE_SPEED);
            mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.SPEED_EXT);
            mHUDMetricsManager.registerMetricsListener(this, HUDMetricsID.CADENCE_EXT);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        try {
            mHUDMetricsManager.unregisterMetricsListener(this, HUDMetricsID.HEART_RATE);
            mHUDMetricsManager.unregisterMetricsListener(this, HUDMetricsID.SPEED_CADENCE_CADENCE);
            mHUDMetricsManager.unregisterMetricsListener(this, HUDMetricsID.SPEED_CADENCE_SPEED);
            mHUDMetricsManager.unregisterMetricsListener(this, HUDMetricsID.SPEED_EXT);
            mHUDMetricsManager.unregisterMetricsListener(this, HUDMetricsID.CADENCE_EXT);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
