package com.example.android.marvelpedia.ui.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.marvelpedia.ui.Adapters.DetailExtrasAdapter;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Utils.Network.CharacterHelper;
import com.example.android.marvelpedia.Utils.Network.ComicHelper;
import com.example.android.marvelpedia.Utils.Network.EventHelper;
import com.example.android.marvelpedia.Utils.Network.SeriesHelper;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;
import com.example.android.marvelpedia.viewmodel.DetailExtrasViewModel;

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
    private DetailExtrasViewModel detailExtrasViewModel;
    private SendComic mComicInterface;
    private SendCharacter mCharacterInterface;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (retrievedItem instanceof Character) {
            setupCharacterAdapters();
        } else if (retrievedItem instanceof Comic) {
            setupComicAdapters();
        }
    }

    private void setupCharacterAdapters() {
        switch (this.getTag()) {
            case "Comic":
                detailExtrasViewModel.getCharacterComics().observe(this, new Observer<List<Comic>>() {
                    @Override
                    public void onChanged(List<Comic> comics) {
                        if (comics != null) {
                            mComicAdapter.setItemData(comics);
                        }
                    }
                });
                break;
            case "Event":
                detailExtrasViewModel.getCharacterEvents().observe(this, new Observer<List<Event>>() {
                    @Override
                    public void onChanged(List<Event> events) {
                        if (events != null) {
                            mEventAdapter.setItemData(events);
                        }
                    }
                });
                break;
            case "Series":
                detailExtrasViewModel.getCharacterSeries().observe(this, new Observer<List<Series>>() {
                    @Override
                    public void onChanged(List<Series> series) {
                        if (series != null) {
                            mSeriesAdapter.setItemData(series);
                        }
                    }
                });
                break;
        }
    }

    private void setupComicAdapters() {
        switch (this.getTag()) {
            case "Character":
                detailExtrasViewModel.getComicCharacters().observe(this, new Observer<List<Character>>() {
                    @Override
                    public void onChanged(List<Character> characters) {
                        if (characters != null) {
                            mCharacterAdapter.setItemData(characters);
                        }
                    }
                });
                break;
            case "Event":
                detailExtrasViewModel.getComicEvents().observe(this, new Observer<List<Event>>() {
                    @Override
                    public void onChanged(List<Event> events) {
                        if (events != null) {
                            mEventAdapter.setItemData(events);
                        }
                    }
                });
                break;
        }
    }

    public DetailExtrasFragments() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail_extras, container, false);
        detailExtrasViewModel = ViewModelProviders.of(getActivity()).get(DetailExtrasViewModel.class);
        retrieveStrings();
        ButterKnife.bind(this, rootView);

        AddToDatabase characterWriter = (AddToDatabase) getActivity();
        mComicInterface = (SendComic) getActivity();
        mCharacterInterface = (SendCharacter) getActivity();

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
        EventHelper eventHelper = new EventHelper(this);
        SeriesHelper seriesHelper = new SeriesHelper(this);

        //Retrieve the passed arguments from the parent activity
        Bundle passedArgs = getArguments();
        if (passedArgs != null) {
            if (passedArgs.getParcelable(CHARACTER_EXTRAS) != null) {
                retrievedItem = passedArgs.getParcelable(CHARACTER_EXTRAS);
                detailExtrasViewModel.setCurrentCharacter((Character) retrievedItem);
            } else if (passedArgs.getParcelable(COMIC_EXTRAS) != null) {
                retrievedItem = passedArgs.getParcelable(COMIC_EXTRAS);
                detailExtrasViewModel.setCurrentComic((Comic) retrievedItem);
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
                    characterWriter.addToDb(currentCharacter);
                    setLabel(this.getTag());
                    populateUi();
                    switch (currentTag) {
                        case "Comic":
                            //characterHelper.retrieveCharacterComics(getContext(), currentCharacter);
                            break;
                        case "Event":
                            //characterHelper.retrieveCharacterEvents(getContext(), currentCharacter);
                            break;
                        case "Series":
                            //characterHelper.retrieveCharacterSeries(getContext(), currentCharacter);
                            break;
                    }
                }

            } else if (passedArgs.getParcelable(COMIC_EXTRAS) != null) {
                if (retrievedItem instanceof Comic) {
                    Log.v(LOG_TAG, "Comic Is Object");
                    Comic currentComic = (Comic) retrievedItem;
                    Log.v(LOG_TAG, currentComic.getTitle());
                    setLabel(this.getTag());
                    populateUi();
                    switch (currentTag) {
                        case "Character":
                            //comicHelper.retrieveComicCharacters(getContext(), currentComic);
                            break;
                        case "Event":
                            //comicHelper.retrieveComicEvents(getContext(), currentComic);
                            break;
                    }
                }
            } else if (passedArgs.getParcelable(EVENT_EXTRAS) != null) {
                if (retrievedItem instanceof Event) {
                    Log.v(LOG_TAG, "Event Is Object");
                    Event currentEvent = (Event) retrievedItem;
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
        if (item instanceof Comic) {
            mComicInterface.sendComicDetails((Comic) item, transitionView);
        } else if (item instanceof Event) {
            Log.v(LOG_TAG, ((Event) item).getEventTitle());
        } else if (item instanceof Series) {
            Log.v(LOG_TAG, ((Series) item).getSeriesTitle());
        } else if (item instanceof Character) {
            Log.v(LOG_TAG, ((Character) item).getName());
            mCharacterInterface.sendCharacterDetails((Character) item, transitionView);
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
    public void sendCharacterSeries(List<Series> series) {
        mSeriesAdapter.setItemData(series);
    }

    public interface SendComic {
        void sendComicDetails(Comic comic, ImageView transitionView);
    }

    public interface SendCharacter {
        void sendCharacterDetails(Character character, ImageView transitionView);
    }
}
