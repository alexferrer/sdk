package com.example.hudmetricsclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.reconinstruments.os.metrics.HUDMetricsID;

public class RunningCadenceActivity extends Activity {
    MetricView mRunningCadence= null;


    RunningCadenceModel mCadenceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runningcadence);

        mRunningCadence = new MetricView((TextView)findViewById(R.id.CadenceCurrent), (TextView)findViewById(R.id.CadencePrevious));

        mCadenceModel = new RunningCadenceModel(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        mCadenceModel.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mCadenceModel.onPause();
    }

    public void UpdateValueChangeText(int metricID, float value) {
        MetricView metricView = GetData(metricID);
        if(metricView != null) {
            metricView.addValue(value);
        }
    }

    private MetricView GetData(int metricID) {
        if(metricID == HUDMetricsID.RUNNING_CADENCE){
            return mRunningCadence;
        }
        return null;
    }
}
