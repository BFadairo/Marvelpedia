package com.example.android.marvelpedia.Utils.Network;

import android.util.Log;

import com.example.android.marvelpedia.BuildConfig;
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

public class CharacterHelper {

    private final String LOG_TAG = CharacterHelper.class.getSimpleName();
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;
    private Data<Event> eventData;
    private Data<Comic> comicData;
    private Data<Series> seriesData;
    private List<Event> mEvents = new ArrayList<>();
    private List<Comic> mComics = new ArrayList<>();
    private List<Series> mSeries = new ArrayList<>();
    private final SendCharacterData mCharacterInterface;

    public CharacterHelper(SendCharacterData characterInterface) {
        mCharacterInterface = characterInterface;
    }


    public void retrieveCharacterEvents(Character character) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        final Call<BaseJsonResponse<Event>> eventCall = marvelData.getCharacterEvents(character.getId(), "1", apiKey, privateKey, 50);
        Log.v(LOG_TAG, "" +
                eventCall.request().url());

        eventCall.enqueue(new Callback<BaseJsonResponse<Event>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Event>> call, Response<BaseJsonResponse<Event>> response) {
                if (response.isSuccessful()) {
                    mEvents.clear();
                    eventData = response.body().getData();
                    mEvents = eventData.getResults();
                    mCharacterInterface.sendCharacterEvents(mEvents);
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

    public void retrieveCharacterComics(Character character) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        Call<BaseJsonResponse<Comic>> comicCall = marvelData.getCharacterComics(character.getId(), "1", apiKey, privateKey, 50);
        Log.v(LOG_TAG, "" +
                comicCall.request().url());

        comicCall.enqueue(new Callback<BaseJsonResponse<Comic>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Comic>> call, Response<BaseJsonResponse<Comic>> response) {
                if (response.isSuccessful()) {
                    mComics.clear();
                    comicData = response.body().getData();
                    mComics = comicData.getResults();
                    mCharacterInterface.sendCharacterComics(mComics);
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

    public void retrieveCharacterSeries(Character character) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        Call<BaseJsonResponse<Series>> seriesCall = marvelData.getCharacterSeries(character.getId(), "1", apiKey, privateKey, 50);
        Log.v(LOG_TAG, "" +
                seriesCall.request().url());
        seriesCall.enqueue(new Callback<BaseJsonResponse<Series>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Series>> call, Response<BaseJsonResponse<Series>> response) {
                if (response.isSuccessful()) {
                    mSeries.clear();
                    seriesData = response.body().getData();
                    mSeries = seriesData.getResults();
                    mCharacterInterface.sendCharacterSeries(mSeries);
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

    public interface SendCharacterData {
        void sendCharacterSeries(List<Series> series);

        void sendCharacterComics(List<Comic> comics);

        void sendCharacterEvents(List<Event> events);
    }
}
