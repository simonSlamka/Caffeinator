/*
 * Copyright 2019 SMD Technologies, s.r.o. All rights reserved.
 */

package com.simtoonsoftware.caffeinator.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.simtoonsoftware.caffeinator.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Variables
    float caffeineIntakeValue;
    float caffeineIntakeLeft;

    int currentCaffeineLevel;
    int maxCaffeineIntake;
    int getPrg_maxCaffeine_currentValue;

    public static final String SAVE = "Caffeinator%Save%File";

    // Timers
    Timer autosave = new Timer();

    // UI data types
    TextView text_caffeineIntakeValue;
    TextView text_caffeineIntakeLeft;
    ProgressBar prg_maxCaffeine;
    private InterstitialAd RandomInterstitialAd;
    private AdView RandomBannerAd;

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


            //icon - cup of coffee HEX #FFA500 [do not remove]

                // Auto Save/Load section
                final SharedPreferences saveInstance = getSharedPreferences(SAVE, MODE_PRIVATE);
                final SharedPreferences.Editor save = saveInstance.edit();
                final SharedPreferences loadInstance = getSharedPreferences(SAVE, MODE_PRIVATE);

                caffeineIntakeValue = loadInstance.getFloat("caffeineIntakeValue", 0);


                // Ad section
                MobileAds.initialize(this, "ca-app-pub-9086446979210331~8508547502"); // Real AD ID
                //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); // Testing AD ID
                RandomInterstitialAd = new InterstitialAd(this);
                RandomInterstitialAd.setAdUnitId("ca-app-pub-9086446979210331/2057677460"); // Real AD ID
                //RandomInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Testing AD ID
                RandomInterstitialAd.loadAd(new AdRequest.Builder().build());
                RandomBannerAd = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                RandomBannerAd.loadAd(adRequest);

                // Data resources
                //prg_maxCaffeine = findViewById(R.id.prgBar_maxCaffeine);
                text_caffeineIntakeLeft = findViewById(R.id.text_caffeineIntakeLeft);
                text_caffeineIntakeValue = findViewById(R.id.text_caffeineIntakeValue);
                Button btn_addCaffeineIntake = findViewById(R.id.btn_addCaffeineIntake);
                maxCaffeineIntake = 400;
//                prg_maxCaffeine.setMax(maxCaffeineIntake);
//                prg_maxCaffeine.setProgress(currentCaffeineLevel); //we have to figure out how to calculate person's max daily caffeine intake and interpret it with this progressbar
//                getPrg_maxCaffeine_currentValue = prg_maxCaffeine.getProgress();

                // UI
                text_caffeineIntakeValue.setText(caffeineIntakeValue + "mg");
                currentCaffeineLevel = (int)caffeineIntakeValue;
                caffeineIntakeLeft = maxCaffeineIntake - caffeineIntakeValue;
                text_caffeineIntakeLeft.setText(caffeineIntakeLeft + "mg");

                btn_addCaffeineIntake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addCaffeineIntakeActivity = new Intent(com.simtoonsoftware.caffeinator.Activities.MainActivity.this, AddCaffeine.class);
                        addCaffeineIntakeActivity.putExtra("caffeineIntakeValue", caffeineIntakeValue);
                        startActivityForResult(addCaffeineIntakeActivity, SECOND_ACTIVITY_REQUEST_CODE);
                        if (RandomInterstitialAd.isLoaded()) {
                            RandomInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial ad hasn't been loaded yet");
                        }
                    }
                });

                autosave.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        save.putFloat("caffeineIntakeValue", caffeineIntakeValue);
                        save.apply();
                    }
                }, 2500, 2500);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //TODO navBar handling
        if (id == R.id.nav_home) {
//            Intent mainActivityIntent = new Intent(MainActivity.this, MainActivity.class);
            // Open main activity
        } else if (id == R.id.nav_stats) {
            // Open stats activity
        } else if (id == R.id.nav_graphs) {
            // Open graphs activity
            Intent graphsActivityIntent = new Intent(MainActivity.this, GraphActivity.class);
            startActivity(graphsActivityIntent);
        } else if (id == R.id.nav_settings) {
            // Open settings activity
            Intent settingsActivityIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsActivityIntent);
        } else if(id == R.id.nav_about) {
            // Open about activity
            Intent aboutActivityIntent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(aboutActivityIntent);
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    // This method is called when the second activity finishes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Get data from Intent
                caffeineIntakeValue = data.getFloatExtra("caffeineIntakeValue", caffeineIntakeValue);

                // Update the text view
                text_caffeineIntakeValue.setText(Float.toString(caffeineIntakeValue));

                // Update the progress bar
                caffeineIntakeLeft = 0;
                caffeineIntakeLeft = maxCaffeineIntake - caffeineIntakeValue;
                text_caffeineIntakeLeft.setText("Caffeine inatake: " + caffeineIntakeLeft + "mg");
            }
        }
    }
}
