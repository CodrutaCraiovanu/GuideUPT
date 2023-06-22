package com.example.licenta.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.example.licenta.R;
import com.example.licenta.databinding.InternetDialogBinding;

/**
 * Class that contains a method to check the user's internet connection
 */
public class CheckNetworkState {
    /**
     * Instance of the CheckConnection in order to be instantiated singleton.
     */
    @SuppressLint("StaticFieldLeak")
    private static volatile CheckNetworkState sConnectionInstance;

    /**
     * The context from the activity in which the method checkInternetConnection is called.
     *
     * @param mContext the context from the activity.
     */
    private CheckNetworkState(Context mContext) {
        /**
         * The context from the activity.
         */
    }

    /**
     * Verifies if the mConnectionInstance in null, if so synchronizes the CheckConnection
     * class to the activity where is going to be instantiated.
     *
     * @param context : the context from the activity where the check is performed.
     * @return a new single instance of the CheckConnection class.
     */
    public static CheckNetworkState getCheckInstance(Context context) {

        if (sConnectionInstance == null) {

            synchronized (CheckNetworkState.class) {

                if (sConnectionInstance == null) {

                    sConnectionInstance = new CheckNetworkState(context);
                }
            }
        }
        return sConnectionInstance;
    }

    /**
     * Method used to check if there is internet connection
     * When there is no internet connection an AlertDialog will appear
     * The AlertDialog can be dismissed when the internet connection is restored.
     *
     * @param context the current activity's context
     */
    public void checkInternetConnection(Context context) {
        if (!ConnectionUtil.isOnline(context.getApplicationContext())) {

            final Dialog noInternetAlertDialog = new Dialog(context);
            InternetDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(
                    noInternetAlertDialog.getContext()), R.layout.internet_dialog, null,
                    false);

            noInternetAlertDialog.setContentView(binding.getRoot());
            noInternetAlertDialog.show();
            noInternetAlertDialog.setCancelable(false);

            binding.btnInternet.setOnClickListener(view -> {

                if (ConnectionUtil.isOnline(context.getApplicationContext())) {

                    noInternetAlertDialog.dismiss();
                }
            });
        }
    }
}
