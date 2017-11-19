package com.suver.nate.weather_checker.api;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.suver.nate.weather_checker.R;
import org.json.JSONObject;

/**
 * Created by nates on 11/18/2017.  A class that sends a request to the weather underground API, and returns a JSON object that contains current conditions.
 */

public class WundergroundApi extends BaseApi {
    private static final String LOG = "Api";
    Context mContext;
    String mBaseApiUrl;
    String mApiKey;
    public WundergroundApi(Context context) {
        mContext = context;
        mBaseApiUrl = context.getString(R.string.base_api_url);
        mApiKey = context.getString(R.string.api_key); //this is my personal key, plz don't share :)
    }

    public JSONObject SearchByZip(String zipcode) {
        String url = mBaseApiUrl.replace("{0}",mApiKey).replace("{1}", BuildParm(zipcode));
        return ExecuteRequest(url,"application/json","");
    }

    @Nullable
    public Bitmap GetImage(String imageUrl) {
        return ExecuteBitmapRequest(imageUrl);
    }

}
