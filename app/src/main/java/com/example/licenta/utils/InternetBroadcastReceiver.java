package com.example.licenta.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class used for broadcast receiver for internet notification
 */
@SuppressLint("UnsafeProtectedBroadcastReceiver")
public class InternetBroadcastReceiver extends BroadcastReceiver {

    /**
     * Method that attaches the broadcast to the activity via context
     *
     * @param context : the application's context
     * @param intent  : the intent for the internet notification
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        CheckNetworkState.getCheckInstance(context)
                .checkInternetConnection(context);
    }
}