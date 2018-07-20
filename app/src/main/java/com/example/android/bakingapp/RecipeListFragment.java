package com.example.android.bakingapp;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.utlities.NetworkUtils;
import com.example.android.bakingapp.utlities.RecipeJsonUtils;

import org.json.JSONArray;

import java.net.URL;


public class RecipeListFragment extends Fragment {


    private static final String TAG = RecipeListFragment.class.getSimpleName();
    private static final String KEY_RECIPE_NAMES = "recipe_names";
    private static final String KEY_SERVING_VALUES = "serving_values";
    private static final String KEY_ID_VALUES = "id_values";
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static final String TWO_PANE = "twoPane";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecipeAdapter mAdapter;
    Parcelable mRecyclerViewState;
    private int[] idValues = null;
    private String[] recipeNames = null;
    private int[] servingValues = null;
    boolean mTwoPane;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTwoPane = arguments.getBoolean(TWO_PANE);
        }
    }


    public RecipeListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        mRecyclerView = rootView.findViewById(R.id.rv_recipe_list);
        if (!mTwoPane)
            mLayoutManager = new LinearLayoutManager(getActivity());
        else
            mLayoutManager = new GridLayoutManager(getActivity(), 3);

        if (savedInstanceState == null) {
            new FetchRecipeTask().execute();
        }

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecipeAdapter();
        mRecyclerView.setAdapter(mAdapter);

        setClickRecipe();

        return rootView;
    }

    private void setClickRecipe() {
        mAdapter.setmClickListener(new RecipeAdapter.RecipeClickListener() {
            @Override
            public void onRecipeClick(View view, int position) {

                JSONArray ingredientsListJSONArray = RecipeJsonUtils.getIngredientsList()[position];
                JSONArray stepsListJSONArray = RecipeJsonUtils.getStepsList()[position];

                if (!mTwoPane) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(DetailActivity.KEY_INGREDIENTS, ingredientsListJSONArray.toString());
                    intent.putExtra(DetailActivity.KEY_STEPS, stepsListJSONArray.toString());
                    intent.putExtra(DetailActivity.KEY_ID, idValues[position]);
                    intent.putExtra(DetailActivity.KEY_NAME, RecipeJsonUtils.getRecipeName()[position]);
                    intent.putExtra(DetailActivity.KEY_SERVES, RecipeJsonUtils.getServingValue()[position]);
                    startActivity(intent);
                }

                else {
                    Intent intent = new Intent(getActivity(), DescriptionActivity.class);
                    intent.putExtra(DetailActivity.KEY_INGREDIENTS, ingredientsListJSONArray.toString());
                    intent.putExtra(DetailActivity.KEY_STEPS, stepsListJSONArray.toString());
                    intent.putExtra(DetailActivity.KEY_ID, idValues[position]);
                    intent.putExtra(DetailActivity.KEY_NAME, RecipeJsonUtils.getRecipeName()[position]);
                    intent.putExtra(DetailActivity.KEY_SERVES, RecipeJsonUtils.getServingValue()[position]);
                    intent.putExtra(TWO_PANE, mTwoPane);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerViewState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_STATE, mRecyclerViewState);
        outState.putStringArray(KEY_RECIPE_NAMES, recipeNames);
        outState.putIntArray(KEY_SERVING_VALUES, servingValues);
        outState.putIntArray(KEY_ID_VALUES, idValues);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mRecyclerViewState = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
            recipeNames = savedInstanceState.getStringArray(KEY_RECIPE_NAMES);
            servingValues = savedInstanceState.getIntArray(KEY_SERVING_VALUES);
            idValues = savedInstanceState.getIntArray(KEY_ID_VALUES);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRecyclerViewState != null) {
            mLayoutManager.onRestoreInstanceState(mRecyclerViewState);
            mAdapter.setRecipeList(recipeNames, servingValues);
        }
    }

    public class FetchRecipeTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {
            MainActivity.showProgressBar();
            MainActivity.hideErrorMessage();
            MainActivity.hideData();
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            URL recipeListRequestUrl = NetworkUtils.buildUrl();
            try {
                String jsonRecipeResponse = NetworkUtils.getResponseFromHttpUrl(recipeListRequestUrl);
                RecipeJsonUtils
                        .getRecipeDetailsFromJson(getContext(), jsonRecipeResponse);
                idValues = RecipeJsonUtils.getIdValues();
                return RecipeJsonUtils.getRecipeName();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            MainActivity.hideProgressBar();
            if (strings != null) {
                recipeNames = strings;
                servingValues = RecipeJsonUtils.getServingValue();
                mAdapter.setRecipeList(recipeNames, servingValues);
                MainActivity.hideErrorMessage();
                MainActivity.showData();
            }
            else {
                MainActivity.hideData();
                MainActivity.showErrorMessage();
            }
        }
    }
}
