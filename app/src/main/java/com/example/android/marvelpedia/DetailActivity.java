package com.example.android.marvelpedia;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class DetailActivity extends AppCompatActivity implements DetailExtrasFragments.AddToDatabase {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private DetailFragment detailFragment;
    @BindView(R.id.floating_action_button_member_add)
    FloatingActionButton floatingActionButton;
    private static FirebaseDatabase firebaseDatabase;
    private Character retrievedCharacter;
    private DetailExtrasFragments<Character> characterDetailExtrasFragments;
    private DetailExtrasFragments<Event> eventDetailExtrasFragments;
    private DetailExtrasFragments<Comic> comicDetailExtrasFragments;
    private String comic_tag, event_tag, series_tag;
    private String character_extras, comic_extras, event_extras;
    private List<Character> teamMembers = new ArrayList<>();
    private DetailExtrasFragments<Series> seriesDetailExtrasFragments;
    private DatabaseReference teamReference;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        teamMembers.clear();

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

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
        retrievedCharacter = argsToPass.getParcelable(getResources().getString(R.string.character_extras));


        //Retrieve the string values for all required fields
        retrieveStrings();

        setupActivityTitle();

        setupComicTestFragment(argsToPass);
        setupCharacterTestFragment(argsToPass);
        setupEventTestFragment(argsToPass);
        setupSeriesTestFragment(argsToPass);
        setupDetailInfoFragment(argsToPass);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_information_container, detailFragment)
                .add(R.id.comic_container, comicDetailExtrasFragments, comic_tag)
                .add(R.id.event_container, eventDetailExtrasFragments, event_tag)
                .add(R.id.story_container, seriesDetailExtrasFragments, series_tag)
                .commit();
    }

    private void setupActivityTitle() {
        if (getIntent().getParcelableExtra(character_extras) != null) {
            Character passedCharacter = getIntent().getExtras().getParcelable("character_extras");
            setTitle(passedCharacter.getName());
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
        comic_extras = getResources().getString(R.string.comic_extras);
        event_extras = getResources().getString(R.string.event_extras);

        //Retrieve the string values for the fragment tags
        comic_tag = getResources().getString(R.string.comic_tag);
        event_tag = getResources().getString(R.string.event_tag);
        series_tag = getResources().getString(R.string.series_tag);
    }

    private void addTeamMember(Character character) {
        if (teamMembers.size() <= 5) {
            Log.v(LOG_TAG, teamMembers.size() + "Size");
            teamReference.child(character.getId().toString()).setValue(character);
            Toast.makeText(this, "Adding Character to Team", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < teamMembers.size(); i++) {
                if (character.getId().equals(teamMembers.get(i).getId())) {
                    //teamMembers.remove(i);
                    teamReference.child(character.getId().toString()).removeValue();
                    Toast.makeText(this, "Removing Character from Team", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Already have 5 Team Members, Delete One", Toast.LENGTH_SHORT).show();
        }
        Log.v(LOG_TAG, "Writing to Database");
    }

    private void retrieveTeamMembers() {
        teamReference = firebaseDatabase.getReference();
        teamReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamMembers.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Character currentCharacter = postSnapshot.getValue(Character.class);
                    teamMembers.add(currentCharacter);
                    //updateFloatingActionButtonDrawable(currentCharacter);
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
                addTeamMember(character);
            }
        });
    }

    private void updateFloatingActionButtonDrawable(Character character) {
        for (int i = 0; i < teamMembers.size(); i++) {
            if (character.getId().equals(teamMembers.get(i).getId())) {
                floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_black_24dp));
            } else {
                floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
            }
        }
    }

    @Override
    public void addToDb(Character character) {
        setupFloatingActionButton(character);
    }

    private void setupAd() {
        mAdView = findViewById(R.id.adView);
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
}
