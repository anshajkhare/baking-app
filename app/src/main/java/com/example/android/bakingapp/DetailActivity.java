package com.example.android.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.bakingapp.Widget.BakingAppWidget;
import com.example.android.bakingapp.data.RecipeContract;

import static com.example.android.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_INGREDIENTS_JSON;
import static com.example.android.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_NAME;
import static com.example.android.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_SERVES;
import static com.example.android.bakingapp.data.RecipeContract.RecipeEntry.COLUMN_STEPS_JSON;

/**
 * Created by Khare on 08-Apr-18.
 */

public class DetailActivity extends AppCompatActivity{
    private static final String TAG = DetailActivity.class.getSimpleName();

    static final String KEY_INGREDIENTS = "ingredients_key";
    static final String KEY_STEPS = "steps_key";
    static final String KEY_ID = "id_key";
    static final String KEY_NAME = "name_key";
    static final String KEY_SERVES = "serve_key";

    SharedPreferences favoritesPreference;
    boolean setFavorite;
    int id;

    String ingredientJSON;
    String stepJSON;
    String name;
    int serve;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        favoritesPreference = getApplicationContext().getSharedPreferences("favoritesPreference", 0);
        Intent intent = getIntent();
        id = intent.getIntExtra(KEY_ID, 0);
        name = intent.getStringExtra(KEY_NAME);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(name);
        serve = intent.getIntExtra(KEY_SERVES, 0);
        setFavorite = favoritesPreference.getBoolean(name, false);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            IngredientFragment fragment = new IngredientFragment();
            Bundle ingredientBundle = new Bundle();
            intent = getIntent();
            ingredientJSON = intent.getStringExtra(KEY_INGREDIENTS);
            ingredientBundle.putString(KEY_INGREDIENTS, ingredientJSON);
            fragment.setArguments(ingredientBundle);
            transaction.add(R.id.ingredient_list_container, fragment);
            StepsFragment stepsFragment = new StepsFragment();
            Bundle stepsBundle = new Bundle();
            stepJSON = intent.getStringExtra(KEY_STEPS);
            stepsBundle.putString(KEY_STEPS, stepJSON);
            stepsFragment.setArguments(stepsBundle);
            transaction.add(R.id.steps_list_container, stepsFragment);
            transaction.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (setFavorite) {
            menu.findItem(R.id.favorites).setIcon(R.drawable.ic_grade_white_24dp);
        } else {
            menu.findItem(R.id.favorites).setIcon(R.drawable.ic_grade_black_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.favorites:
                SharedPreferences.Editor editor = favoritesPreference.edit();
                if (setFavorite) {
                    Uri uri = RecipeContract.RecipeEntry.CONTENT_URI;
                    uri = ContentUris.withAppendedId(uri, id);
                    String[] selectionArgs = new String[]{name};
                    getContentResolver().delete(uri,null,selectionArgs);
                    editor.putBoolean(name, false);
                    setFavorite = false;
                }
                else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COLUMN_INGREDIENTS_JSON, ingredientJSON);
                    contentValues.put(COLUMN_STEPS_JSON, stepJSON);
                    contentValues.put(COLUMN_NAME, name);
                    contentValues.put(COLUMN_SERVES, serve);

                    Uri uri = getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, contentValues);
                    if(uri != null) {
                        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
                        editor.putBoolean(name, true);
                        setFavorite = true;
                    }
                }
                Intent intent = new Intent(this, BakingAppWidget.class);
                intent.setAction(BakingAppWidget.DATABASE_UPDATE);
                int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), BakingAppWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                //Log.d(TAG, "____________________________ databaseUpdated, sending broadcast now _________");
                sendBroadcast(intent);
                invalidateOptionsMenu();
                editor.apply();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }
}
