package com.example.android.marvelpedia.Utils.Network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

    private static final String ACTION_FOO = "com.example.android.marvelpedia.action.FOO";
    private static final String EXTRA_PARAM1 = "com.example.android.marvelpedia.extra.PARAM1";
    private final String LOG_TAG = CharacterHelper.class.getSimpleName();
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;
    private Data<Event> eventData;
    private Data<Comic> comicData;
    private Data<Series> seriesData;
    public List<Character> mCharacters = new ArrayList<>();
    public List<Event> mEvents = new ArrayList<>();
    public List<Comic> mComics = new ArrayList<>();
    public List<Series> mSeries = new ArrayList<>();
    public Boolean isFinished = false;
    public Boolean isFinishedComics = false;
    public Boolean isFinishedEvents = false;
    public Boolean isFinishedSeries = false;
    public Boolean allFinished = false;
    private Data<Character> characterData;
    private SendCharacterData mCharacterInterface;

    public CharacterHelper(SendCharacterData characterInterface) {
        mCharacterInterface = characterInterface;
    }

    public CharacterHelper() {
    }

    public void setCharacters(List<Character> characters) {
        this.mCharacters = characters;
    }

    public void setEvents(List<Event> mEvents) {
        this.mEvents = mEvents;
    }

    public void setComics(List<Comic> mComics) {
        this.mComics = mComics;
    }

    public void setSeries(List<Series> mSeries) {
        this.mSeries = mSeries;
    }

    public void retrieveCharacters(String searchTerm) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        String apiKey = BuildConfig.MARVEL_API_KEY;
        String privateKey = BuildConfig.MARVEL_HASH_KEY;
        Call<BaseJsonResponse<Character>> characterCall = marvelData.getCharacters("1", apiKey, privateKey, searchTerm, 100);
        Log.v(LOG_TAG, "" +
                characterCall.request().url());

        characterCall.enqueue(new Callback<BaseJsonResponse<Character>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Character>> call, Response<BaseJsonResponse<Character>> response) {
                if (response.isSuccessful()) {
                    mCharacters.clear();
                    characterData = response.body().getData();
                    mCharacters = characterData.getResults();
                    setCharacters(mCharacters);
                    //Log.v(LOG_TAG, fetchedData.getCharacterData().getCount().toString());
                    //mCharacters = fetchedData.getCharacterData().getCharacters();
                    //mCharacterAdapter.setCharacterData(mCharacters);
                    /*for (int i = 0; i < 10; i++){
                        Log.v(LOG_TAG, mCharacters.get(i).getName());
                    }*/
                    isFinished = true;
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Character>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                isFinished = true;
            }
        });
    }

    public void retrieveCharacterEvents(final Context context, Character character) {
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
                    //setEvents(mEvents);
                    mCharacterInterface.sendCharacterEvents(mEvents);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                    isFinishedEvents = true;
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Event>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                isFinishedEvents = true;
                Log.v(LOG_TAG, "Cause: " + t.getCause());
                Toast.makeText(context, "Event Call Failed, Retrying", Toast.LENGTH_SHORT).show();
                Log.v(LOG_TAG, "Attempting Call Again");
                //call.clone().enqueue(this);
            }
        });
    }

    public void retrieveCharacterComics(Context context, Character character) {
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
                    //setComics(mComics);
                    mCharacterInterface.sendCharacterComics(mComics);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                    isFinishedComics = true;
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Comic>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                isFinishedComics = true;
                Log.v(LOG_TAG, "Cause: " + t.getCause());
                Log.v(LOG_TAG, "Attempting Call Again");
                //call.clone().enqueue(this);
            }
        });
    }

    public void retrieveCharacterSeries(Context context, Character character) {
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
                    //setSeries(mSeries);
                    mCharacterInterface.sendCharacterSeries(mSeries);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                    isFinishedSeries = true;
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Series>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                isFinishedSeries = true;
                Log.v(LOG_TAG, "Cause: " + t.getCause());
                Log.v(LOG_TAG, "Attempting Call Again");
                //call.clone().enqueue(this);
            }
        });
    }

    public Boolean checkIfAllFinished() {
        if (isFinishedComics && isFinishedEvents && isFinishedSeries) {
            allFinished = true;
        }
        return allFinished;
    }

    public interface SendCharacterData {
        void sendCharacterSeries(List<Series> series);

        void sendCharacterComics(List<Comic> comics);

        void sendCharacterEvents(List<Event> events);
    }
}
