package com.example.android.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.Widget.BakingAppWidget;
import com.example.android.bakingapp.data.RecipeContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.android.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_INGREDIENTS_JSON;
import static com.example.android.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_NAME;
import static com.example.android.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_SERVES;
import static com.example.android.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_STEPS_JSON;

public class IngredientFragment extends Fragment {

    private static final String TAG = IngredientFragment.class.getSimpleName();
    private static final String TWO_PANE = "twoPane";
    private final String KEY_RECYCLER_STATE = "recycler_state";

    private RecyclerView ingredientRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private IngredientsAdapter mIngredientsAdapter;
    Parcelable mRecyclerViewState;
    SharedPreferences favoritesPreference;

    static final String KEY_INGREDIENTS = "ingredients_key";
    static final String KEY_STEPS = "steps_key";
    static final String KEY_ID = "id_key";
    static final String KEY_NAME = "name_key";
    static final String KEY_SERVES = "serve_key";

    String ingredientJSON;
    String stepJSON;
    int id;
    String name;
    int serve;
    boolean setFavorite;
    private String[] measure;
    private String[] ingredient;
    private int[] quantity;
    boolean mTwoPane;

    public IngredientFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mTwoPane = arguments.getBoolean(TWO_PANE);
            ingredientJSON = arguments.getString(KEY_INGREDIENTS);
            if (mTwoPane) {
                name = arguments.getString(KEY_NAME);
                stepJSON = arguments.getString(KEY_STEPS);
                id = arguments.getInt(KEY_ID, 0);
                serve = arguments.getInt(KEY_SERVES, 0);
            }
        }
        if (mTwoPane) {
            favoritesPreference = getContext().getSharedPreferences("favoritesPreference", 0);
            setFavorite = favoritesPreference.getBoolean(name, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredient, container, false);

        ingredientRecyclerView = rootView.findViewById(R.id.rv_ingredients);
        mLayoutManager = new LinearLayoutManager(getActivity());
        ingredientRecyclerView.setLayoutManager(mLayoutManager);

        mIngredientsAdapter = new IngredientsAdapter();
        ingredientRecyclerView.setAdapter(mIngredientsAdapter);

        loadData();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(mTwoPane) {
            inflater.inflate(R.menu.main, menu);
            if (setFavorite) {
                menu.findItem(R.id.favorites).setIcon(R.drawable.ic_grade_white_24dp);
            } else {
                menu.findItem(R.id.favorites).setIcon(R.drawable.ic_grade_black_24dp);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mTwoPane) {
            switch (item.getItemId()) {
                case R.id.favorites:
                    SharedPreferences.Editor editor = favoritesPreference.edit();
                    if (setFavorite) {
                        Uri uri = RecipeContract.RecipeEntry.CONTENT_URI;
                        uri = ContentUris.withAppendedId(uri, id);
                        String[] selectionArgs = new String[]{name};
                        getContext().getContentResolver().delete(uri,null,selectionArgs);
                        editor.putBoolean(name, false);
                        setFavorite = false;
                    }
                    else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(COLUMN_INGREDIENTS_JSON, ingredientJSON);
                        contentValues.put(COLUMN_STEPS_JSON, stepJSON);
                        contentValues.put(COLUMN_NAME, name);
                        contentValues.put(COLUMN_SERVES, serve);

                        Uri uri = getContext().getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, contentValues);
                        if(uri != null) {
                            editor.putBoolean(name, true);
                            setFavorite = true;
                        }
                    }
                    Intent intent = new Intent(getContext(), BakingAppWidget.class);
                    intent.setAction(BakingAppWidget.DATABASE_UPDATE);
                    int ids[] = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), BakingAppWidget.class));
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                    getActivity().sendBroadcast(intent);
                    getActivity().invalidateOptionsMenu();
                    editor.apply();
            }
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void loadData() {
        try {
            JSONArray ingredientList = new JSONArray(ingredientJSON);
            int inLen = ingredientList.length();
            quantity = new int[inLen];
            measure = new String[inLen];
            ingredient = new String[inLen];

            for (int i = 0; i < inLen; i++) {
                JSONObject selectedRecipe = ingredientList.getJSONObject(i);

                quantity[i] = selectedRecipe.getInt("quantity");
                measure[i] = selectedRecipe.getString("measure");
                ingredient[i] = selectedRecipe.getString("ingredient");
            }
            mIngredientsAdapter.setIngredientList(quantity, measure, ingredient);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerViewState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_STATE, mRecyclerViewState);
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

    public static IngredientFragment setValues(String name,
                                               String ingredientJSON,
                                               String stepJSON,
                                               int id, int serve, boolean mTwoPane) {

        IngredientFragment fragment = new IngredientFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY_NAME, name);
        bundle.putString(KEY_INGREDIENTS, ingredientJSON);
        bundle.putString(KEY_STEPS, stepJSON);
        bundle.putInt(KEY_ID, id);
        bundle.putInt(KEY_SERVES, serve);
        bundle.putBoolean(TWO_PANE, mTwoPane);

        fragment.setArguments(bundle);
        return fragment;
    }
}
