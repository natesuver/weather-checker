package com.suver.nate.weather_checker;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.RadioButton;
import com.suver.nate.weather_checker.api.WundergroundApi;

import org.json.JSONObject;

/**
 * Created by nates on 11/18/2017.
 */

public class SearchFragment extends Fragment {
    private OnSearchListener mCallback;
    private Button mButton;
    private EditText mEditor;
    private RadioButton mRadioZip;
    private RadioButton mRadioState;
    private Spinner mState;
    private SearchType mSearchType;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //no need to save instance state here, since the only data we keep is in the EditText, which preserves it by default
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
        View v = inflater.inflate(R.layout.fragment_search,container,false);
        mButton = v.findViewById(R.id.zip_search);
        mEditor = v.findViewById(R.id.zip_text);
        mRadioZip = v.findViewById(R.id.radioZip);
        mRadioState = v.findViewById(R.id.radioState);
        mState = v.findViewById(R.id.state_spinner);

        mRadioZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchType = SearchType.Zip;
                mState.setVisibility(View.GONE);
                mEditor.setVisibility(View.VISIBLE);
                mButton.setVisibility(View.VISIBLE);
            }
        });
        mRadioState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchType = SearchType.State;
                mEditor.setVisibility(View.GONE);
                mState.setVisibility(View.VISIBLE);
                mButton.setVisibility(View.VISIBLE);
            }
        });

        setupStateSpinner(v);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setError(null);
                String search;
                if (mSearchType == SearchType.Zip) {
                    search = mEditor.getText().toString();
                } else {
                    search = mState.getSelectedItem().toString();
                }

                if (mSearchType == SearchType.Zip && search.length()==0) {
                    mEditor.setError(getText(R.string.search_validation_error));
                    return;
                }
                new Search(getActivity().getBaseContext()).execute(search);
            }
        });


        return v;
    }

    private void setupStateSpinner(View v) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mState.setAdapter(adapter);
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
            mResult = api.SearchByZip(params[0]);
            return mResult;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mCallback.OnSearch(result, mSearchType);
        }
    }
}
