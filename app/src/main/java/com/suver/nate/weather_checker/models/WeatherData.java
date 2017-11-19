package com.suver.nate.weather_checker.models;

/**
 * Created by nates on 11/18/2017.
 */

public class WeatherData {
    private float mTemperature;
    private String mCityName;
    private String mImageUrl;

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }



    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String mCityName) {
        this.mCityName = mCityName;
    }



    public float getTemperature() {
        return mTemperature;
    }

    public void setTemperature(float mTemperature) {
        this.mTemperature = mTemperature;
    }
}
