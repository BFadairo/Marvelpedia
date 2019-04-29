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
import com.example.android.marvelpedia.model.Series;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeriesRepository {

    private final String LOG_TAG = SeriesRepository.class.getSimpleName();

    //Marvel API and Hash Key
    private final String apiKey = BuildConfig.MARVEL_API_KEY;
    private final String privateKey = BuildConfig.MARVEL_HASH_KEY;

    private List<Series> mSeries;
    private Data<Series> seriesData;

    private MutableLiveData<List<Series>> heroSeriesList;

    public LiveData<List<Series>> getCharacterSeries(Character character) {
        heroSeriesList = new MutableLiveData<>();
        retrieveCharacterSeries(character);
        return heroSeriesList;
    }

    private void retrieveCharacterSeries(Character character) {
        GetMarvelData marvelData = new RetrofitInstance().getRetrofitInstance().create(GetMarvelData.class);
        Call<BaseJsonResponse<Series>> seriesCall = marvelData.getCharacterSeries(character.getId(), "1", apiKey, privateKey, 50);
        Log.v(LOG_TAG, "" +
                seriesCall.request().url());
        seriesCall.enqueue(new Callback<BaseJsonResponse<Series>>() {
            @Override
            public void onResponse(Call<BaseJsonResponse<Series>> call, Response<BaseJsonResponse<Series>> response) {
                if (response.isSuccessful()) {
                    seriesData = response.body().getData();
                    mSeries = seriesData.getResults();
                    heroSeriesList.postValue(mSeries);
                }
            }

            @Override
            public void onFailure(Call<BaseJsonResponse<Series>> call, Throwable t) {
                Log.v(LOG_TAG, t.getMessage());
            }
        });
    }
}
