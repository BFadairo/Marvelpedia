package com.example.android.marvelpedia.data.Retrofit;

import com.example.android.marvelpedia.model.BaseJsonResponse;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMarvelData {

    @GET("characters")
    Call<BaseJsonResponse<Character>> getCharacters(@Query("ts") String timeStamp, @Query("apikey") String apiKey, @Query("hash") String privateKey,
                                                    @Query("nameStartsWith") String heroName, @Query("limit") int limit);

    @GET("characters/{characterId}/comics")
    Call<BaseJsonResponse<Comic>> getCharacterComics(@Path("characterId") int characterId, @Query("ts") String timeStamp,
                                                     @Query("apikey") String apiKey, @Query("hash") String privateKey, @Query("limit") int limit);

    @GET("characters/{characterId}/events")
    Call<BaseJsonResponse<Event>> getCharacterEvents(@Path("characterId") int characterId, @Query("ts") String timeStamp,
                                                     @Query("apikey") String apiKey, @Query("hash") String privateKey, @Query("limit") int limit);

    @GET("characters/{characterId}/series")
    Call<BaseJsonResponse<Series>> getCharacterSeries(@Path("characterId") int characterId, @Query("ts") String timeStamp,
                                                      @Query("apikey") String apiKey, @Query("hash") String privateKey, @Query("limit") int limit);


    @GET("comics/{comicId}/characters")
    Call<BaseJsonResponse<Character>> getComicCharacters(@Path("comicId") int comicId, @Query("ts") String timeStamp,
                                                         @Query("apikey") String apiKey, @Query("hash") String privateKey);

    @GET("comics/{comicId}/events")
    Call<BaseJsonResponse<Event>> getComicEvents(@Path("comicId") int comicId, @Query("ts") String timeStamp,
                                                 @Query("apikey") String apiKey, @Query("hash") String privateKey);

    @GET("events/{eventId}/characters")
    Call<BaseJsonResponse<Character>> getEventCharacters(@Path("eventId") int eventId, @Query("ts") String timeStamp,
                                                         @Query("apikey") String apiKey, @Query("hash") String privateKey);

    @GET("events/{eventId}/comics")
    Call<BaseJsonResponse<Comic>> getEventComics(@Path("eventId") int eventId, @Query("ts") String timeStamp,
                                                 @Query("apikey") String apiKey, @Query("hash") String privateKey);

    @GET("events/{eventId}/series")
    Call<BaseJsonResponse<Series>> getEventSeries(@Path("eventId") int eventId, @Query("ts") String timeStamp,
                                                  @Query("apikey") String apiKey, @Query("hash") String privateKey);

    @GET("series/{seriesId}/characters")
    Call<BaseJsonResponse<Character>> getSeriesCharacters(@Path("seriesId") int seriesId, @Query("ts") String timeStamp,
                                                          @Query("apikey") String apiKey, @Query("hash") String privateKey);

    @GET("series/{seriesId}/comics")
    Call<BaseJsonResponse<Comic>> getSeriesComics(@Path("seriesId") int seriesId, @Query("ts") String timeStamp,
                                                  @Query("apikey") String apiKey, @Query("hash") String privateKey);

    @GET("series/{seriesId}/events")
    Call<BaseJsonResponse<Event>> getSeriesEvents(@Path("seriesId") int seriesId, @Query("ts") String timeStamp,
                                                  @Query("apikey") String apiKey, @Query("hash") String privateKey);

    @GET("comics")
    Call<BaseJsonResponse<Comic>> getTeamComics(@Query("ts") String timeStamp,
                                                @Query("apikey") String apiKey, @Query("hash") String privateKey,
                                                @Query("sharedAppearances") String characterIds);
}
