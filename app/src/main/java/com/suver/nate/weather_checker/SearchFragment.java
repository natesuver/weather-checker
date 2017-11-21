package com.suver.nate.weather_checker;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.RadioButton;
import com.suver.nate.weather_checker.api.WundergroundApi;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nates on 11/18/2017.
 */

public class SearchFragment extends Fragment {
    private static final String SearchTypeKey = "SearchTypeKey";
    private static final String StateKey = "StateKey";
    private OnSearchListener mCallback;
    private Button mButton;
    private EditText mEditor;
    private RadioButton mRadioZip;
    private RadioButton mRadioState;
    private Spinner mState;
    private SearchType mSearchType;
    private String mSelectedState;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null) {
            retrieveData(savedInstanceState);
        }
    }
    private void retrieveData(Bundle savedInstanceState) {
        mSearchType = (SearchType)savedInstanceState.getSerializable(SearchTypeKey);
        mSelectedState = savedInstanceState.getString(StateKey);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(SearchTypeKey, mSearchType);
        savedInstanceState.putString(StateKey, mSelectedState);
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
                SetupZipSearch();
            }
        });
        mRadioState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupStateSearch();
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

        if (mSearchType == SearchType.State) {
            SetupStateSearch();
        }
        else {
            SetupZipSearch();
        }
        return v;
    }

    private void SetupZipSearch() {
        mSearchType = SearchType.Zip;
        mState.setVisibility(View.GONE);
        mEditor.setVisibility(View.VISIBLE);
        mButton.setVisibility(View.VISIBLE);
    }

    private void SetupStateSearch() {
        mSearchType = SearchType.State;
        mEditor.setVisibility(View.GONE);
        mState.setVisibility(View.VISIBLE);
        mButton.setVisibility(View.VISIBLE);
        ArrayList states = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.states_array)));
        int position = states.indexOf(mSelectedState);
        if (position!=-1) {
            mState.setSelection(position);
        }

    }

    private void setupStateSpinner(View v) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.states_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mState.setAdapter(adapter);
        mState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedState = mState.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
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
            mResult = api.SearchByText(params[0]);
            return mResult;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mCallback.OnSearch(result, mSearchType);
        }
    }
}
