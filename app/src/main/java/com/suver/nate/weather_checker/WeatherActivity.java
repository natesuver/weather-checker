package com.suver.nate.weather_checker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONObject;

public class WeatherActivity extends AppCompatActivity implements OnSearchListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.search_container);
        if (frag==null) {
            frag = new SearchFragment();
            fm.beginTransaction().add(R.id.search_container,frag).commit();
        }
        frag = fm.findFragmentById(R.id.result_container);
        if (frag==null) {
            frag = new ResultFragment();
            fm.beginTransaction().add(R.id.result_container,frag).commit();
        }

    }

    @Override
    public void OnSearch(JSONObject result) {
        ResultFragment r = (ResultFragment) getSupportFragmentManager().findFragmentById(R.id.result_container);
        r.SetResult(result);
    }
}
