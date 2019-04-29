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

public class SeriesHelper {

    private final String LOG_TAG = SeriesHelper.class.getSimpleName();
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;
    private Data<Comic> comicData;
    private Data<Character> characterData;
    private Data<Event> eventData;
    private List<Comic> mComics = new ArrayList<>();
    private List<Character> mCharacters = new ArrayList<>();
    private List<Event> mEvent = new ArrayList<>();
    private final SendSeriesData mSeriesInterface;

    public SeriesHelper(SendSeriesData mSeriesInterface) {
        this.mSeriesInterface = mSeriesInterface;
    }

    public void retrieveSeriesCharacters(Series series) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        final Call<BaseJsonResponse<Character>> characterCall = marvelData.getSeriesCharacters(series.getSeriesId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                characterCall.request().url());

        characterCall.enqueue(new Callback<BaseJsonResponse<Character>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Character>> call, Response<BaseJsonResponse<Character>> response) {
                if (response.isSuccessful()) {
                    mCharacters.clear();
                    characterData = response.body().getData();
                    mCharacters = characterData.getResults();
                    mSeriesInterface.sendSeriesCharacters(mCharacters);
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

    public void retrieveSeriesComics(Series series) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        final Call<BaseJsonResponse<Comic>> comicCall = marvelData.getSeriesComics(series.getSeriesId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                comicCall.request().url());

        comicCall.enqueue(new Callback<BaseJsonResponse<Comic>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Comic>> call, Response<BaseJsonResponse<Comic>> response) {
                if (response.isSuccessful()) {
                    mComics.clear();
                    comicData = response.body().getData();
                    mComics = comicData.getResults();
                    mSeriesInterface.sendSeriesComics(mComics);
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

    public void retrieveSeriesEvents(Series series) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        final Call<BaseJsonResponse<Event>> eventCall = marvelData.getSeriesEvents(series.getSeriesId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                eventCall.request().url());

        eventCall.enqueue(new Callback<BaseJsonResponse<Event>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Event>> call, Response<BaseJsonResponse<Event>> response) {
                if (response.isSuccessful()) {
                    mEvent.clear();
                    eventData = response.body().getData();
                    mEvent = eventData.getResults();
                    mSeriesInterface.sendSeriesEvents(mEvent);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Event>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
                Log.v(LOG_TAG, "Attempting Call Again");
                call.clone().enqueue(this);
            }
        });
    }


    public interface SendSeriesData {
        void sendSeriesEvents(List<Event> events);

        void sendSeriesComics(List<Comic> comics);

        void sendSeriesCharacters(List<Character> characters);
    }
}
