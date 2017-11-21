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
        CreateSearchFragment(fm);
    }

    private void CreateSearchFragment(FragmentManager fm) {
        Fragment frag = fm.findFragmentById(R.id.search_container);
        if (frag==null) {
            frag = new SearchFragment();
            fm.beginTransaction().add(R.id.search_container,frag).commit();
        }
    }

    @Override
    public void OnSearch(JSONObject result, SearchType type) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragResult = fm.findFragmentById(R.id.result_container);
        if (fragResult!=null) {
            fm.beginTransaction().remove(fragResult).commit();
        }
        Fragment frag;
        if (type==SearchType.State) {
            frag = new StateResultFragment();
        } else {
            frag = new ResultFragment();
        }
        fm.beginTransaction().add(R.id.result_container, frag).commit();
        WeatherResult wr = (WeatherResult) frag;
        wr.SetResult(result.toString());
    }
}
