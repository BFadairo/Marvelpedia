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
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;

import java.util.ArrayList;
import java.util.List;

public class TestFragments<T> extends Fragment implements TestAdapter.ItemOnClick, CharacterHelper.SendCharacterData, ComicHelper.SendComicData {

    private final static String LOG_TAG = TestFragments.class.getSimpleName();
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
    private FragmentManager fragmentManager;

    public TestFragments() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_test_list, container, false);

        characterHelper = new CharacterHelper(this);
        comicHelper = new ComicHelper(this);

        //Get the reference to the item's recycler view
        itemRecyclerView = rootView.findViewById(R.id.test_recycler_view);
        itemDetailHeader = rootView.findViewById(R.id.test_item_header);

        //Retrieve the passed arguments from the parent activity
        Bundle passedArgs = getArguments();

        if (passedArgs.getParcelable("character_extras") != null) {
            retrievedItem = passedArgs.getParcelable("character_extras");
        } else if (passedArgs.getParcelable("comic_extras") != null) {
            retrievedItem = passedArgs.getParcelable("comic_extras");
        } else if (passedArgs.getParcelable("event_extras") != null) {
            retrievedItem = passedArgs.getParcelable("event_extras");
        }

        Log.v(LOG_TAG, (retrievedItem instanceof Character) + "Char");
        Log.v(LOG_TAG, (retrievedItem instanceof Comic) + "Comic");
        Log.v(LOG_TAG, (retrievedItem instanceof Event) + "Event");

        //Initialize the fragment manager
        initializeFragmentManager();

        if (passedArgs.getParcelable("character_extras") != null) {
            if (retrievedItem instanceof Character) {
                retrievedItem = passedArgs.getParcelable("character_extras");
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
        } else if (passedArgs.getParcelable("comic_extras") != null) {
            if (retrievedItem instanceof Comic) {
                retrievedItem = passedArgs.getParcelable("comic_extras");
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
        } else if (passedArgs.getParcelable("event_extras") != null) {
            if (retrievedItem instanceof Event) {
                Log.v(LOG_TAG, "Event Is Object");
                retrievedItem = passedArgs.getParcelable("event_extras");
            }
        } else if (passedArgs.getParcelable("series_extras") != null) {
            if (retrievedItem instanceof Series) {

            }
        }

        //return the rootView
        return rootView;
    }

    private void populateUi() {
        //Create a new adapter depending on the fragment tag
        if (this.getTag().equals("Comic")) {
            //Create a new comic Adapter
            // This adapter takes in an empty list of items as well as a context
            mComicAdapter = new TestAdapter<>(getContext(), mComics, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mComicAdapter);
        } else if (this.getTag().equals("Event")) {
            //Create a new event Adapter
            // This adapter takes in an empty list of items as well as a context
            mEventAdapter = new TestAdapter<>(getContext(), mEvents, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mEventAdapter);
        } else if (this.getTag().equals("Series")) {
            //Create a new series Adapter
            // This adapter takes in an empty list of series items as well as a context
            mSeriesAdapter = new TestAdapter<>(getContext(), mSeries, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mSeriesAdapter);
        } else if (this.getTag().equals("Character")) {
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
            argsToPass.putParcelable("comic_extras", (Comic) item);
            newCharacterFragment.setArguments(argsToPass);
            newDetailFragment.setArguments(argsToPass);
            newEventFragment.setArguments(argsToPass);
            newSeriesFragment.setArguments(argsToPass);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction
                    .replace(R.id.detail_information_container, newDetailFragment)
                    .replace(R.id.comic_container, newCharacterFragment, "Character")
                    .replace(R.id.event_container, newEventFragment, "Event")
                    .replace(R.id.story_container, newSeriesFragment, "Series")
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

    @Override
    public List<Series> sendSeries(List<Series> series) {
        mSeriesAdapter.setItemData(series);
        return series;
    }

    @Override
    public List<Comic> sendComics(List<Comic> comics) {
        mComicAdapter.setItemData(comics);
        return comics;
    }

    @Override
    public List<Event> sendEvents(List<Event> events) {
        mEventAdapter.setItemData(events);
        return events;
    }

    @Override
    public List<Character> sendCharacterComics(List<Character> characters) {
        mCharacterAdapter.setItemData(characters);
        return characters;
    }

    @Override
    public List<Event> sendEventComics(List<Event> events) {
        mEventAdapter.setItemData(events);
        return events;
    }

    @Override
    public List<Series> sendSeriesComics(List<Series> series) {
        mSeriesAdapter.setItemData(series);
        return series;
    }
}
