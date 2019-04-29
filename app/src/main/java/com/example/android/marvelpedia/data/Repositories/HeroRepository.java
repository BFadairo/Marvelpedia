package com.example.android.marvelpedia.data.Repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeroRepository {

    private final String LOG_TAG = HeroRepository.class.getSimpleName();
    //Marvel API and Hash Key
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;
    private List<Character> mCharacters;
    private Data<Character> characterData;
    private List<Comic> mComics;
    private Data<Comic> comicData;
    private List<Event> mEvents;
    private Data<Event> eventData;
    private List<Series> mSeries;
    private Data<Series> seriesData;
    private MutableLiveData<List<Character>> heroData;
    private MutableLiveData<List<Comic>> heroComicList;
    private MutableLiveData<List<Event>> heroEventList;
    private MutableLiveData<List<Series>> heroSeriesList;

    public LiveData<List<Character>> getSearchTermHeroes(String query) {
        heroData = new MutableLiveData<>();
        gatherSearchTermHeroes(query);
        //Log.v(LOG_TAG, mCharacters.get(0).getName());
        return heroData;
    }

    public LiveData<List<Character>> getComicCharacters(Comic comic) {
        heroData = new MutableLiveData<>();
        gatherComicCharacters(comic);
        return heroData;
    }

    private void gatherSearchTermHeroes(String searchTerm) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        Call<BaseJsonResponse<Character>> characterCall = marvelData.getCharacters("1", apiKey, privateKey, searchTerm, 100);
        Log.v(LOG_TAG, "" +
                characterCall.request().url());
        characterCall.enqueue(new Callback<BaseJsonResponse<Character>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Character>> call, Response<BaseJsonResponse<Character>> response) {
                if (response.isSuccessful()) {
                    characterData = response.body().getData();
                    mCharacters = characterData.getResults();
                    heroData.postValue(mCharacters);
                    Log.v(LOG_TAG, "Retrofit Call Successful");
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Character>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
            }
        });
    }

    private void gatherComicCharacters(Comic comic) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        Call<BaseJsonResponse<Character>> characterCall = marvelData.getComicCharacters(comic.getId(), "1", apiKey, privateKey);
        Log.v(LOG_TAG, "" +
                characterCall.request().url());
        characterCall.enqueue(new Callback<BaseJsonResponse<Character>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Character>> call, Response<BaseJsonResponse<Character>> response) {
                characterData = response.body().getData();
                mCharacters = characterData.getResults();
                heroData.postValue(mCharacters);
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Character>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
            }
        });
    }
}
