package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.bakingapp.DetailActivity.KEY_ID;
import static com.example.android.bakingapp.DetailActivity.KEY_INGREDIENTS;
import static com.example.android.bakingapp.DetailActivity.KEY_NAME;
import static com.example.android.bakingapp.DetailActivity.KEY_STEPS;

public class DescriptionActivity extends AppCompatActivity {

    private static final String SHORT_TAG = "short_description";
    private static final String LONG_TAG = "long_description";
    private static final String URL_TAG = "URL_description";
    private static final String TURL_TAG = "tURL_description";
    private static final String TWO_PANE = "twoPane";
    private static final String TAG_RETAINED_FRAGMENT_DESCRIPTION = "retained_fragment_description";
    private static final String TAG_RETAINED_FRAGMENT_INGREDIENTS = "retained_fragment_ingredients";
    private static final String TAG_RETAINED_FRAGMENT_STEPS = "retained_fragment_steps";
    private static final String STEP_INDEX = "step_index";
    private static final String TAG = DescriptionActivity.class.getSimpleName();

    @Nullable @BindView(R.id.current_state)TextView mStepCurrent;
    @Nullable @BindView(R.id.previous_button)Button mPrevButton;
    @Nullable @BindView(R.id.next_button)Button mNextButton;

    private static RelativeLayout relativeLayout;

    private DescriptionFragment mDescriptionFragment;
    private IngredientFragment mIngredientFragment;
    private StepsFragment mStepsFragment;
    private ArrayList<String> shortDescriptions;
    private ArrayList<String> longDescriptions;
    private ArrayList<String> urlDescriptions;
    private ArrayList<String> turlDescriptions;

    String ingredientJSON;
    String stepJSON;
    int id;
    String name;
    int serve;

    private int stepPosition;
    private boolean mTwoPane;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        ButterKnife.bind(this);
        relativeLayout = findViewById(R.id.description_navigation_bar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Detail Steps");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        mTwoPane = bundle.getBoolean(TWO_PANE);

        if (mTwoPane) {
            name = bundle.getString(KEY_NAME);
            actionBar.setTitle(name);
            ingredientJSON = bundle.getString(KEY_INGREDIENTS);
            stepJSON = bundle.getString(KEY_STEPS);
            serve = bundle.getInt(KEY_STEPS);
            id = bundle.getInt(KEY_ID);
        }
        else {
            shortDescriptions = bundle.getStringArrayList(SHORT_TAG);
            longDescriptions = bundle.getStringArrayList(LONG_TAG);
            urlDescriptions = bundle.getStringArrayList(URL_TAG);
            turlDescriptions = bundle.getStringArrayList(TURL_TAG);
        }

        if (savedInstanceState == null) {
            stepPosition = bundle.getInt("position");
        } else {
            stepPosition = savedInstanceState.getInt(STEP_INDEX);
        }

        updateCurrentStep(stepPosition);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        mDescriptionFragment = (DescriptionFragment) fragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT_DESCRIPTION);
        mIngredientFragment = (IngredientFragment) fragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT_INGREDIENTS);
        mStepsFragment = (StepsFragment) fragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT_STEPS);

        if (!mTwoPane) {

            if (mPrevButton != null) {
                mPrevButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stepPosition--;
                        mDescriptionFragment = DescriptionFragment.setValues(shortDescriptions, longDescriptions,
                                urlDescriptions, turlDescriptions, stepPosition, mTwoPane);
                        updateCurrentStep(stepPosition);
                        fragmentManager.beginTransaction().replace(R.id.description_fragment_container, mDescriptionFragment, TAG_RETAINED_FRAGMENT_DESCRIPTION).commit();
                    }
                });
            }
            if (mNextButton != null) {
                mNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stepPosition++;
                        mDescriptionFragment = DescriptionFragment.setValues(shortDescriptions, longDescriptions,
                                urlDescriptions, turlDescriptions, stepPosition, mTwoPane);
                        updateCurrentStep(stepPosition);
                        fragmentManager.beginTransaction().replace(R.id.description_fragment_container, mDescriptionFragment, TAG_RETAINED_FRAGMENT_DESCRIPTION).commit();
                    }
                });
            }
            if (mDescriptionFragment == null) {
                mDescriptionFragment = DescriptionFragment.setValues(shortDescriptions, longDescriptions,
                        urlDescriptions, turlDescriptions, stepPosition, mTwoPane);
                fragmentManager.beginTransaction().replace(R.id.description_fragment_container, mDescriptionFragment, TAG_RETAINED_FRAGMENT_DESCRIPTION).commit();
            }
        }

        else {

            if (mIngredientFragment == null) {
                mIngredientFragment = IngredientFragment.setValues(name, ingredientJSON,
                        stepJSON, id, serve, mTwoPane);
                fragmentManager.beginTransaction().replace(R.id.ingredient_list_container, mIngredientFragment, TAG_RETAINED_FRAGMENT_INGREDIENTS).commit();
            }

            if(mStepsFragment == null) {
                mStepsFragment = StepsFragment.setValues(shortDescriptions, longDescriptions,
                        urlDescriptions, turlDescriptions, stepJSON, mTwoPane);
                fragmentManager.beginTransaction().replace(R.id.steps_list_container, mStepsFragment, TAG_RETAINED_FRAGMENT_STEPS).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STEP_INDEX, stepPosition);
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!mTwoPane) {
            // Checking the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                relativeLayout.setVisibility(View.GONE);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                relativeLayout.setVisibility(View.VISIBLE);
            }
        }
    }*/

    public static void showRelativeLayout() {
        relativeLayout.setVisibility(View.VISIBLE);
    }

    public static void hideRelativeLayout() {
        relativeLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateCurrentStep(int stepPosition) {
        if (mStepCurrent != null) {
            String text = "Step " + (stepPosition + 1) + ".";
            mStepCurrent.setText(text);

            if (!mTwoPane) {
                if (stepPosition == 0) {
                    mPrevButton.setVisibility(View.GONE);
                } else if (stepPosition == shortDescriptions.size() - 1) {
                    mNextButton.setVisibility(View.GONE);
                } else {
                    mPrevButton.setVisibility(View.VISIBLE);
                    mNextButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
