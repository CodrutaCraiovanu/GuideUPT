package com.example.licenta.utils;

import android.content.IntentFilter;

/**
 * Class of constants for the application
 */
public class Constants {

    /**
     * Duration of wait
     **/
    public static final int SPLASH_DELAY = 3000;

    /**
     * Milliseconds before the user can exist the application.
     */
    public static final int APP_CLOSING_TIME = 3000;

    /**
     * The action for the intent filter used with the InternetBroadcast
     */
    public static final String INTENT_FILTER_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    /**
     * The intent filter used alongside InternetBroadcast
     */
    public static final IntentFilter sIntentFilter = new IntentFilter(INTENT_FILTER_ACTION);

    /**
     * Repo's full name from intent
     */
    public static final String LINK_FOR_VIDEO = "link_for_video_intent";

    /**
     * Variable used over the intent with ScannedBarcodeActivity
     */
    public static final int SCANNED_CODE = 1000;

    public static final int REQUEST_CAMERA_PERMISSION = 201;

}
