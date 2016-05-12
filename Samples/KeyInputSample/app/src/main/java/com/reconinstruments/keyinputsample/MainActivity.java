package com.reconinstruments.keyinputsample;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class MainActivity extends Activity
{
    static final String TAG   = "Main";
    TextView mTextView        = null;
    AudioManager audioManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView    = (TextView) findViewById(R.id.mTextView);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.v(TAG, "onKeyDown keyCode: " + keyCode);
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                doStuff("LEFT", AudioManager.FX_FOCUS_NAVIGATION_LEFT);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                doStuff("RIGHT", AudioManager.FX_FOCUS_NAVIGATION_RIGHT);
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                doStuff("UP", AudioManager.FX_FOCUS_NAVIGATION_UP);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                doStuff("DOWN", AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                doStuff("SELECT", AudioManager.FX_KEYPRESS_RETURN);
                return true;
            case KeyEvent.KEYCODE_BACK:
                doStuff("BACK", AudioManager.FX_KEYPRESS_DELETE);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Change text view to input type
    // Play audio for specific input type
    private void doStuff(String text, int audioFX)
    {
        mTextView.setText(text);
        audioManager.playSoundEffect(audioFX, 1);
    }
}