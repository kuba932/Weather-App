# Weather App
Simple weather app based on OpenWeatherMap API.

![weather-app](https://i.imgur.com/A6DsHH0.png)

## Table of content
* [General info](#general-info)
* [Built with](#built-with)
* [Setup](#setup)
* [License](#license)

## General info
I approached this project as an opportunity to improve my knowledge about REST API and to experiment with the conception of the Drawer Layout.

This simple app provides the user with basic weather data (temperature day & night, windspeed, humidity and pressure) using only one screen.

The Weather App constantly checks the current device's location. If location status changes, the app passes new coordinates to OpenWeatherMap server via Retrofit and OpenWeatherMap API to fetch latest statistics.

The user can also check weather in different cities after clicking on the "hamburger" icon.

## Setup
To test this project you need to download this repository and emulate it in Android Studio.

To do that you have to download and install the Android environment:
* JDK & JRE - https://developer.android.com/studio/install
* Android SDK - https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwiM3KST4aztAhVNAxAIHSRACHcQFjACegQIFhAC&url=https%3A%2F%2Fdeveloper.android.com%2Fstudio&usg=AOvVaw3fIlahucURgOEYHHhVdQuW

After that you can simply import this repository to Android Studio and run it on AVD.

You can also download the .apk file and install it straight on your Android device! - https://github.com/kuba932/Weather-App/releases/

## Built with
 * Java
 * OpenWeatherMap API
 * Retrofit from Square
 * Picasso from Square
 * Material Design from Google

## License
This code is developed under the terms of the OpenWeatherMap license.
