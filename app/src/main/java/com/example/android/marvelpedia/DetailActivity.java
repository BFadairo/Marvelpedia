package com.example.android.marvelpedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.marvelpedia.Fragments.DetailExtrasFragments;
import com.example.android.marvelpedia.Fragments.DetailFragment;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements DetailExtrasFragments.AddToDatabase, DetailExtrasFragments.SendComic {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private DetailFragment detailFragment;
    @BindView(R.id.floating_action_button_member_add)
    FloatingActionButton floatingActionButton;
    private static FirebaseDatabase firebaseDatabase;
    private DetailExtrasFragments<Event> eventDetailExtrasFragments;
    private DetailExtrasFragments<Comic> comicDetailExtrasFragments;
    private DetailExtrasFragments<Character> characterDetailExtrasFragments;
    private String comic_tag, event_tag, series_tag, character_tag;
    private List<Character> teamMembers = new ArrayList<>();
    private List<Integer> characterIds = new ArrayList<>();
    private String character_extras;
    private DetailExtrasFragments<Series> seriesDetailExtrasFragments;
    private DatabaseReference teamReference;
    private Intent extrasIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        teamMembers.clear();

        ButterKnife.bind(this);

        //Load a Test Ad
        setupAd();

        //get the firebase instance
        setupFirebaseInstance();
        //Retrieve the team members currently stored in the database
        retrieveTeamMembers();

        //Receive the extras that were passed through the intent
        //and put them into a bundle
        Bundle argsToPass = getIntent().getExtras();

        //Grab the passed character from the bundle
        Character retrievedCharacter = argsToPass.getParcelable(getResources().getString(R.string.character_extras));

        //Initial update to FAB Drawable
        updateFloatingActionButtonDrawable(retrievedCharacter);
        setupFloatingActionButton(retrievedCharacter);


        //Retrieve the string values for all required fields
        retrieveStrings();

        setupActivityTitle(argsToPass);

        setupComicTestFragment(argsToPass);
        setupCharacterTestFragment(argsToPass);
        setupEventTestFragment(argsToPass);
        setupSeriesTestFragment(argsToPass);
        setupDetailInfoFragment(argsToPass);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_information_container, detailFragment)
                .addToBackStack(null)
                .add(R.id.comic_container, comicDetailExtrasFragments, comic_tag)
                .addToBackStack(null)
                .add(R.id.event_container, eventDetailExtrasFragments, event_tag)
                .addToBackStack(null)
                .add(R.id.story_container, seriesDetailExtrasFragments, series_tag)
                .addToBackStack(null)
                .commit();
    }

    private void setupActivityTitle(Bundle arguments) {
        if (arguments.getParcelable(character_extras) != null) {
            Character passedCharacter = getIntent().getExtras().getParcelable("character_extras");
            setTitle(passedCharacter.getName());
        } else if (arguments.getParcelable("comic_extras") != null) {
            Comic passedComic = arguments.getParcelable("comic_extras");
            setTitle(passedComic.getTitle());
        }
    }

    private void setupComicTestFragment(Bundle passedArgs) {
        comicDetailExtrasFragments = new DetailExtrasFragments<>();
        comicDetailExtrasFragments.setArguments(passedArgs);
    }

    private void setupCharacterTestFragment(Bundle passedArgs) {
        characterDetailExtrasFragments = new DetailExtrasFragments<>();
        characterDetailExtrasFragments.setArguments(passedArgs);
    }

    private void setupEventTestFragment(Bundle passedArgs) {
        eventDetailExtrasFragments = new DetailExtrasFragments<>();
        eventDetailExtrasFragments.setArguments(passedArgs);
    }

    private void setupSeriesTestFragment(Bundle passedArgs) {
        seriesDetailExtrasFragments = new DetailExtrasFragments<>();
        seriesDetailExtrasFragments.setArguments(passedArgs);
    }

    private void setupDetailInfoFragment(Bundle passedArgs) {
        detailFragment = new DetailFragment();
        detailFragment.setArguments(passedArgs);
    }

    private void retrieveStrings() {
        //Retrieve the string values for the extras
        character_extras = getResources().getString(R.string.character_extras);
        String comic_extras = getResources().getString(R.string.comic_extras);
        String event_extras = getResources().getString(R.string.event_extras);

        //Retrieve the string values for the fragment tags
        character_tag = getResources().getString(R.string.character_tag);
        comic_tag = getResources().getString(R.string.comic_tag);
        event_tag = getResources().getString(R.string.event_tag);
        series_tag = getResources().getString(R.string.series_tag);
    }

    private void addOrRemoveTeamMember(Character character) {
        if (teamMembers.size() <= 5 && !(characterIds.contains(character.getId()))) {
            Log.v(LOG_TAG, "Does it contain:" + teamMembers.contains(character));
            Log.v(LOG_TAG, "Team Object: " + teamMembers);
            Log.v(LOG_TAG, teamMembers.size() + "Size");
            Log.v(LOG_TAG, "Character Id Size: " + characterIds.size());
            teamReference.child(character.getId().toString()).setValue(character);
            Toast.makeText(this, "Adding Character to Team", Toast.LENGTH_SHORT).show();
            updateFloatingActionButtonDrawable(character);
        } else if (characterIds.contains(character.getId())) {
            removeTeamMember(character);
        } else if (characterIds.contains(character.getId()) && teamMembers.size() == 5) {
            Toast.makeText(this, "Team is Full, Delete a Member", Toast.LENGTH_SHORT).show();
        }
        updateFloatingActionButtonDrawable(character);
        Log.v(LOG_TAG, "Writing to Database");
    }

    private void removeTeamMember(Character character) {
        Log.v(LOG_TAG, "Removing Character from Team");
        Toast.makeText(this, "Removing Character from Team", Toast.LENGTH_SHORT).show();
        teamReference.child(character.getId().toString()).removeValue();
        Log.v(LOG_TAG, "Deleting from Database");
    }

    private void retrieveTeamMembers() {
        teamReference = firebaseDatabase.getReference();
        teamReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamMembers.clear();
                characterIds.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Character currentCharacter = postSnapshot.getValue(Character.class);
                    teamMembers.add(currentCharacter);
                    characterIds.add(currentCharacter.getId());
                    updateFloatingActionButtonDrawable(currentCharacter);
                    Log.v(LOG_TAG, postSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void setupFirebaseInstance() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setupFloatingActionButton(final Character character) {
        updateFloatingActionButtonDrawable(character);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrRemoveTeamMember(character);
            }
        });
    }

    private void updateFloatingActionButtonDrawable(Character character) {
        if (characterIds.contains(character.getId())) {
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_black_24dp));
        } else {
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
        }
    }

    @Override
    public void addToDb(Character character) {
        setupFloatingActionButton(character);
    }

    private void setupAd() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "On Pause Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "On Stop Called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "On SavedInstance called");
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

    @Override
    public void sendComicDetails(Comic comic, ImageView transitionView) {
        Bundle passedArgs = new Bundle();
        passedArgs.putParcelable("comic_extras", comic);
        setupDetailInfoFragment(passedArgs);
        setupEventTestFragment(passedArgs);
        setupCharacterTestFragment(passedArgs);
        setupActivityTitle(passedArgs);
        passedArgs.putString("character_transition", ViewCompat.getTransitionName(transitionView));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_information_container, detailFragment)
                .addToBackStack(null)
                .replace(R.id.comic_container, characterDetailExtrasFragments, character_tag)
                .addToBackStack(null)
                .replace(R.id.event_container, eventDetailExtrasFragments, event_tag)
                .addToBackStack(null)
                .remove(seriesDetailExtrasFragments)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get the MenuInflater and Inflate the Marvel Menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //If the Settings button is hit in the Menu, Open the Settings Activity
        if (id == R.id.refresh_button) {
            //Create a new Intent to start the SettingsActivity
            //Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            //Start the settings Activity
            Log.v(LOG_TAG, "Refresh Hit");
            //startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
