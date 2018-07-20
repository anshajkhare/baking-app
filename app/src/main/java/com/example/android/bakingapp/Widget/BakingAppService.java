package com.example.android.bakingapp.Widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.bakingapp.data.RecipeContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.android.bakingapp.data.RecipeContract.BASE_CONTENT_URI;
import static com.example.android.bakingapp.data.RecipeContract.PATH_FAVORITES;

/**
 * Created by Khare on 08-Apr-18.
 */

public class BakingAppService extends IntentService {

    public static final String ACTION_UPDATE_RECIPE_WIDGETS = "com.example.android.bakingapp.action.update_recipe_widgets";
    private static final String TAG = BakingAppService.class.getSimpleName();


    public BakingAppService() {
        super("BakingAppService");
    }

    public static void startActionShowRecipe(Context context) {
        //Log.d(TAG, "_________________________ startActionShowRecipe called _____________________");
        Intent intent = new Intent(context, BakingAppService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Log.d(TAG, "_________________________ onHandleIntent called _____________________");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_RECIPE_WIDGETS.equals(action)) {
                handleActionUpdate();
            }
        }
    }

    private void handleActionUpdate() {
        //Log.d(TAG, "_________________________ handleActionUpdate called _____________________");
        try {
            Uri RECIPE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
            //Log.d(TAG, "_________________________ querying database... _____________________");
            Cursor cursor = getContentResolver().query(
                    RECIPE_URI,
                    null,
                    null,
                    null,
                    null
            );
            ArrayList<String> concat = new ArrayList<>();
            String name = "";
            if (cursor.getCount() < 1) {
                //Log.d(TAG, "_________________________ cursor was **NULL** _____________________");
                cursor.close();
            } else {
                cursor.moveToFirst();
                name = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME));
                String ingredientJSON = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS_JSON));
                JSONArray ingredientList = new JSONArray(ingredientJSON);
                int inLen = ingredientList.length();
                int[] quantity = new int[inLen];
                String[] measure = new String[inLen];
                String[] ingredient = new String[inLen];

                for (int i = 0; i < inLen; i++) {
                    JSONObject selectedRecipe = ingredientList.getJSONObject(i);

                    quantity[i] = selectedRecipe.getInt("quantity");
                    measure[i] = selectedRecipe.getString("measure");
                    ingredient[i] = selectedRecipe.getString("ingredient");
                    String text = quantity[i] + " " + measure[i] + " of " + ingredient[i];
                    //Log.d(TAG, "_________________________ adding " + text + " to concat _____________________");
                    concat.add(i, text);
                }
            }
            Intent intent = new Intent(this, BakingAppWidget.class);
            intent.setAction(BakingAppWidget.RECIPE_ITEM_FETCH);
            intent.putExtra("text", concat);
            intent.putExtra("name", name);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingAppWidget.class));
            intent.putExtra("appWidgetIds", appWidgetIds);
            //Log.d(TAG, "_________________________ sending Broadcast now ... _____________________");
            sendBroadcast(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
