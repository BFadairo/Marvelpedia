package com.example.android.marvelpedia.Utils.Network;

import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMarvelData {

    @GET("characters")
    Call<BaseJsonResponse<Character>> getCharacters(@Query("ts") String timeStamp, @Query("apikey") String apiKey, @Query("hash") String privateKey, @Query("nameStartsWith") String heroName);

    @GET("characters/{characterId}/comics")
    Call<BaseJsonResponse<Comic>> getCharacterComics(@Path("characterId") int CharacterId, @Query("ts") String timeStamp, @Query("apikey") String apiKey, @Query("hash") String privateKey);
}
