package com.example.licenta.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class for checking the internet connection
 */
public class ConnectionUtil {

    /**
     * method checks if device is connected to internet
     *
     * @param context of application
     * @return true if device has internet connection, false otherwise
     */
    public static boolean isOnline(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
    }
}
