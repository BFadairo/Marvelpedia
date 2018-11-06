package com.example.android.marvelpedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.marvelpedia.Fragments.CharacterFragment;
import com.example.android.marvelpedia.Fragments.ComicFragment;
import com.example.android.marvelpedia.Fragments.DetailFragment;
import com.example.android.marvelpedia.Fragments.EventFragment;
import com.example.android.marvelpedia.Fragments.TestFragments;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;

public class DetailActivity extends AppCompatActivity implements ComicFragment.SendComicData {

    private ComicFragment comicFragment;
    private DetailFragment detailFragment;
    public String comic_tag, event_tag, series_tag;
    private CharacterFragment characterFragment;
    private EventFragment eventFragment;
    private TestFragments<Character> characterTestFragments;
    private TestFragments<Event> eventTestFragments;
    private TestFragments<Comic> comicTestFragments;
    private TestFragments<Series> seriesTestFragments;
    private String character_extras, comic_extras, event_extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Receive the extras that were passed through the intent
        //and put them into a bundle
        Bundle argsToPass = getIntent().getExtras();

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

    @Override
    public void sendComic(Comic comic) {
        Bundle bundle = new Bundle();
        //TODO Add string for this
        bundle.putParcelable(comic_extras, comic);
        detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);

        characterFragment = new CharacterFragment();
        characterFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.comic_container, characterFragment)
                .replace(R.id.detail_information_container, detailFragment)
                .commit();
        setTitle(comic.getTitle());
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
}
