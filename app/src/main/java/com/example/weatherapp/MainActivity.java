package com.example.weatherapp;

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
import android.view.MenuItem;
import android.view.ViewGroup;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private String apiKey;

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
    private boolean isGPS = false; //flag linked with GpsUtils class. It states current GPS status
    private boolean locationChanger = true; // this flag makes sure that changeLocationManually() won't be overwritten by current location passed by GPS


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiKey  = getString(R.string.DemoApiKey);

        //Adding toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting up drawer layout
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.snowWhite));
        toggle.syncState();

        // Setting up navigation view. It is responsible for choosing different cities (that aren't based on current user location)
        navigationView = findViewById(R.id.nav_view);
        changeNavigationSize();
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        temperatureLarge = findViewById(R.id.temperatureLarge);
        weatherName = findViewById(R.id.textWeather);
        lowestTemperature = findViewById(R.id.lowestTemperature);
        windSpeed = findViewById(R.id.windSpeed);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        imageIcon = findViewById(R.id.weatherIcon);

        // From now on, locationManager has accesses to the system location services
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

    // Passing location to OpenWeatherApi and fetching data for the frontend part
    private void getConnection() {
        getWeatherRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getWeatherInterface = getWeatherRetrofit.create(GetWeatherInterface.class);

        getWeatherInterface.getWeather(String.valueOf(latitude),String.valueOf(longitude), apiKey, "metric").enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
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

                // passing string which represents current weather icon
                myIconLoader.loadImage(iconString);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                Log.d("Retrofit error", t.getMessage());
            }
        });
    }

    // Checks all needed system permissions required by this app
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

    // Next step after checkPermission()
    // if response is successful getLocation()
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

    // 'Called when the location has changed'
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(locationChanger) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            getConnection();
        }
    }

    // Called when the provider is enabled by the user
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("location status", "location enabled");
    }

    //Called when the provider is disabled by the user. If requestLocationUpdates is called on an already disabled provider, this method is called immediately.
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("location status", "location disabled");
        Toast.makeText(MainActivity.this.getBaseContext(), getString(R.string.turn_on_GPS), Toast.LENGTH_SHORT).show();
    }

    //Process the result that is passed from turnGPSOn(final onGpsListener onGpsListener) from GPSUtils class
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true;
            }
        }
    }

    // Set a location for different cities
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case (R.id.current_Location):
                locationChanger = true;
                break;
            case (R.id.paris):
                changeLocationManually(48.864716, 2.349014);
                break;
            case (R.id.manchester):
                changeLocationManually(53.483959, -2.244644);
                break;
            case (R.id.warsaw):
                changeLocationManually(52.229676, 21.012229);
                break;
            case (R.id.berlin):
                changeLocationManually(52.520008, 13.404954);
                break;
            case (R.id.stockholm):
                changeLocationManually(59.334591, 18.063240);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    // Customizing navigationView size so it won't take most of the screen
    private void changeNavigationSize(){
        int width = getResources().getDisplayMetrics().widthPixels/2;
        ViewGroup.LayoutParams params = navigationView.getLayoutParams();
        params.width = width;
        navigationView.setLayoutParams(params);
    }

    //  This method allows the user to choose a city manually
    private void changeLocationManually (double tempLatitude, double tempLongitude){
        locationChanger = false;
        longitude = tempLongitude;
        latitude = tempLatitude;
        getConnection();
    }
}