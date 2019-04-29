package com.example.android.marvelpedia.ui;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.marvelpedia.ui.Fragments.DetailExtrasFragments;
import com.example.android.marvelpedia.ui.Fragments.DetailFragment;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.marvelpedia.ui.MainActivity.roomDatabase;

public class DetailActivity extends AppCompatActivity implements DetailExtrasFragments.AddToDatabase, DetailExtrasFragments.SendComic,
        DetailExtrasFragments.SendCharacter {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private DetailFragment detailFragment;
    @BindView(R.id.floating_action_button_member_add)
    FloatingActionButton floatingActionButton;
    private static FirebaseDatabase firebaseDatabase;
    private DetailExtrasFragments<Event> eventDetailExtrasFragments;
    private DetailExtrasFragments<Comic> comicDetailExtrasFragments;
    private DetailExtrasFragments<Character> characterDetailExtrasFragments;
    private String comic_tag, event_tag, series_tag, character_tag;
    private static final List<Character> teamMembers = new ArrayList<>();
    private static DatabaseReference teamReference;
    private DetailExtrasFragments<Series> seriesDetailExtrasFragments;
    private static Boolean isMember;
    private static String userId;
    private static Character retrievedCharacter;
    private String character_extras;
    private String comic_extras;
    @BindString(R.string.character_transition)
    String character_transition;
    @BindString(R.string.comic_transition)
    String comic_transition;
    @BindString(R.string.users_path)
    String userPath;
    @BindString(R.string.team_child)
    String teamPath;

    private static void retrieveTeamMembers() {
        teamReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamMembers.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.v(LOG_TAG, postSnapshot.getKey());
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

    private void setupActivityTitle(Object item) {
        if (item instanceof Character) {
            this.setTitle(((Character) item).getName());
        } else if (item instanceof Comic) {
            this.setTitle(((Comic) item).getTitle());
        } else if (item instanceof Event) {
            this.setTitle(((Event) item).getEventTitle());
        }
    }

    private static void addTeamMember(Character character) {
        teamReference.child(character.getName()).setValue(character);
    }

    private static void removeTeamMember(Character character) {
        Log.v(LOG_TAG, "Removing Character from Team");
        teamReference.child(character.getName()).removeValue();
        Log.v(LOG_TAG, "Deleting from Database");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        teamMembers.clear();

        ButterKnife.bind(this);

        //Check if the user is logged in
        checkIfUserLoggedIn();

        //Load a Test Ad
        setupAd();

        //get the FireBase instance
        setupFirebaseInstance();

        //Receive the extras that were passed through the intent
        //and put them into a bundle
        Bundle argsToPass = getIntent().getExtras();

        //Grab the passed character from the bundle
        retrievedCharacter = argsToPass.getParcelable(getResources().getString(R.string.character_extras));

        //Get the reference to the database
        getTeamReference();

        new MemberCheck() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                updateFloatingActionButtonDrawable();
            }
        }.execute(retrievedCharacter);


        //Setup the floating action button
        setupFloatingActionButton();

        //Retrieve the string values for all required fields
        retrieveStrings();

        setupActivityTitle(retrievedCharacter);

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
                .addToBackStack(null)
                .commit();
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

    private void setupFirebaseInstance() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void retrieveStrings() {
        //Retrieve the string values for the extras
        character_extras = getResources().getString(R.string.character_extras);
        comic_extras = getResources().getString(R.string.comic_extras);
        String event_extras = getResources().getString(R.string.event_extras);

        //Retrieve the string values for the fragment tags
        character_tag = getResources().getString(R.string.character_tag);
        comic_tag = getResources().getString(R.string.comic_tag);
        event_tag = getResources().getString(R.string.event_tag);
        series_tag = getResources().getString(R.string.series_tag);
    }

    private void checkIfUserLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //Retrieve the user's email address
            userId = user.getUid();
        }
    }

    private void setupFloatingActionButton() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addOrRemoveTeamMember(character);
                new DatabaseLoader().execute();
                if (isMember) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
                    Toast.makeText(getApplicationContext(), "Removing Character from Team", Toast.LENGTH_SHORT).show();
                    isMember = false;
                } else {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_black_24dp));
                    Toast.makeText(getApplicationContext(), "Adding Character to Team", Toast.LENGTH_SHORT).show();
                    isMember = true;
                }
            }
        });
    }

    private void hideFabIfNotCharacter(Character character) {
        if (character != null) {
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.GONE);
        }
    }

    private void setupAd() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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

    private void updateFloatingActionButtonDrawable() {
        if (isMember) {
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_black_24dp));
        } else {
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
        }
    }

    @Override
    public void addToDb(Character character) {
        setupFloatingActionButton();
    }

    @Override
    public void sendComicDetails(Comic comic, ImageView transitionView) {
        Bundle passedArgs = new Bundle();
        passedArgs.putParcelable(comic_extras, comic);
        setupDetailInfoFragment(passedArgs);
        setupEventTestFragment(passedArgs);
        setupCharacterTestFragment(passedArgs);
        setupActivityTitle(comic);
        hideFabIfNotCharacter(null);
        passedArgs.putString(comic_transition, ViewCompat.getTransitionName(transitionView));
        Toast.makeText(this, comic.getTitle(), Toast.LENGTH_SHORT).show();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_information_container, detailFragment)
                .replace(R.id.comic_container, characterDetailExtrasFragments, character_tag)
                .replace(R.id.event_container, eventDetailExtrasFragments, event_tag)
                .remove(seriesDetailExtrasFragments)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void sendCharacterDetails(Character character, ImageView transitionView) {
        Bundle passedArgs = new Bundle();
        passedArgs.putParcelable(character_extras, character);
        setupDetailInfoFragment(passedArgs);
        setupEventTestFragment(passedArgs);
        setupComicTestFragment(passedArgs);
        setupActivityTitle(character);
        hideFabIfNotCharacter(character);
        passedArgs.putString(character_transition, ViewCompat.getTransitionName(transitionView));
        Toast.makeText(this, character.getName(), Toast.LENGTH_SHORT).show();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_information_container, detailFragment)
                .replace(R.id.comic_container, comicDetailExtrasFragments, comic_tag)
                .replace(R.id.event_container, eventDetailExtrasFragments, event_tag)
                .add(R.id.story_container, seriesDetailExtrasFragments, series_tag)
                .addToBackStack(null)
                .commit();
    }

    private void getTeamReference() {
        DatabaseReference root = firebaseDatabase.getReference();
        teamReference = root.child(userPath).child(userId).child(teamPath);
        teamReference.keepSynced(true);
    }

    private static class DatabaseLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (isMember) {
                Log.v(LOG_TAG, "Character is Team Member: Deleted");
                roomDatabase.characterDao().deleteMember(retrievedCharacter);
                removeTeamMember(retrievedCharacter);
                for (int i = 0; i < roomDatabase.characterDao().getAllMembers().size(); i++) {
                    Log.v(LOG_TAG, "Member: " + roomDatabase.characterDao().getAllMembers().get(i).getName());
                }
            } else {
                Log.v(LOG_TAG, "Character is not a Member: Added");
                roomDatabase.characterDao().insertTeamMember(retrievedCharacter);
                addTeamMember(retrievedCharacter);
                for (int i = 0; i < roomDatabase.characterDao().getAllMembers().size(); i++) {
                    Log.v(LOG_TAG, "Member: " + roomDatabase.characterDao().getAllMembers().get(i).getName());
                }
            }
            return null;
        }
    }

    private static class MemberCheck extends AsyncTask<Character, Void, Boolean> {

        private static final String LOG_TAG = MemberCheck.class.getSimpleName();

        @Override
        protected Boolean doInBackground(Character... characters) {
            //retrieveTeamMembers();
            isMember = roomDatabase.characterDao().fetchCharacterById(characters[0].getId()) != null;
            Log.v(LOG_TAG, "Member: " + isMember);
            return isMember;
        }
    }
}
