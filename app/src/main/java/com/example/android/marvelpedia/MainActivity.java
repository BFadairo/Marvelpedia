package com.example.android.marvelpedia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.marvelpedia.Fragments.MasterList;
import com.example.android.marvelpedia.Fragments.TeamFragment;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindString(R.string.master_tag)
    String master_tag;
    private MasterList masterList;
    private TeamFragment teamFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Setups and adds the masterList
        setupMasterList();

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
        masterList = new MasterList();

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
        teamFragment = new TeamFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.master_list_container, teamFragment)
                .commit();
    }
}
