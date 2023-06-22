package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.licenta.utils.Constants;

/**
 * Activity displayed before Main Activity
 */
public class SplashActivity extends AppCompatActivity {

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

        DataBindingUtil.setContentView(this, R.layout.activity_splash);

        // New Handler to start the Menu-Activity and close this Splash-Screen after some seconds.
        Handler handler = new Handler();
        handler.postDelayed(this::goToMainActivity, Constants.SPLASH_DELAY);
    }

    /**
     * Create an Intent that will start the Main-Activity.
     */
    private void goToMainActivity() {
        Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);

        startActivity(mainActivityIntent);
        finish();
    }
}