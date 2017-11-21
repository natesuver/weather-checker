package com.suver.nate.weather_checker.models;

/**
 * Created by nates on 11/20/2017.
 */

public class City {

    private String mCityName;
    private String mStateName;
    private String mResourceUrl;
    public City(String city, String state, String url) {
        mCityName = city;
        mStateName = state;
        mResourceUrl = url;
    }
    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String mCityName) {
        this.mCityName = mCityName;
    }

    public String getStateName() {
        return mStateName;
    }

    public void setStateName(String mStateName) {
        this.mStateName = mStateName;
    }

    public String getResourceUrl() {
        return mResourceUrl;
    }

    public void setResourceUrl(String mResourceUrl) {
        this.mResourceUrl = mResourceUrl;
    }
}
