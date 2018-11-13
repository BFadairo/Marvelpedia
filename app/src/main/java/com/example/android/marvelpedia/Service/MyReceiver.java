package com.example.android.marvelpedia.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private static final String ACTION_FOO = "com.example.android.marvelpedia.action.FOO";
    private final String LOG_TAG = MyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals(ACTION_FOO)) {
            Log.v(LOG_TAG, "Inside onReceive");
            //throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}
