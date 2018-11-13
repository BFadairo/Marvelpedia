package com.example.android.marvelpedia;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.marvelpedia.Database.CharacterDatabase;
import com.example.android.marvelpedia.Fragments.MasterList;
import com.example.android.marvelpedia.Fragments.TeamFragment;
import com.example.android.marvelpedia.Widget.MarvelAppWidget;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindString(R.string.master_tag)
    String master_tag;
    public static CharacterDatabase roomDatabase;
    private static FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        //Create the room Database
        roomDatabase = CharacterDatabase.getAppDatabase(this);

        //Setups and adds the masterList
        setupMasterList();

        setupFirebasePersistance();

        //Initialize Mobile Ads
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

    private void setupFirebasePersistance() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
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
        } else if (id == R.id.sign_out) {
            //Sign the user out
            mAuth.signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CharacterDatabase.destroyInstance();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        int[] ids = AppWidgetManager.getInstance(this)
                .getAppWidgetIds(new ComponentName(this, MarvelAppWidget.class));
        Intent intent = new Intent();
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(MarvelAppWidget.WIDGET_IDS_KEY, ids);
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        int mBackStackCount = getSupportFragmentManager().getBackStackEntryCount();
        //
        Log.v(LOG_TAG, mBackStackCount + "Size");
        if (mBackStackCount <= 1) {
            getSupportFragmentManager().popBackStack();
        }
        super.onBackPressed();
    }
}
