package com.suver.nate.weather_checker;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suver.nate.weather_checker.api.WundergroundApi;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by nates on 11/18/2017.
 */

public class ResultFragment extends Fragment implements WeatherResult {
    private static final String instance_key = "result";
    private TextView mLocation;
    private TextView mTemperature;
    private TextView mConditions;
    private TextView mNiceOut;
    private ImageView mImage;
    private String mLastResult;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            retrieveData(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_result,container,false);
        mLocation = v.findViewById(R.id.locationValue);
        mTemperature = v.findViewById(R.id.temperatureValue);
        mConditions = v.findViewById(R.id.conditionsValue);
        mNiceOut = v.findViewById(R.id.niceOutLabel);
        mImage = v.findViewById(R.id.image);
        if (mLastResult!=null) {
            try {
                SetResult(new JSONObject(mLastResult));
            }
            catch (Exception ex) {
                Toast.makeText(getActivity(),R.string.invalid_data,Toast.LENGTH_SHORT).show();
            }
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(instance_key,mLastResult);
    }

    private void retrieveData(Bundle savedInstanceState) {
        mLastResult = savedInstanceState.getString(instance_key);
    }

    public void SetResult(String result) {
        mLastResult = result.toString();
    }
    public void SetResult(JSONObject result) {
        try { //todo: refactor to model, maybe using GSON
            mLastResult = result.toString();
            JSONObject obs = result.getJSONObject("current_observation");
            mTemperature.setText(obs.getString("temperature_string"));
            JSONObject display = obs.getJSONObject("display_location");
            mLocation.setText(display.getString("full"));
            mConditions.setText(obs.getString("weather"));
            mNiceOut.setText(Niceness(obs.getDouble("temp_f")));
            String imageUrl = obs.getString("icon_url");
            new GetImage(getActivity()).execute(imageUrl);

        } catch (Exception ex) {
            //a bit hacky, since this assumes any serialization error was the result of an invalid zip (which is pretty likely)
            Toast.makeText(getActivity(),R.string.invalid_zip,Toast.LENGTH_SHORT).show();
            mTemperature.setText(null);
            mLocation.setText(null);
            mConditions.setText(null);
            mNiceOut.setText(null);
            mImage.setImageBitmap(null);
        }
    }

    private Integer Niceness(Double temperature) {
        if (temperature < 32) {return R.string.result_freezing;}
        if (temperature>=32 && temperature <68) {return R.string.result_cold;}
        if (temperature>=68 && temperature <78) {return R.string.result_nice;}
        else {return R.string.result_hot;}
    }
    //Did not want to get lock up the main thread with grabbing the image, so I made it async.
    private class GetImage extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private JSONObject mResult;
        public GetImage (Context context){
            mContext = context;
        }
        @Override
        protected Bitmap doInBackground(String... params){
            WundergroundApi api = new WundergroundApi(mContext);
            return api.GetImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mImage.setImageBitmap(result);
        }
    }
}
