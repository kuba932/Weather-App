package com.example.wateherapp;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface GetWeatherInterface {


    @GET("data/2.5/weather")
    Call<JsonObject> getWeather (@Query("lat") String latitude, @Query("lon") String longitude, @Query("appid") String APIKey, @Query("units") String units);
}
