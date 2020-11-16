package com.example.wateherapp;

import android.content.Context;
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

        String url = "https://openweathermap.org/img/wn/" + iconString + "@2x.png";

        Picasso.get()
                .load(url)
                .resize(500,500)
                .into(imageView);
    }
}
