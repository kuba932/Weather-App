package com.example.wateherapp;

import android.os.Build;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MyIconLoader {

    ImageView imageView;
    String iconString;

    public MyIconLoader (ImageView imageView){
        this.imageView = imageView;
    }

    public void loadImage (String iconString){

        Picasso.get().setLoggingEnabled(true);

        if(iconString.equals("01d")){

            Picasso.get().load(R.mipmap.ic_sunny).resize(400,400).into(imageView);

        }if(iconString.equals("02d")){

            Picasso.get().load(R.mipmap.ic_cloudy_sun).resize(400,400).into(imageView);

        }else{

            String url = "https://openweathermap.org/img/wn/" + iconString + "@2x.png";

            Picasso.get()
                    .load(url)
                    .resize(500,500)
                    .into(imageView);
        }
    }
}
