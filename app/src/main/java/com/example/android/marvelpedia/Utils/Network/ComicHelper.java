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

public class ComicHelper {

    public final String LOG_TAG = ComicHelper.class.getSimpleName();
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;
    private Data<Event> eventData;
    private Data<Character> characterData;
    private Data<Series> seriesData;
    private List<Event> mEvents = new ArrayList<>();
    private List<Character> mCharacters = new ArrayList<>();
    private List<Series> mSeries = new ArrayList<>();
    private SendComicData comicInterface;

    public ComicHelper(SendComicData dataInterface) {
        comicInterface = dataInterface;
    }

    public void retrieveComicCharacters(Comic comic) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        Call<BaseJsonResponse<Character>> characterCall = marvelData.getComicCharacters(comic.getId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                characterCall.request().url());
        characterCall.enqueue(new Callback<BaseJsonResponse<Character>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Character>> call, Response<BaseJsonResponse<Character>> response) {
                if (response.isSuccessful()) {
                    mCharacters.clear();
                    characterData = response.body().getData();
                    mCharacters = characterData.getResults();
                    comicInterface.sendCharacterComics(mCharacters);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Character>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
            }
        });
    }

    public void retrieveComicEvents(Comic comic) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        Call<BaseJsonResponse<Event>> eventCall = marvelData.getComicEvents(comic.getId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                eventCall.request().url());
        eventCall.enqueue(new Callback<BaseJsonResponse<Event>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Event>> call, Response<BaseJsonResponse<Event>> response) {
                if (response.isSuccessful()) {
                    mEvents.clear();
                    eventData = response.body().getData();
                    mEvents = eventData.getResults();
                    comicInterface.sendEventComics(mEvents);
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

    public void retrieveComicSeries(Comic comic) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        Call<BaseJsonResponse<Series>> seriesComics = marvelData.getComicSeries(comic.getId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                seriesComics.request().url());
        seriesComics.enqueue(new Callback<BaseJsonResponse<Series>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Series>> call, Response<BaseJsonResponse<Series>> response) {
                if (response.isSuccessful()) {
                    mSeries.clear();
                    seriesData = response.body().getData();
                    mSeries = seriesData.getResults();
                    comicInterface.sendSeriesComics(mSeries);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Series>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
            }
        });
    }

    public interface SendComicData {
        List<Character> sendCharacterComics(List<Character> characters);

        List<Event> sendEventComics(List<Event> events);

        List<Series> sendSeriesComics(List<Series> series);
    }
}
