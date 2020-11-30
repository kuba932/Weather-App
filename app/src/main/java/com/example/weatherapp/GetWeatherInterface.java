package com.example.weatherapp;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetWeatherInterface {


    @GET("data/2.5/weather")
    Call<JsonObject> getWeather (@Query("lat") String latitude, @Query("lon") String longitude, @Query("appid") String APIKey, @Query("units") String units);
}
