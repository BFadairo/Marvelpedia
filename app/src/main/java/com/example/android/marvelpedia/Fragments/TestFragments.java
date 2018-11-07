package com.example.android.marvelpedia.Fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.marvelpedia.Adapters.TestAdapter;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Utils.Network.CharacterHelper;
import com.example.android.marvelpedia.Utils.Network.ComicHelper;
import com.example.android.marvelpedia.Utils.Network.EventHelper;
import com.example.android.marvelpedia.Utils.Network.SeriesHelper;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;

import java.util.ArrayList;
import java.util.List;

public class TestFragments<T> extends Fragment implements TestAdapter.ItemOnClick, CharacterHelper.SendCharacterData,
        ComicHelper.SendComicData, EventHelper.SendEventData, SeriesHelper.SendSeriesData {

    private final static String LOG_TAG = TestFragments.class.getSimpleName();
    private String CHARACTER_EXTRAS, COMIC_EXTRAS, EVENT_EXTRAS, SERIES_EXTRAS, CHARACTER_TAG, COMIC_TAG,
            EVENT_TAG, SERIES_TAG;
    private RecyclerView itemRecyclerView;
    private Parcelable retrievedItem;
    private TestAdapter<Comic> mComicAdapter;
    private TestAdapter<Event> mEventAdapter;
    private TestAdapter<Series> mSeriesAdapter;
    private TestAdapter<Character> mCharacterAdapter;
    private TextView itemDescription, itemDetailHeader;
    private RecyclerView.LayoutManager layoutManager;
    private List<Comic> mComics = new ArrayList<>();
    private List<Character> mCharacters = new ArrayList<>();
    private List<Event> mEvents = new ArrayList<>();
    private List<Series> mSeries = new ArrayList<>();
    private CharacterHelper characterHelper;
    private ComicHelper comicHelper;
    private EventHelper eventHelper;
    private SeriesHelper seriesHelper;
    private FragmentManager fragmentManager;

    public TestFragments() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_test_list, container, false);

        retrieveStrings();
        //Setup the helpers for each class (contain the API calls)
        characterHelper = new CharacterHelper(this);
        comicHelper = new ComicHelper(this);
        eventHelper = new EventHelper(this);
        seriesHelper = new SeriesHelper(this);

        //Get the reference to the item's recycler view
        itemRecyclerView = rootView.findViewById(R.id.test_recycler_view);
        itemDetailHeader = rootView.findViewById(R.id.test_item_header);

        //Retrieve the passed arguments from the parent activity
        Bundle passedArgs = getArguments();

        if (passedArgs.getParcelable(CHARACTER_EXTRAS) != null) {
            retrievedItem = passedArgs.getParcelable(CHARACTER_EXTRAS);
        } else if (passedArgs.getParcelable(COMIC_EXTRAS) != null) {
            retrievedItem = passedArgs.getParcelable(COMIC_EXTRAS);
        } else if (passedArgs.getParcelable(EVENT_EXTRAS) != null) {
            retrievedItem = passedArgs.getParcelable(EVENT_EXTRAS);
        } else if (passedArgs.getParcelable(SERIES_EXTRAS) != null) {
            retrievedItem = passedArgs.getParcelable(SERIES_EXTRAS);
        }

        Log.v(LOG_TAG, (retrievedItem instanceof Character) + "Char");
        Log.v(LOG_TAG, (retrievedItem instanceof Comic) + "Comic");
        Log.v(LOG_TAG, (retrievedItem instanceof Event) + "Event");
        Log.v(LOG_TAG, (retrievedItem instanceof Series) + "Series");

        //Initialize the fragment manager
        initializeFragmentManager();

        if (passedArgs != null) {
            if (passedArgs.getParcelable(CHARACTER_EXTRAS) != null) {
                if (retrievedItem instanceof Character) {
                    Log.v(LOG_TAG, "Character Is Object");
                    Log.v(LOG_TAG, this.getTag());
                    populateUi();

                    switch (this.getTag()) {
                        case "Comic":
                            characterHelper.retrieveCharacterComics((Character) retrievedItem);
                            itemDetailHeader.setText(R.string.comic_header);
                            break;
                        case "Event":
                            characterHelper.retrieveCharacterEvents((Character) retrievedItem);
                            itemDetailHeader.setText(R.string.event_header);
                            break;
                        case "Story":
                            itemDetailHeader.setText(R.string.story_header);
                            break;
                        case "Series":
                            characterHelper.retrieveCharacterSeries((Character) retrievedItem);
                            itemDetailHeader.setText(R.string.series_header);
                            break;
                    }
                    //helperMethods.retrieveCharacterEvents((Character) retrievedItem);
                }
            } else if (passedArgs.getParcelable(COMIC_EXTRAS) != null) {
                if (retrievedItem instanceof Comic) {
                    Log.v(LOG_TAG, "Comic Is Object");
                    populateUi();
                    switch (this.getTag()) {
                        case "Character":
                            comicHelper.retrieveComicCharacters((Comic) retrievedItem);
                            itemDetailHeader.setText(R.string.character_header);
                            break;
                        case "Event":
                            comicHelper.retrieveComicEvents((Comic) retrievedItem);
                            itemDetailHeader.setText(R.string.event_header);
                            break;
                        case "Series":
                            comicHelper.retrieveComicSeries((Comic) retrievedItem);
                            itemDetailHeader.setText(R.string.series_header);
                            break;
                    }
                }
            } else if (passedArgs.getParcelable(EVENT_EXTRAS) != null) {
                if (retrievedItem instanceof Event) {
                    Log.v(LOG_TAG, "Event Is Object");
                    Event currentEvent = (Event) retrievedItem;
                    populateUi();
                    switch (this.getTag()) {
                        case "Character":
                            eventHelper.retrieveEventCharacters(currentEvent);
                            itemDetailHeader.setText(R.string.character_header);
                            break;
                        case "Comic":
                            eventHelper.retrieveEventComics(currentEvent);
                            itemDetailHeader.setText(R.string.comic_header);
                            break;
                        case "Series":
                            eventHelper.retrieveEventSeries(currentEvent);
                            itemDetailHeader.setText(R.string.series_header);
                            break;
                    }
                }
            } else if (passedArgs.getParcelable(SERIES_EXTRAS) != null) {
                if (retrievedItem instanceof Series) {
                    Series currentSeries = (Series) retrievedItem;
                    Log.v(LOG_TAG, "Series is Object");
                    populateUi();
                    switch (this.getTag()) {
                        case "Character":
                            seriesHelper.retrieveSeriesCharacters(currentSeries);
                            itemDetailHeader.setText(R.string.character_header);
                            break;
                        case "Comic":
                            seriesHelper.retrieveSeriesComics(currentSeries);
                            itemDetailHeader.setText(R.string.comic_header);
                            break;
                        case "Event":
                            seriesHelper.retrieveSeriesEvents(currentSeries);
                            itemDetailHeader.setText(R.string.event_header);
                            break;
                    }
                }
            }
        }

        //return the rootView
        return rootView;
    }

    private void populateUi() {
        //Create a new adapter depending on the fragment tag
        if (this.getTag().equals(COMIC_TAG)) {
            //Create a new comic Adapter
            // This adapter takes in an empty list of items as well as a context
            mComicAdapter = new TestAdapter<>(getContext(), mComics, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mComicAdapter);
        } else if (this.getTag().equals(EVENT_TAG)) {
            //Create a new event Adapter
            // This adapter takes in an empty list of items as well as a context
            mEventAdapter = new TestAdapter<>(getContext(), mEvents, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mEventAdapter);
        } else if (this.getTag().equals(SERIES_TAG)) {
            //Create a new series Adapter
            // This adapter takes in an empty list of series items as well as a context
            mSeriesAdapter = new TestAdapter<>(getContext(), mSeries, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mSeriesAdapter);
        } else if (this.getTag().equals(CHARACTER_TAG)) {
            //Create a new Character Adapter
            mCharacterAdapter = new TestAdapter<>(getContext(), mCharacters, this);

            //Set the adapter on the RecylcerView
            itemRecyclerView.setAdapter(mCharacterAdapter);
        }

        //Create a horizontal Linear Layout manager
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        itemRecyclerView.setLayoutManager(layoutManager);
    }

    private void initializeFragmentManager() {
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onClick(Object item) {
        Bundle argsToPass = new Bundle();
        if (item instanceof Comic) {
            TestFragments<Character> newCharacterFragment = new TestFragments<>();
            TestFragments<Event> newEventFragment = new TestFragments<>();
            TestFragments<Series> newSeriesFragment = new TestFragments<>();
            DetailFragment newDetailFragment = new DetailFragment();
            Log.v(LOG_TAG, ((Comic) item).getTitle());
            argsToPass.putParcelable(COMIC_EXTRAS, (Comic) item);
            newCharacterFragment.setArguments(argsToPass);
            newDetailFragment.setArguments(argsToPass);
            newEventFragment.setArguments(argsToPass);
            newSeriesFragment.setArguments(argsToPass);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction
                    .replace(R.id.detail_information_container, newDetailFragment)
                    .replace(R.id.comic_container, newCharacterFragment, CHARACTER_TAG)
                    .replace(R.id.event_container, newEventFragment, EVENT_TAG)
                    .replace(R.id.story_container, newSeriesFragment, SERIES_TAG)
                    //.addSharedElement(getActivity().findViewById(R.id.image_detail), getString(R.string.image_transition))
                    .commit();
        } else if (item instanceof Event) {
            Log.v(LOG_TAG, ((Event) item).getEventTitle());
        } else if (item instanceof Series) {
            Log.v(LOG_TAG, ((Series) item).getSeriesTitle());
        } else if (item instanceof Character) {
            Log.v(LOG_TAG, ((Character) item).getName());
        }
    }

    private void retrieveStrings() {
        //Retrieve the strings for the extras
        CHARACTER_EXTRAS = getResources().getString(R.string.character_extras);
        COMIC_EXTRAS = getResources().getString(R.string.comic_extras);
        EVENT_EXTRAS = getResources().getString(R.string.event_extras);
        SERIES_EXTRAS = getResources().getString(R.string.series_extras);
        //Retrieve the strings for the fragments tags
        CHARACTER_TAG = getResources().getString(R.string.character_tag);
        COMIC_TAG = getResources().getString(R.string.comic_tag);
        EVENT_TAG = getResources().getString(R.string.event_tag);
        SERIES_TAG = getResources().getString(R.string.series_tag);
    }

    @Override
    public List<Character> sendComicCharacters(List<Character> characters) {
        mCharacterAdapter.setItemData(characters);
        return characters;
    }

    @Override
    public List<Character> sendEventCharacters(List<Character> characters) {
        mCharacterAdapter.setItemData(characters);
        return characters;
    }

    @Override
    public List<Character> sendSeriesCharacters(List<Character> characters) {
        mCharacterAdapter.setItemData(characters);
        return characters;
    }

    @Override
    public List<Event> sendCharacterEvents(List<Event> events) {
        mEventAdapter.setItemData(events);
        return events;
    }

    @Override
    public List<Event> sendComicEvents(List<Event> events) {
        mEventAdapter.setItemData(events);
        return events;
    }

    @Override
    public List<Event> sendSeriesEvents(List<Event> events) {
        mEventAdapter.setItemData(events);
        return events;
    }


    @Override
    public List<Comic> sendCharacterComics(List<Comic> comics) {
        mComicAdapter.setItemData(comics);
        return comics;
    }

    @Override
    public List<Comic> sendEventComics(List<Comic> comics) {
        mComicAdapter.setItemData(comics);
        return comics;
    }

    @Override
    public List<Comic> sendSeriesComics(List<Comic> comics) {
        mComicAdapter.setItemData(comics);
        return comics;
    }


    @Override
    public List<Series> sendEventSeries(List<Series> series) {
        mSeriesAdapter.setItemData(series);
        return series;
    }

    @Override
    public List<Series> sendComicSeries(List<Series> series) {
        mSeriesAdapter.setItemData(series);
        return series;
    }

    @Override
    public List<Series> sendCharacterSeries(List<Series> series) {
        mSeriesAdapter.setItemData(series);
        return series;
    }
}
