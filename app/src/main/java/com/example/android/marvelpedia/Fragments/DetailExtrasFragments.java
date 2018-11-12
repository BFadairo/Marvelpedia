package com.example.android.marvelpedia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.example.android.marvelpedia.MyReceiver;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Service.DetailIntentService;
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

    private static final String ACTION_EXTRAS = "com.example.android.marvelpedia.action.EXTRAS";
    private static final String EXTRA_PARAM1 = "com.example.android.marvelpedia.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.android.marvelpedia.extra.PARAM2";
    private static final String EXTRA_PARAM3 = "com.example.android.marvelpedia.extra.PARAM3";
    private static final String EXTRA_PARAM4 = "com.example.android.marvelpedia.extra.PARAM4";

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
    private List<Comic> mComics = new ArrayList<>();
    private List<Character> mCharacters = new ArrayList<>();
    private List<Event> mEvents = new ArrayList<>();
    private List<Series> mSeries = new ArrayList<>();
    private String transitionName;
    private SendComic mComicInterface;
    private MyReceiver broadcastReceiver;
    private Intent extrasIntent;

    public DetailExtrasFragments() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail_extras, container, false);


        retrieveStrings();
        ButterKnife.bind(this, rootView);

        AddToDatabase characterWriter = (AddToDatabase) getActivity();
        mComicInterface = (SendComic) getActivity();

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

        //Setup the broadcast Receiver
        setupBroadcastReceiver();

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

        if (passedArgs != null) {
            if (passedArgs.getParcelable(CHARACTER_EXTRAS) != null) {
                if (retrievedItem instanceof Character) {
                    Character currentCharacter = (Character) retrievedItem;
                    Log.v(LOG_TAG, "Character Is Object");
                    Log.v(LOG_TAG, this.getTag());
                    setupExtrasIntent(currentCharacter);
                    characterWriter.addToDb(currentCharacter);
                    setLabel(this.getTag());
                }
            } else if (passedArgs.getParcelable(COMIC_EXTRAS) != null) {
                if (retrievedItem instanceof Comic) {
                    Log.v(LOG_TAG, "Comic Is Object");
                    Comic currentComic = (Comic) retrievedItem;
                    setupExtrasIntent(currentComic);
                    setLabel(this.getTag());
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

    @Override
    public void onClick(int adapterPosition, Object item, ImageView transitionView) {
        Bundle argsToPass = new Bundle();
        if (item instanceof Comic) {
            mComicInterface.sendComicDetails((Comic) item, transitionView);
        } else if (item instanceof Event) {
            Log.v(LOG_TAG, ((Event) item).getEventTitle());
        } else if (item instanceof Series) {
            Log.v(LOG_TAG, ((Series) item).getSeriesTitle());
        } else if (item instanceof Character) {
            Log.v(LOG_TAG, ((Character) item).getName());
        }
    }

    private void setLabel(String currentTag) {
        switch (currentTag) {
            case "Comic":
                itemDetailHeader.setText(R.string.comic_header);
                break;
            case "Event":
                itemDetailHeader.setText(R.string.event_header);
                break;
            case "Story":
                itemDetailHeader.setText(R.string.story_header);
                break;
            case "Series":
                itemDetailHeader.setText(R.string.series_header);
                break;
            case "Character":
                itemDetailHeader.setText(R.string.character_header);
        }
    }

    private void setupExtrasIntent(Character character) {
        if (character != null) {
            DetailIntentService.startActionGetCharacterData(getContext(), character);
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

    private void setupExtrasIntent(Comic comic) {
        if (comic != null) {
            DetailIntentService.startActionGetComicData(getContext(), comic);
        }
    }

    private void setupExtrasIntent(Event event) {
        if (event != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        populateUi();
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

    public void setupBroadcastReceiver() {
        Log.v(LOG_TAG, "Context: " + getContext());
        if (getContext() != null) {
            broadcastReceiver = new MyReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(ACTION_EXTRAS)) {
                        Log.v(LOG_TAG, "Inside onReceive");
                        Bundle receivedArgs = intent.getBundleExtra(EXTRA_PARAM1);
                        mCharacters = receivedArgs.getParcelableArrayList(EXTRA_PARAM1);
                        mComics = receivedArgs.getParcelableArrayList(EXTRA_PARAM2);
                        mEvents = receivedArgs.getParcelableArrayList(EXTRA_PARAM3);
                        mSeries = receivedArgs.getParcelableArrayList(EXTRA_PARAM4);
                        populateUi();

                        if (mCharacterAdapter != null && mCharacters != null) {
                            mCharacterAdapter.setItemData(mCharacters);
                        }
                        if (mComicAdapter != null && mComics != null) {
                            mComicAdapter.setItemData(mComics);
                        }
                        if (mEventAdapter != null && mEvents != null) {
                            mEventAdapter.setItemData(mEvents);
                        }
                        if (mSeriesAdapter != null && mSeries != null) {
                            mSeriesAdapter.setItemData(mSeries);
                        }
                        //throw new UnsupportedOperationException("Not yet implemented");
                    } else {
                        Log.v(LOG_TAG, "OnReceive Skipped");
                    }
                }
            };
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "On Resume Called");
        if (getContext() != null) {
            IntentFilter filter = new IntentFilter(ACTION_EXTRAS);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            getContext().registerReceiver(broadcastReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "On Pause Called");
        if (getContext() != null) {
            Log.v(LOG_TAG, "Stopping Extras Intent");
            getContext().unregisterReceiver(broadcastReceiver);
        }
    }

    public interface SendComic {
        void sendComicDetails(Comic comic, ImageView transitionView);
    }
}
