package com.example.wateherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LocationListener, NavigationView.OnNavigationItemSelectedListener {

    //TODO menu w praywm górnym rogu
    //TODO ekran ładownia na czas ściągania danych

    final private String apiKey = "1d702a245455321c8dad01ee15794d96";

    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;

    TextView temperatureLarge;
    TextView weatherName;

    TextView lowestTemperature;
    TextView windSpeed;
    TextView humidity;
    TextView pressure;

    ImageView imageIcon;

    Retrofit getWeatherRetrofit;
    GetWeatherInterface getWeatherInterface;

    LocationManager locationManager;

    MyIconLoader myIconLoader;

    static private double latitude , longitude;
    private boolean isGPS = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.snowWhite));
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        temperatureLarge = findViewById(R.id.temperatureLarge);
        weatherName = findViewById(R.id.textWeather);

        lowestTemperature = findViewById(R.id.lowestTemperature);
        windSpeed = findViewById(R.id.windSpeed);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);

        imageIcon = findViewById(R.id.weatherIcon);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        myIconLoader = new MyIconLoader(imageIcon);

        new GpsUtils(this, locationManager).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                isGPS = isGPSEnable;
            }
        });

        if(isGPS) {
            checkPermission();
        }else {
            Toast.makeText(this, this.getString(R.string.turn_on_GPS), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
        getConnection();
    }

    private void getConnection() {
        getWeatherRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getWeatherInterface = getWeatherRetrofit.create(GetWeatherInterface.class);

        getWeatherInterface.getWeather(String.valueOf(latitude),String.valueOf(longitude), apiKey, "metric").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject responseObject = response.body();
                JsonArray weatherArray = responseObject.getAsJsonArray("weather");
                JsonObject weatherObject = (JsonObject) weatherArray.get(0);

                String weather = weatherObject.get("description").getAsString();
                String iconString = weatherObject.get("icon").getAsString();

                JsonObject tempObject = responseObject.get("main").getAsJsonObject();
                int temp = tempObject.get("temp").getAsInt();
                int temp_night = tempObject.get("temp_min").getAsInt();
                int tempHumidity = tempObject.get("humidity").getAsInt();
                int tempPressure = tempObject.get("pressure").getAsInt();

                JsonObject windObject = responseObject.get("wind").getAsJsonObject();
                double tempWindSpeed = windObject.get("speed").getAsDouble();

                temperatureLarge.setText(temp + " °C");
                weatherName.setText(weather);
                lowestTemperature.setText(temp_night + " °C");
                windSpeed.setText(tempWindSpeed + " m/sec");
                humidity.setText(tempHumidity + " %");
                pressure.setText(tempPressure + " hPa");

                myIconLoader.loadImage(iconString);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                Log.d("Retrofit error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int locationRequestCode = 1000;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);
        }else {
            getLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                getLocation();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getConnection();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("location status", "location enabled");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("location status", "location disabled");
    }

    //GPS
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case (R.id.paris):
                Toast.makeText(MainActivity.this, "Paryż lokacja", Toast.LENGTH_SHORT).show();

        }

        // tutaj ustawić miasta
        //id kieruje do id itemów activity main menu

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}