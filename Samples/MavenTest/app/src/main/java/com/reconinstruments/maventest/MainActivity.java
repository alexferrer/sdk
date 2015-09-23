package com.reconinstruments.maventest;

import android.app.Activity;

import android.os.Bundle;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.extsensor.HUDExternalSensorManager;

public class MainActivity extends Activity {

    private final String TAG = "MavenTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HUDExternalSensorManager externalSensorManager = (HUDExternalSensorManager) HUDOS.getHUDService(HUDOS.HUD_EXTERNAL_SENSOR_SERVICE);
    }
}