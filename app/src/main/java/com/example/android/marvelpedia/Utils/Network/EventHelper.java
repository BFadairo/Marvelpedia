package com.example.android.marvelpedia.Utils.Network;

import android.util.Log;

import com.example.android.marvelpedia.BuildConfig;
import com.example.android.marvelpedia.data.Retrofit.GetMarvelData;
import com.example.android.marvelpedia.data.Retrofit.RetrofitInstance;
import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Data;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventHelper {

    private final String LOG_TAG = EventHelper.class.getSimpleName();
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;
    private Data<Comic> comicData;
    private Data<Character> characterData;
    private Data<Series> seriesData;
    private List<Comic> mComics = new ArrayList<>();
    private List<Character> mCharacters = new ArrayList<>();
    private List<Series> mSeries = new ArrayList<>();
    private final SendEventData mEventInterface;

    public EventHelper(SendEventData mEventInterface) {
        this.mEventInterface = mEventInterface;
    }

    public void retrieveEventCharacters(Event event) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        final Call<BaseJsonResponse<Character>> characterCall = marvelData.getEventCharacters(event.getEventId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                characterCall.request().url());

        characterCall.enqueue(new Callback<BaseJsonResponse<Character>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Character>> call, Response<BaseJsonResponse<Character>> response) {
                if (response.isSuccessful()) {
                    mCharacters.clear();
                    characterData = response.body().getData();
                    mCharacters = characterData.getResults();
                    mEventInterface.sendEventCharacters(mCharacters);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Character>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
                Log.v(LOG_TAG, "Attempting Call Again");
                call.clone().enqueue(this);
            }
        });
    }

    public void retrieveEventComics(Event event) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        final Call<BaseJsonResponse<Comic>> comicCall = marvelData.getEventComics(event.getEventId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                comicCall.request().url());

        comicCall.enqueue(new Callback<BaseJsonResponse<Comic>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Comic>> call, Response<BaseJsonResponse<Comic>> response) {
                if (response.isSuccessful()) {
                    mComics.clear();
                    comicData = response.body().getData();
                    mComics = comicData.getResults();
                    mEventInterface.sendEventComics(mComics);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Comic>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
                Log.v(LOG_TAG, "Attempting Call Again");
                call.clone().enqueue(this);
            }
        });
    }

    public void retrieveEventSeries(Event event) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        final Call<BaseJsonResponse<Series>> seriesCall = marvelData.getEventSeries(event.getEventId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                seriesCall.request().url());

        seriesCall.enqueue(new Callback<BaseJsonResponse<Series>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Series>> call, Response<BaseJsonResponse<Series>> response) {
                if (response.isSuccessful()) {
                    mSeries.clear();
                    seriesData = response.body().getData();
                    mSeries = seriesData.getResults();
                    mEventInterface.sendEventSeries(mSeries);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Series>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
                Log.v(LOG_TAG, "Attempting Call Again");
                call.clone().enqueue(this);
            }
        });
    }

    public interface SendEventData {
        void sendEventSeries(List<Series> series);

        void sendEventComics(List<Comic> comics);

        void sendEventCharacters(List<Character> characters);
    }

}
