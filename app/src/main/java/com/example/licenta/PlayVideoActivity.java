package com.example.licenta;

import static com.example.licenta.utils.Constants.APP_CLOSING_TIME;
import static com.example.licenta.utils.Constants.LINK_FOR_VIDEO;
import static com.example.licenta.utils.Constants.sIntentFilter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.licenta.databinding.ActivityPlayVideoBinding;
import com.example.licenta.utils.CheckNetworkState;
import com.example.licenta.utils.InternetBroadcastReceiver;

/**
 * Activity that will display the video
 */
public class PlayVideoActivity extends AppCompatActivity {

    private ActivityPlayVideoBinding mActivityPlayVideoBinding;

    /**
     * Counter for number of back button pressed.
     */
    private boolean mIsPressedBack;

    /**
     * An instance of CheckNetworkState
     */
    private CheckNetworkState mCheckNetworkState;
    /**
     * Reference of the broadcast receiver
     */
    private InternetBroadcastReceiver mInternetBroadcastReceiver;
    private BroadcastReceiver broadcastReceiver;

    /**
     * Method called when the activity is starting
     * Initialize the toolbar
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being
     *                           shut down then this variable contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //code used for check and supervise the internet connection
        mInternetBroadcastReceiver = new InternetBroadcastReceiver();
        mCheckNetworkState = CheckNetworkState.getCheckInstance(this);
        mCheckNetworkState.checkInternetConnection(this);

        // bind the activity for being able tu get/set elements from it
        mActivityPlayVideoBinding = DataBindingUtil.setContentView(PlayVideoActivity.this, R.layout.activity_play_video);

        Intent intent = getIntent();

        // Get the link from intent
        String fullLinkForPlayVideo = intent.getStringExtra(LINK_FOR_VIDEO);

        // Uri object to refer the
        // resource from the videoUrl
        Uri uri = Uri.parse(fullLinkForPlayVideo);

        mActivityPlayVideoBinding.VideoView.setVideoURI(uri);

        // creating object of
        // media controller class
        MediaController mediaController = new MediaController(this);

        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(mActivityPlayVideoBinding.VideoView);

        // sets the media player to the videoView
        mediaController.setMediaPlayer(mActivityPlayVideoBinding.VideoView);

        // sets the media controller to the videoView
        mActivityPlayVideoBinding.VideoView.setMediaController(mediaController);

        // starts the video
        mActivityPlayVideoBinding.VideoView.start();


        // add listener on ImageView and TextView in order to go on ScannedBarcodeActivity
        mActivityPlayVideoBinding.QrImageView.setOnClickListener(view -> {

            Intent goAgainAndScanQRActivity = new Intent(this, ScannedBarcodeActivity.class);
            startActivity(goAgainAndScanQRActivity);

        });

        mActivityPlayVideoBinding.textViewNewQrCode.setOnClickListener(view -> {

            Intent goAgainAndScanQRActivity = new Intent(this, ScannedBarcodeActivity.class);
            startActivity(goAgainAndScanQRActivity);
        });

        setUpToolbar();


        listenActivityForHeadphones();
    }

    private void listenActivityForHeadphones() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                final String action = intent.getAction();
                int headphonesState;

                if (Intent.ACTION_HEADSET_PLUG.equals(action)) {

                    headphonesState = intent.getIntExtra("state", -1);
                    if (headphonesState == 0) {

                        mActivityPlayVideoBinding.VideoView.suspend();
                        Toast.makeText(getApplicationContext(), "Connect again the headphones!", Toast.LENGTH_LONG).show();
                    }
                    if (headphonesState == 1) {
                        mActivityPlayVideoBinding.VideoView.resume();
                        mActivityPlayVideoBinding.VideoView.start();
                        Toast.makeText(getApplicationContext(), "The headphones are connected now!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(broadcastReceiver, receiverFilter);
    }

    /**
     * Initialize the toolbar
     */
    public void setUpToolbar() {

        if (mActivityPlayVideoBinding != null) {

            setSupportActionBar(mActivityPlayVideoBinding.toolbar.toolbarMain);
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {

                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    /**
     * Method called when an item from the toolbar is pressed
     *
     * @param item : The item that is pressed
     * @return : boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.toolbar_exit) {

            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finish();
        }
        if(item.getItemId() == R.id.toolbar_home){
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize the menu components
     *
     * @param menu is a reference to a Menu object
     * @return a boolean value for displaying the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Checks if the user is on the first page after login. If so, on back button pressed,
     * exit the application, otherwise stay on main page.
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {

            if (mIsPressedBack) {

                super.onBackPressed();
                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                finishAffinity();
                finishAndRemoveTask();
                return;
            }

            this.mIsPressedBack = true;
            Toast.makeText(this, R.string.app_exit,
                    Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> mIsPressedBack = false,
                    APP_CLOSING_TIME);
        }
    }

    /**
     * Method called when the activity becomes visible to the user
     * The internet connection is checked
     */
    @Override
    protected void onStart() {

        super.onStart();
        mCheckNetworkState.checkInternetConnection(this);
    }

    /**
     * Override onPause method
     * Unregisters the broadcast receiver
     */
    @Override
    protected void onPause() {

        unregisterReceiver(mInternetBroadcastReceiver);
        super.onPause();
    }

    /**
     * Called at the start of the active lifetime.
     * Registers again the broadcast receiver
     */
    @Override
    protected void onResume() {

        registerReceiver(mInternetBroadcastReceiver, sIntentFilter);
        super.onResume();
    }

}