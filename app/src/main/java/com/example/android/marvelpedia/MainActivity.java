package com.example.android.marvelpedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.marvelpedia.Fragments.MasterList;
import com.example.android.marvelpedia.Fragments.TeamFragment;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindString(R.string.master_tag)
    String master_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Setups and adds the masterList
        setupMasterList();

        //Get the Firebase instance and enable persistence
        //This allows users to access their data offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //
        initializeAdMob();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search_button:
                        item.setChecked(true);
                        returnMasterListFragment();
                        Log.v(LOG_TAG, "Menu ID: " + item.getItemId());
                        break;
                    case R.id.team_button:
                        item.setChecked(true);
                        setupTeamFragment();
                        Log.v(LOG_TAG, "Menu ID: " + item.getItemId());
                        break;
                }
                return false;
            }
        });
    }

    private void setupMasterList() {
        MasterList masterList = new MasterList();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.master_list_container, masterList, master_tag)
                .addToBackStack(master_tag)
                .commit();
    }

    private void returnMasterListFragment() {
        Fragment restoredFragment;
        restoredFragment = getSupportFragmentManager().findFragmentByTag(master_tag);
        if (restoredFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.master_list_container, restoredFragment)
                    .commit();
        }
    }

    private void setupTeamFragment() {
        TeamFragment teamFragment = new TeamFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.master_list_container, teamFragment)
                .commit();
    }

    private void initializeAdMob() {
        MobileAds.initialize(this, "ca-app-pub-3369393916529285~9024148672");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get the MenuInflater and Inflate the Marvel Menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.marvel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //If the Settings button is hit in the Menu, Open the Settings Activity
        if (id == R.id.marvel_settings) {
            //Create a new Intent to start the SettingsActivity
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            //Start the settings Activity
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
