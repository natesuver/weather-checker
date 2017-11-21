package com.suver.nate.weather_checker;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.suver.nate.weather_checker.api.WundergroundApi;
import com.suver.nate.weather_checker.models.City;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nates on 11/19/2017.
 */

public class StateResultFragment extends Fragment implements WeatherResult {
    private OnSearchListener mCallback;
    private static final String instance_key = "stateresult";
    private RecyclerView mRecycleView;
    private String mLastResult;
    private List<City> mCities;
    private CityAdapter mAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            retrieveData(savedInstanceState);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        WeatherActivity a;
        if (context instanceof WeatherActivity){
            a=(WeatherActivity) context;
            try {
                mCallback = (OnSearchListener) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString() + " must implement OnSearchListener");
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stateresult,container,false);
        mRecycleView = (RecyclerView) v.findViewById(R.id.recycler);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (mLastResult!=null) {
            try {
                SetResult(new JSONObject(mLastResult));
            }
            catch (Exception ex) {
                Toast.makeText(getActivity(),R.string.invalid_data,Toast.LENGTH_SHORT).show();
            }
        }
        updateUI();
        return v;
    }

    private void updateUI() {
        if (mLastResult!=null) {
            mAdapter = new CityAdapter(mCities);
            mRecycleView.setAdapter(mAdapter);
        }
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
        try {
            mLastResult = result.toString();
            mCities = new ArrayList<City>();
            JSONObject obs = result.getJSONObject("response");
            JSONArray results = obs.getJSONArray("results");
            for(int i=0; i<results.length(); i++) {
                JSONObject c = results.getJSONObject(i);
                City city = new City(c.getString("city"),c.getString("state"),c.getString("l"));
                mCities.add(city);
            }
        } catch (Exception ex) {
            //a bit hacky, since this assumes any serialization error was the result of an invalid zip (which is pretty likely)
            Toast.makeText(getActivity(),R.string.invalid_data,Toast.LENGTH_SHORT).show();

        }
    }

    private class CityHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mCity;
        private TextView mState;
        private City mCityData;
        public CityHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_city, parent, false  ));
            itemView.setOnClickListener(this);
            mCity = itemView.findViewById(R.id.cityText);
            mState = itemView.findViewById(R.id.stateText);
        }
        public void bind(City city) {
            mCityData = city;
            mCity.setText(mCityData.getCityName());
            mState.setText(mCityData.getStateName());
        }

        @Override
        public void onClick(View view) {
            new Search(getActivity().getBaseContext()).execute(mCityData.getResourceUrl());
        }
    }

    private class CityAdapter extends RecyclerView.Adapter<CityHolder> {
        private List<City> mCities;
        public CityAdapter(List<City> cities) {
            mCities = cities;
        }

        @Override
        public CityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CityHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(CityHolder holder, int position) {
            City city = mCities.get(position);
            holder.bind(city);
        }
        @Override
        public int getItemCount() {
            return mCities.size();
        }
    }

    private class Search extends AsyncTask<String, Void, JSONObject> {

        private Context mContext;
        private JSONObject mResult;
        public Search (Context context){
            mContext = context;
        }
        @Override
        protected JSONObject doInBackground(String... params){
            WundergroundApi api = new WundergroundApi(mContext);
            mResult = api.SearchByZmwCode(params[0]);
            return mResult;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mCallback.OnSearch(result, SearchType.Zip);
        }
    }
}
