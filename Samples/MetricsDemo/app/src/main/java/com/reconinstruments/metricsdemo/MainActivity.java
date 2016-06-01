package com.reconinstruments.metricsdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = "HUDMetricsClientActivity";

    private Button mAltButton;
    private Button mSpeedButton;
    private Button mGradeButton;
    private Button mExternalSensorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAltButton = (Button) findViewById(R.id.buttonAlt);
        mSpeedButton = (Button) findViewById(R.id.buttonSpeed);
        mGradeButton = (Button) findViewById(R.id.buttonGrade);
        mExternalSensorButton = (Button) findViewById(R.id.buttonExternalSensor);
        mAltButton.setOnClickListener(this);
        mSpeedButton.setOnClickListener(this);
        mGradeButton.setOnClickListener(this);
        mExternalSensorButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.buttonAlt:
                Intent altIntent = new Intent(getApplicationContext(), AltitudeActivity.class);
                startActivity(altIntent);
                break;
            case R.id.buttonSpeed:
                Intent speedIntent = new Intent(getApplicationContext(), SpeedActivity.class);
                startActivity(speedIntent);
                break;
            case R.id.buttonGrade:
                Intent gradeIntent = new Intent(getApplicationContext(), GradeActivity.class);
                startActivity(gradeIntent);
                break;
            case R.id.buttonExternalSensor:
                Intent externlSensorIntent = new Intent(getApplicationContext(), ExternalSensorActivity.class);
                startActivity(externlSensorIntent);
                break;
        }
    }
}
