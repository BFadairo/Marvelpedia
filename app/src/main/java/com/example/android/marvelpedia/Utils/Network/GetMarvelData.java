package com.example.android.marvelpedia.Utils.Network;

import com.example.android.marvelpedia.model.MarvelResultCharacter;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetMarvelData {

    @GET("characters")
    Call<MarvelResultCharacter> getCharacters(@Query("ts") String timeStamp, @Query("apikey") String apiKey, @Query("hash") String privateKey, @Query("nameStartsWith") String heroName);
}
