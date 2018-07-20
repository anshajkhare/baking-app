package com.example.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StepsFragment extends Fragment {

    private static final String TAG = StepsFragment.class.getSimpleName();
    private static final String SHORT_TAG = "short_description";
    private static final String LONG_TAG = "long_description";
    private static final String URL_TAG = "URL_description";
    private static final String TURL_TAG = "tURL_description";
    private static final String TWO_PANE = "twoPane";
    private final String KEY_RECYCLER_STATE = "recycler_state";

    private static final String KEY_SHORT_DES = "short_des_key";
    private static final String KEY_LONG_DES = "long_des_key";

    static final String KEY_STEPS = "steps_key";

    private RecyclerView stepsRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    Parcelable mRecyclerViewState;
    private StepsAdapter mStepsAdapter;
    String stepJSON;
    boolean mTwoPane;

    private ArrayList<String> shortDes;
    private ArrayList<String> longDes;
    private ArrayList<String> URL;
    private ArrayList<String> tURL;

    public StepsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shortDes = new ArrayList<>();
        longDes = new ArrayList<>();
        URL = new ArrayList<>();
        tURL = new ArrayList<>();
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTwoPane = arguments.getBoolean(TWO_PANE);
            stepJSON = arguments.getString(KEY_STEPS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);

        stepsRecyclerView = rootView.findViewById(R.id.rv_steps);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mStepsAdapter = new StepsAdapter();

        if (savedInstanceState == null) {
            loadData();
            if (mTwoPane)
                updateFragment(0);
        }
        else {
            shortDes = savedInstanceState.getStringArrayList(KEY_SHORT_DES);
            longDes = savedInstanceState.getStringArrayList(KEY_LONG_DES);
            mStepsAdapter.setStepsList(shortDes);
        }

        stepsRecyclerView.setLayoutManager(mLayoutManager);
        stepsRecyclerView.setAdapter(mStepsAdapter);

        mStepsAdapter.setmClickListener(new StepsAdapter.StepsItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (!mTwoPane) {
                    Intent startActivityIntent = new Intent(getContext(), DescriptionActivity.class);
                    startActivityIntent.putStringArrayListExtra(SHORT_TAG, shortDes);
                    startActivityIntent.putStringArrayListExtra(LONG_TAG, longDes);
                    startActivityIntent.putStringArrayListExtra(URL_TAG, URL);
                    startActivityIntent.putStringArrayListExtra(TURL_TAG, tURL);
                    startActivityIntent.putExtra("position", position);
                    startActivityIntent.putExtra(TWO_PANE, mTwoPane);
                    startActivity(startActivityIntent);
                } else
                    updateFragment(position);
            }
        });

        return rootView;
    }

    private void updateFragment(int position) {
        final FragmentManager fragmentManager = getFragmentManager();
        DescriptionFragment mFragment = DescriptionFragment.setValues(shortDes, longDes, URL, tURL, position, mTwoPane);
        fragmentManager.beginTransaction()
                .replace(R.id.description_fragment_container, mFragment).commit();
        TextView stepText = getActivity().findViewById(R.id.current_state);
        String text = "Step " + (position + 1) + ".";
        stepText.setText(text);
    }

    public void loadData() {
        try {
            JSONArray stepsList = new JSONArray(stepJSON);
            int stepLen = stepsList.length();

            for (int i = 0; i < stepLen; i++) {
                JSONObject selectedRecipe = stepsList.getJSONObject(i);
                String temp = selectedRecipe.getString("shortDescription");
                shortDes.add(i, temp);
                String tempLong = selectedRecipe.getString("description");
                longDes.add(i, tempLong);
                String uri;
                if (selectedRecipe.has("videoURL")) {
                    uri = selectedRecipe.getString("videoURL");
                } else {
                    uri = "";
                }
                URL.add(i, uri);
                String tUri;
                if (selectedRecipe.has("thumbnailURL")) {
                    tUri = selectedRecipe.getString("thumbnailURL");
                } else {
                    tUri = "";
                }
                tURL.add(i, tUri);
            }
            mStepsAdapter.setStepsList(shortDes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerViewState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_STATE, mRecyclerViewState);
        outState.putStringArrayList(KEY_SHORT_DES, shortDes);
        outState.putStringArrayList(KEY_LONG_DES, longDes);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            mRecyclerViewState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRecyclerViewState != null) {
            mLayoutManager.onRestoreInstanceState(mRecyclerViewState);
        }
    }

    public static StepsFragment setValues(ArrayList<String> shortDescriptions,
                                          ArrayList<String> longDescriptions,
                                          ArrayList<String> urlDescriptions,
                                          ArrayList<String> turlDescriptions,
                                          String stepJSON,
                                          boolean mTwoPane) {
        StepsFragment fragment = new StepsFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(SHORT_TAG, shortDescriptions);
        bundle.putStringArrayList(LONG_TAG, longDescriptions);
        bundle.putStringArrayList(URL_TAG, urlDescriptions);
        bundle.putStringArrayList(TURL_TAG, turlDescriptions);
        bundle.putString(KEY_STEPS, stepJSON);
        bundle.putBoolean(TWO_PANE, mTwoPane);

        fragment.setArguments(bundle);
        return fragment;
    }
}
