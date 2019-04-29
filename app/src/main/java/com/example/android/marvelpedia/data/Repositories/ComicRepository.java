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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComicRepository {

    private final String LOG_TAG = ComicRepository.class.getSimpleName();
    //Marvel API and Hash Key
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;

    private List<Comic> mComics;
    private Data<Comic> comicData;

    private MutableLiveData<List<Comic>> heroComicList;

    public LiveData<List<Comic>> getCharacterComics(Character character) {
        heroComicList = new MutableLiveData<>();
        retrieveCharacterComics(character);
        return heroComicList;
    }

    private void retrieveCharacterComics(Character character) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        Call<BaseJsonResponse<Comic>> comicCall = marvelData.getCharacterComics(character.getId(), "1", apiKey, privateKey, 50);
        Log.v(LOG_TAG, "" +
                comicCall.request().url());

        comicCall.enqueue(new Callback<BaseJsonResponse<Comic>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Comic>> call, Response<BaseJsonResponse<Comic>> response) {
                if (response.isSuccessful()) {
                    comicData = response.body().getData();
                    mComics = comicData.getResults();
                    heroComicList.postValue(mComics);
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Comic>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
            }
        });
    }
}
