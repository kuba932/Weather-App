package com.example.weatherapp;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MyIconLoader {

    ImageView imageView;

    public MyIconLoader (ImageView imageView){
        this.imageView = imageView;
    }

    public void loadImage (String iconString){

        Picasso.get().setLoggingEnabled(true);

            // Swapping default "sunny" picture to custom
        if(iconString.equals("01d") || iconString.equals("01n")){

            Picasso.get().load(R.mipmap.ic_sunny).resize(400,400).into(imageView);

            // Swapping default "cloudy" picture to custom
        }else if(iconString.equals("02d") || iconString.equals("02n")){

            Picasso.get().load(R.mipmap.ic_cloudy_sun).resize(400,400).into(imageView);

            // Uploading default OpenWeatherAPI icons
        }else{

            String url = "https://openweathermap.org/img/wn/" + iconString + "@2x.png";

            Picasso.get()
                    .load(url)
                    .resize(500,500)
                    .into(imageView);
        }
    }
}
