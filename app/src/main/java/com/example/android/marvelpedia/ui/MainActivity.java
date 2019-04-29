package com.example.android.marvelpedia.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.android.marvelpedia.viewmodel.HeroViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.marvelpedia.data.Database.CharacterDatabase;
import com.example.android.marvelpedia.ui.Fragments.MasterListFragment;
import com.example.android.marvelpedia.ui.Fragments.TeamFragment;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Widget.MarvelAppWidget;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindString(R.string.master_tag)
    String master_tag;
    public static Boolean isConnected;
    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    public static CharacterDatabase roomDatabase;
    private static FirebaseDatabase mDatabase;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private FirebaseAuth mAuth;
    private HeroViewModel heroViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkNetworkStatus();
        heroViewModel = ViewModelProviders.of(this).get(HeroViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        //Create the room Database
        roomDatabase = CharacterDatabase.getAppDatabase(this);

        //Setups and adds the masterList
        //setupMasterList();

        enableFireBasePersistence();

        //Initialize Mobile Ads
        initializeAdMob();
        setupViewPager();

        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed. Please note that we must unregister MainActivity as an
         * OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
         */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    private void setupViewPager() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //@BindView(R.id.navigation)
        //BottomNavigationView navigation;
        SectionsPageAdapter mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    private void enableFireBasePersistence() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
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
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
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

    private void checkNetworkStatus() {
        //Check the current internet connection of the phone
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
