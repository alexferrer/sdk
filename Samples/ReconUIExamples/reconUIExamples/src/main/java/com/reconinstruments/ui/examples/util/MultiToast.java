package com.reconinstruments.ui.examples.util;

import android.content.Context;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

/**
 * Simple utility class for a toast that can receive multiple messages in quick succession without waiting for the last
 * toast to disappear, useful for testing. Also shrinks the text size, allowing for more messages to be displayed
 *
 * Works by clearing the last toast whenever a new one appears, and showing a new toast with all messages received very
 * recently.
 */
public class MultiToast {
    Context context;
    Handler handler;
    Toast toast;
    LinkedList<String> messages;

    public MultiToast(Context context) {
        this.context = context;
        handler = new Handler();
        messages = new LinkedList<String>();
    }

    public void addMessage(final String newMessage) {
        if(toast!=null)
            toast.cancel();
        String toastText = newMessage;
        for(String message: messages) {
            toastText += "\n"+message;
        }
        messages.add(newMessage);
        toast = Toast.makeText(context,toastText,Toast.LENGTH_LONG);
        // set toast text size
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTextSize(20);

        toast.show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messages.remove(newMessage);
            }
        }, 50);
    }
}