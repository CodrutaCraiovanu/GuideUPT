package com.example.licenta;

import android.Manifest;

import static com.example.licenta.utils.Constants.APP_CLOSING_TIME;
import static com.example.licenta.utils.Constants.LINK_FOR_VIDEO;
import static com.example.licenta.utils.Constants.REQUEST_CAMERA_PERMISSION;
import static com.example.licenta.utils.Constants.SCANNED_CODE;
import static com.example.licenta.utils.Constants.sIntentFilter;
import static com.example.licenta.utils.ScannerUtils.checkIfTheLinkIsValid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.example.licenta.databinding.ActivityScannedBarcodeBinding;
import com.example.licenta.utils.CheckNetworkState;
import com.example.licenta.utils.InternetBroadcastReceiver;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Activity that will display the camera scanner
 */
public class ScannedBarcodeActivity extends AppCompatActivity {

    /**
     * An instance of ActivityScannedBarcodeBinding
     */
    private ActivityScannedBarcodeBinding mActivityScannedBarcodeBinding;

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

    /**
     * An instance of CameraSource
     */
    private CameraSource cameraSource;

    private String intentDataForGrtTheLinkFromScanner = "";

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
        mActivityScannedBarcodeBinding = DataBindingUtil.setContentView(ScannedBarcodeActivity.this, R.layout.activity_scanned_barcode);

        initViews();

        setUpToolbar();
    }

    /**
     * Method used for init views
     */
    private void initViews() {

        mActivityScannedBarcodeBinding.btnPlayVideo.setVisibility(View.GONE);
        mActivityScannedBarcodeBinding.btnScanAgainQrCode.setVisibility(View.GONE);
        mActivityScannedBarcodeBinding.textPlayVideo.setVisibility(View.GONE);
        mActivityScannedBarcodeBinding.textScanAgain.setVisibility(View.GONE);
    }

    /**
     * Method used for init the barcode detector and camera source,
     * also used for listen the activity of barcode detector
     */
    private void initialiseDetectorsAndSources() {

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        // put the camera source on surface view
        mActivityScannedBarcodeBinding.surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                openCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // listen the activity of barcode detector
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barCode = detections.getDetectedItems();

                if (barCode.size() > 0) {

                    setBarCode(barCode);
                }
            }
        });
    }

    /**
     * Method used for check if permissions are granted and init the surface view
     */
    private void openCamera() {

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                cameraSource.start(mActivityScannedBarcodeBinding.surfaceView.getHolder());

            } else {
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setBarCode(final SparseArray<Barcode> barCode) {

        mActivityScannedBarcodeBinding.txtBarcodeValue.post(() -> {

            intentDataForGrtTheLinkFromScanner = barCode.valueAt(0).displayValue;

            if (!intentDataForGrtTheLinkFromScanner.equals("")) {

                mActivityScannedBarcodeBinding.txtBarcodeValue.setText(intentDataForGrtTheLinkFromScanner);

                if (checkIfTheLinkIsValid(intentDataForGrtTheLinkFromScanner)) {

                    mActivityScannedBarcodeBinding.txtBarcodeValue.setText(intentDataForGrtTheLinkFromScanner);

                    mActivityScannedBarcodeBinding.btnPlayVideo.setVisibility(View.VISIBLE);
                    mActivityScannedBarcodeBinding.btnScanAgainQrCode.setVisibility(View.VISIBLE);
                    mActivityScannedBarcodeBinding.textPlayVideo.setVisibility(View.VISIBLE);
                    mActivityScannedBarcodeBinding.textScanAgain.setVisibility(View.VISIBLE);

                    mActivityScannedBarcodeBinding.btnPlayVideo.setOnClickListener(view -> {

                        if (isWiredHeadsetOn()) {

                            Intent goToPlayVideoActivity = new Intent(this, PlayVideoActivity.class);
                            goToPlayVideoActivity.putExtra(LINK_FOR_VIDEO, intentDataForGrtTheLinkFromScanner);
                            setResult(SCANNED_CODE, goToPlayVideoActivity);
                            startActivity(goToPlayVideoActivity);
                        } else {

                            Toast.makeText(getApplicationContext(), "Connect the headphones to play the video", Toast.LENGTH_SHORT).show();
                        }
                    });

                    scanAgainQrCode();
                } else {

                    mActivityScannedBarcodeBinding.btnPlayVideo.setVisibility(View.GONE);
                    mActivityScannedBarcodeBinding.btnScanAgainQrCode.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Invalid QR code", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    /**
     * Method used for scan again the qr code and init the ScannedBarcodeActivity
     */
    private void scanAgainQrCode() {

        mActivityScannedBarcodeBinding.btnScanAgainQrCode.setOnClickListener(view -> {
            Intent goAgainAndScanQRActivity = new Intent(this, ScannedBarcodeActivity.class);
            startActivity(goAgainAndScanQRActivity);
        });
    }

    /**
     * Method used to check if the headphones are on
     *
     * @return true if are on, false if are off
     */
    private boolean isWiredHeadsetOn() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);

        for (AudioDeviceInfo deviceInfo : audioDevices) {

            if (deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                    || deviceInfo.getType() == AudioDeviceInfo.TYPE_WIRED_HEADSET) {

                return true;
            }
        }
        return false;
    }

    /**
     * Method used for request permissions for open camera
     *
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *                     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                finish();
            } else {

                openCamera();
            }
        } else {
            finish();
        }
    }

    /**
     * Initialize the toolbar
     */
    public void setUpToolbar() {

        if (mActivityScannedBarcodeBinding != null) {

            setSupportActionBar(mActivityScannedBarcodeBinding.toolbar.toolbarMain);
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
        cameraSource.release();
    }

    /**
     * Called at the start of the active lifetime.
     * Registers again the broadcast receiver
     */
    @Override
    protected void onResume() {

        registerReceiver(mInternetBroadcastReceiver, sIntentFilter);
        initialiseDetectorsAndSources();
        super.onResume();
    }
}