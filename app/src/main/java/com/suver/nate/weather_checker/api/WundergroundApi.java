package com.suver.nate.weather_checker.api;
import android.content.Context;
import com.suver.nate.weather_checker.R;
import org.json.JSONObject;

/**
 * Created by nates on 11/18/2017.
 */

public class WundergroundApi extends BaseApi {
    private static final String LOG = "Api";
    Context mContext;
    String mBaseApiUrl;
    String mApiKey;
    public WundergroundApi(Context context) {
        mContext = context;
        mBaseApiUrl = context.getString(R.string.base_api_url);
        mApiKey = context.getString(R.string.api_key);
    }

    public JSONObject SearchByZip(String zipcode) {
        String url = mBaseApiUrl.replace("{0}",mApiKey).replace("{1}", BuildParm(zipcode));
        return ExecuteRequest(url,"application/json","");
    }

}
