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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicHelper {

    private final String LOG_TAG = ComicHelper.class.getSimpleName();
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;
    private Data<Event> eventData;
    private Data<Character> characterData;
    private final SendComicData comicInterface;
    private List<Event> mEvents = new ArrayList<>();
    private List<Character> mCharacters = new ArrayList<>();

    public ComicHelper(SendComicData dataInterface) {
        comicInterface = dataInterface;
    }

    public void retrieveComicCharacters(Context context, Comic comic) {
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
                    comicInterface.sendComicCharacters(mCharacters);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Character>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
                //call.clone().enqueue(this);
            }
        });
    }

    public void retrieveComicEvents(final Context context, Comic comic) {
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
                    comicInterface.sendComicEvents(mEvents);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Event>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
                Log.v(LOG_TAG, "Cause: " + t.getCause());
                Toast.makeText(context, "Event Call Failed, Retrying", Toast.LENGTH_SHORT).show();
                //call.clone().enqueue(this);
            }
        });
    }

    public interface SendComicData {
        void sendComicCharacters(List<Character> characters);

        void sendComicEvents(List<Event> events);
    }
}
