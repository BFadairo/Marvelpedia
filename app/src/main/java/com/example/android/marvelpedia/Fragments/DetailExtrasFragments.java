package com.example.android.marvelpedia.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.marvelpedia.Adapters.DetailExtrasAdapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailExtrasFragments<T> extends Fragment implements DetailExtrasAdapter.ItemOnClick, CharacterHelper.SendCharacterData,
        ComicHelper.SendComicData, EventHelper.SendEventData, SeriesHelper.SendSeriesData {

    private final static String LOG_TAG = DetailExtrasFragments.class.getSimpleName();
    private String CHARACTER_EXTRAS, COMIC_EXTRAS, EVENT_EXTRAS, SERIES_EXTRAS, CHARACTER_TAG, COMIC_TAG,
            EVENT_TAG, SERIES_TAG;
    @BindView(R.id.test_recycler_view)
    RecyclerView itemRecyclerView;
    private Parcelable retrievedItem;
    private final String CHARACTER_TRANSITION_NAME = "character_transition";
    private final String COMIC_TRANSITION_NAME = "comic_transition";
    private DetailExtrasAdapter<Comic> mComicAdapter;
    private DetailExtrasAdapter<Event> mEventAdapter;
    private DetailExtrasAdapter<Series> mSeriesAdapter;
    private DetailExtrasAdapter<Character> mCharacterAdapter;
    @BindView(R.id.test_item_header)
    TextView itemDetailHeader;
    private final List<Comic> mComics = new ArrayList<>();
    private final List<Character> mCharacters = new ArrayList<>();
    private final List<Event> mEvents = new ArrayList<>();
    private final List<Series> mSeries = new ArrayList<>();
    private String transitionName;
    private FragmentManager fragmentManager;

    public DetailExtrasFragments() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail_extras, container, false);


        retrieveStrings();
        ButterKnife.bind(this, rootView);

        AddToDatabase characterWriter = (AddToDatabase) getActivity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    rootView.getViewTreeObserver().removeOnPreDrawListener(this);
                    getActivity().startPostponedEnterTransition();
                    return true;
                }
            });
        }

        //Setup the helpers for each class (contain the API calls)
        CharacterHelper characterHelper = new CharacterHelper(this);
        ComicHelper comicHelper = new ComicHelper(this);
        EventHelper eventHelper = new EventHelper(this);
        SeriesHelper seriesHelper = new SeriesHelper(this);

        //Retrieve the passed arguments from the parent activity
        Bundle passedArgs = getArguments();
        if (passedArgs != null) {
            transitionName = passedArgs.getString(CHARACTER_TRANSITION_NAME);

            if (passedArgs.getParcelable(CHARACTER_EXTRAS) != null) {
                retrievedItem = passedArgs.getParcelable(CHARACTER_EXTRAS);
            } else if (passedArgs.getParcelable(COMIC_EXTRAS) != null) {
                retrievedItem = passedArgs.getParcelable(COMIC_EXTRAS);
            } else if (passedArgs.getParcelable(EVENT_EXTRAS) != null) {
                retrievedItem = passedArgs.getParcelable(EVENT_EXTRAS);
            } else if (passedArgs.getParcelable(SERIES_EXTRAS) != null) {
                retrievedItem = passedArgs.getParcelable(SERIES_EXTRAS);
            }
        }

        Log.v(LOG_TAG, (retrievedItem instanceof Character) + "Char");
        Log.v(LOG_TAG, (retrievedItem instanceof Comic) + "Comic");
        Log.v(LOG_TAG, (retrievedItem instanceof Event) + "Event");
        Log.v(LOG_TAG, (retrievedItem instanceof Series) + "Series");

        String currentTag = this.getTag();

        //Initialize the fragment manager
        initializeFragmentManager();

        if (passedArgs != null) {
            if (passedArgs.getParcelable(CHARACTER_EXTRAS) != null) {
                if (retrievedItem instanceof Character) {
                    Character currentCharacter = (Character) retrievedItem;
                    Log.v(LOG_TAG, "Character Is Object");
                    Log.v(LOG_TAG, this.getTag());
                    characterWriter.addToDb(currentCharacter);
                    populateUi();

                    switch (currentTag) {
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
                }
            } else if (passedArgs.getParcelable(COMIC_EXTRAS) != null) {
                if (retrievedItem instanceof Comic) {
                    Log.v(LOG_TAG, "Comic Is Object");
                    populateUi();
                    switch (currentTag) {
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
                    switch (currentTag) {
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
                    switch (currentTag) {
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
            mComicAdapter = new DetailExtrasAdapter<>(getContext(), mComics, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mComicAdapter);
        } else if (this.getTag().equals(EVENT_TAG)) {
            //Create a new event Adapter
            // This adapter takes in an empty list of items as well as a context
            mEventAdapter = new DetailExtrasAdapter<>(getContext(), mEvents, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mEventAdapter);
        } else if (this.getTag().equals(SERIES_TAG)) {
            //Create a new series Adapter
            // This adapter takes in an empty list of series items as well as a context
            mSeriesAdapter = new DetailExtrasAdapter<>(getContext(), mSeries, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mSeriesAdapter);
        } else if (this.getTag().equals(CHARACTER_TAG)) {
            //Create a new Character Adapter
            mCharacterAdapter = new DetailExtrasAdapter<>(getContext(), mCharacters, this);

            //Set the adapter on the RecyclerView
            itemRecyclerView.setAdapter(mCharacterAdapter);
        }

        //Create a horizontal Linear Layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        itemRecyclerView.setLayoutManager(layoutManager);
    }

    private void initializeFragmentManager() {
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onClick(int adapterPosition, Object item, ImageView transitionView) {
        Bundle argsToPass = new Bundle();
        if (item instanceof Comic) {
            DetailExtrasFragments<Character> newCharacterFragment = new DetailExtrasFragments<>();
            DetailExtrasFragments<Event> newEventFragment = new DetailExtrasFragments<>();
            DetailExtrasFragments<Series> newSeriesFragment = new DetailExtrasFragments<>();
            DetailFragment newDetailFragment = new DetailFragment();
            Log.v(LOG_TAG, ((Comic) item).getTitle());
            argsToPass.putParcelable(COMIC_EXTRAS, (Comic) item);
            argsToPass.putString(CHARACTER_TRANSITION_NAME, ViewCompat.getTransitionName(transitionView));
            newCharacterFragment.setArguments(argsToPass);
            newDetailFragment.setArguments(argsToPass);
            newEventFragment.setArguments(argsToPass);
            newSeriesFragment.setArguments(argsToPass);

            Transition changeTransform = TransitionInflater.from(getContext())
                    .inflateTransition(R.transition.change_image_transform);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .addSharedElement(transitionView, ViewCompat.getTransitionName(transitionView))
                    .addToBackStack(null)
                    .replace(R.id.detail_information_container, newDetailFragment)
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

    public interface AddToDatabase {
        void addToDb(Character character);
    }

    @Override
    public void sendComicCharacters(List<Character> characters) {
        mCharacterAdapter.setItemData(characters);
    }

    @Override
    public void sendEventCharacters(List<Character> characters) {
        mCharacterAdapter.setItemData(characters);
    }

    @Override
    public void sendSeriesCharacters(List<Character> characters) {
        mCharacterAdapter.setItemData(characters);
    }

    @Override
    public void sendCharacterEvents(List<Event> events) {
        mEventAdapter.setItemData(events);
    }

    @Override
    public void sendComicEvents(List<Event> events) {
        mEventAdapter.setItemData(events);
    }

    @Override
    public void sendSeriesEvents(List<Event> events) {
        mEventAdapter.setItemData(events);
    }


    @Override
    public void sendCharacterComics(List<Comic> comics) {
        mComicAdapter.setItemData(comics);
    }

    @Override
    public void sendEventComics(List<Comic> comics) {
        mComicAdapter.setItemData(comics);
    }

    @Override
    public void sendSeriesComics(List<Comic> comics) {
        mComicAdapter.setItemData(comics);
    }


    @Override
    public void sendEventSeries(List<Series> series) {
        mSeriesAdapter.setItemData(series);
    }

    @Override
    public void sendComicSeries(List<Series> series) {
        mSeriesAdapter.setItemData(series);
    }

    @Override
    public void sendCharacterSeries(List<Series> series) {
        mSeriesAdapter.setItemData(series);
    }
}
