package com.example.android.marvelpedia.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.marvelpedia.Adapters.EventDetailAdapter;
import com.example.android.marvelpedia.BuildConfig;
import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.Utils.Network.GetMarvelData;
import com.example.android.marvelpedia.Utils.Network.RetrofitInstance;
import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Data;
import com.example.android.marvelpedia.model.Event;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFragment extends Fragment implements EventDetailAdapter.EventOnClick {

    private final static String LOG_TAG = EventFragment.class.getSimpleName();
    private RecyclerView eventRecyclerView;
    private Comic retrievedComic;
    private Character retrievedCharacter;
    private EventDetailAdapter mEventAdapter;
    private TextView eventDescription, eventDetailHeader;
    private Data<Event> eventData;
    private RecyclerView.LayoutManager layoutManager;
    private List<Event> mEvents = new ArrayList<>();

    public EventFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_event_list, container, false);

        //Get the reference to the character recycler view
        eventRecyclerView = rootView.findViewById(R.id.event_recycler_view);
        eventDetailHeader = rootView.findViewById(R.id.event_header);

        //Retrieve the passed arguments from the parent activity
        Bundle passedArgs = getArguments();

        //Retrieve the parcelable argument with key "character_extras"
        retrievedCharacter = passedArgs.getParcelable("character_extras");

        retrieveEvents();

        //return the rootView
        return rootView;
    }

    private void retrieveEvents() {
        populateUi();
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        String apiKey = BuildConfig.MARVEL_API_KEY;
        String privateKey = BuildConfig.MARVEL_HASH_KEY;
        Call<BaseJsonResponse<Event>> eventCall = marvelData.getCharacterEvents(retrievedCharacter.getId(), "1", apiKey, privateKey, 50);
        Log.v(LOG_TAG, "" +
                eventCall.request().url());

        eventCall.enqueue(new Callback<BaseJsonResponse<Event>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Event>> call, Response<BaseJsonResponse<Event>> response) {
                if (response.isSuccessful()) {
                    mEvents.clear();
                    eventData = response.body().getData();
                    mEvents = eventData.getResults();
                    //Log.v(LOG_TAG, fetchedData.getCharacterData().getCount().toString());
                    //mCharacters = fetchedData.getCharacterData().getCharacters();
                    mEventAdapter.setEventData(mEvents);
                    /*for (int i = 0; i < 10; i++){
                        Log.v(LOG_TAG, mCharacters.get(i).getName());
                    }*/
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Event>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
            }
        });
    }

    private void populateUi() {
        //Create a new Event Adapter
        // This adapter takes in an empty list of Event as well as a context
        mEventAdapter = new EventDetailAdapter(getContext(), mEvents, this);

        //Set the adapter on the RecyclerView
        eventRecyclerView.setAdapter(mEventAdapter);

        //Create a Horizontal Linear Layout manager
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        eventRecyclerView.setLayoutManager(layoutManager);
    }


    @Override
    public void onClick(Event event) {

    }
}
