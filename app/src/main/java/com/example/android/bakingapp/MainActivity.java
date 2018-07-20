package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.IdlingResource.SimpleIdlingResource;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements RecipeDownloader.DelayerCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TWO_PANE = "twoPane";
    private static final String KEY_RECIPE_LIST_FRAGMENT = "RecipeListFragment";

    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private boolean mTwoPane;

    RelativeLayout relativeLayout;
    private static FrameLayout frameLayout;
    private static ProgressBar progressBar;
    private static TextView errorMessageView;

    SimpleIdlingResource mSimpleIdlingResource;
    Toast toast;

    RecipeListFragment recipeListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        relativeLayout = findViewById(R.id.tablet_view);
        frameLayout = findViewById(R.id.recipe_list_container);
        progressBar = findViewById(R.id.pb_loading_indicator);
        errorMessageView = findViewById(R.id.error_message_display);

        if (relativeLayout != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
        getmSimpleIdlingResource();
        Bundle args = new Bundle();
        args.putBoolean(TWO_PANE, mTwoPane);

        if (savedInstanceState == null) {
            String text = "Loading Recipes";
            int duration = Toast.LENGTH_LONG;
            toast = Toast.makeText(this, text, duration);
            toast.show();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            recipeListFragment = new RecipeListFragment();
            recipeListFragment.setArguments(args);
            transaction.add(R.id.recipe_list_container, recipeListFragment);
            transaction.commit();
        }
        else {
            recipeListFragment = (RecipeListFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, KEY_RECIPE_LIST_FRAGMENT);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, KEY_RECIPE_LIST_FRAGMENT, recipeListFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecipeDownloader.downLoadRecipe(this, mSimpleIdlingResource);
    }

    @Override
    public void onDone(boolean isDone) {
        if(isDone) {
            if (toast != null)
                toast.cancel();
        }
    }

    @VisibleForTesting
    @NonNull
    public SimpleIdlingResource getmSimpleIdlingResource() {
        if (mSimpleIdlingResource == null) {
            mSimpleIdlingResource = new SimpleIdlingResource();
        }
        return mSimpleIdlingResource;
    }

    public static void showErrorMessage() {
        errorMessageView.setVisibility(View.VISIBLE);
    }

    public static void hideErrorMessage() {
        errorMessageView.setVisibility(View.INVISIBLE);
    }

    public static void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public static void showData() {
        frameLayout.setVisibility(View.VISIBLE);
    }

    public static void hideData() {
        frameLayout.setVisibility(View.INVISIBLE);
    }
}
