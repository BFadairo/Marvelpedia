package com.example.android.marvelpedia;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.marvelpedia.Fragments.DetailFragment;
import com.example.android.marvelpedia.Fragments.TestFragments;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements TestFragments.AddToDatabase {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private DetailFragment detailFragment;
    @BindView(R.id.floating_action_button_member_add)
    FloatingActionButton floatingActionButton;
    private TestFragments<Character> characterTestFragments;
    private TestFragments<Event> eventTestFragments;
    private TestFragments<Comic> comicTestFragments;
    private TestFragments<Series> seriesTestFragments;
    private String comic_tag, event_tag, series_tag;
    private String character_extras, comic_extras, event_extras;
    private List<Character> teamMembers = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference teamReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        teamMembers.clear();

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        retrieveTeamMembers();


        //Receive the extras that were passed through the intent
        //and put them into a bundle
        Bundle argsToPass = getIntent().getExtras();

        if (argsToPass.getParcelable("character_extras") != null) {
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.GONE);
        }

        //Retrieve the string values for all required fields
        retrieveStrings();

        setupActivityTitle();

        setupComicTestFragment(argsToPass);
        setupCharacterTestFragment(argsToPass);
        setupEventTestFragment(argsToPass);
        setupSeriesTestFragment(argsToPass);


        //Create a new Detail Fragment
        detailFragment = new DetailFragment();
        //Set the arguments for the detail fragment
        detailFragment.setArguments(argsToPass);

        /*
        //Create a new Comic Fragment
        comicFragment = new ComicFragment();
        //Set the arguments for the comic fragment
        comicFragment.setArguments(argsToPass);

        //Create a new Event Fragment
        eventFragment = new EventFragment();
        //Set the arguments for the event fragment
        eventFragment.setArguments(argsToPass);

        /*getSupportFragmentManager().beginTransaction()
                .add(R.id.comic_container, comicFragment, "Comic")
                .commit();*/

                /*
        getSupportFragmentManager().beginTransaction()
                .add(R.id.event_container, eventFragment, "Event")
                .commit();
                */


        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_information_container, detailFragment)
                .add(R.id.comic_container, comicTestFragments, comic_tag)
                .add(R.id.event_container, eventTestFragments, event_tag)
                .add(R.id.story_container, seriesTestFragments, series_tag)
                .commit();
    }

    private void setupActivityTitle() {
        if (getIntent().getParcelableExtra(character_extras) != null) {
            Character passedCharacter = getIntent().getExtras().getParcelable("character_extras");
            setTitle(passedCharacter.getName());
        }
    }

    private void setupComicTestFragment(Bundle passedArgs) {
        comicTestFragments = new TestFragments<>();
        comicTestFragments.setArguments(passedArgs);
    }

    private void setupCharacterTestFragment(Bundle passedArgs) {
        characterTestFragments = new TestFragments<>();
        characterTestFragments.setArguments(passedArgs);
    }

    private void setupEventTestFragment(Bundle passedArgs) {
        eventTestFragments = new TestFragments<>();
        eventTestFragments.setArguments(passedArgs);
    }

    private void setupSeriesTestFragment(Bundle passedArgs) {
        seriesTestFragments = new TestFragments<>();
        seriesTestFragments.setArguments(passedArgs);
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
            for (int i = 0; i < teamMembers.size(); i++) {
                if (character.getId().equals(teamMembers.get(i).getId())) {
                    //teamMembers.remove(i);
                    teamReference.child(character.getId().toString()).removeValue();
                    Toast.makeText(this, "Removing Character from Team", Toast.LENGTH_SHORT).show();
                } else {
                    //teamMembers.add(character);
                    Toast.makeText(this, "Adding Character to Team", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Already have 5 Team Members, Delete One", Toast.LENGTH_SHORT).show();
        }
        updateFloatingActionButtonDrawable(character);
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
        setupFirebaseInstance();
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
