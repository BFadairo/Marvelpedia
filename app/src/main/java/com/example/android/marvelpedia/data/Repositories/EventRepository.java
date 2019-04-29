package com.example.android.marvelpedia.data.Repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.marvelpedia.BuildConfig;
import com.example.android.marvelpedia.data.Retrofit.GetMarvelData;
import com.example.android.marvelpedia.data.Retrofit.RetrofitInstance;
import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Data;
import com.example.android.marvelpedia.model.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepository {

    private final String LOG_TAG = EventRepository.class.getSimpleName();
    //Marvel API and Hash Key
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;

    private List<Event> mEvents;
    private Data<Event> eventData;

    private MutableLiveData<List<Event>> heroEventList;

    public LiveData<List<Event>> getCharacterEvents(Character character) {
        heroEventList = new MutableLiveData<>();
        retrieveCharacterEvents(character);
        return heroEventList;
    }

    public LiveData<List<Event>> getComicEvents(Comic comic) {
        heroEventList = new MutableLiveData<>();
        retrieveComicEvents(comic);
        return heroEventList;
    }

    private void retrieveCharacterEvents(Character character) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        final Call<BaseJsonResponse<Event>> eventCall = marvelData.getCharacterEvents(character.getId(), "1", apiKey, privateKey, 50);
        Log.v(LOG_TAG, "" +
                eventCall.request().url());

        eventCall.enqueue(new Callback<BaseJsonResponse<Event>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Event>> call, Response<BaseJsonResponse<Event>> response) {
                if (response.isSuccessful()) {
                    eventData = response.body().getData();
                    mEvents = eventData.getResults();
                    heroEventList.postValue(mEvents);
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Event>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
            }
        });
    }

    private void retrieveComicEvents(Comic comic) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        final Call<BaseJsonResponse<Event>> eventCall = marvelData.getCharacterEvents(comic.getId(), "1", apiKey, privateKey, 50);
        Log.v(LOG_TAG, "" +
                eventCall.request().url());

        eventCall.enqueue(new Callback<BaseJsonResponse<Event>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Event>> call, Response<BaseJsonResponse<Event>> response) {
                if (response.isSuccessful()) {
                    eventData = response.body().getData();
                    mEvents = eventData.getResults();
                    heroEventList.postValue(mEvents);
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Event>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
            }
        });
    }
}
