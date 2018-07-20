package com.example.android.bakingapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
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

public class ListViewService extends RemoteViewsService {
    private static final String TAG_OUT = ListViewService.class.getSimpleName();
    private boolean isEmpty = false;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //Log.d(TAG_OUT, "_________________________ onGetViewFactory _____________________");
        return new ListviewRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ListviewRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private final String TAG = ListviewRemoteViewsFactory.class.getSimpleName();

        private Context mContext;
        private Intent mIntent;
        private ArrayList<String> records;
        private int count;

        public ListviewRemoteViewsFactory(Context applicationContext, Intent intent) {
            //Log.d(TAG, "_________________________ ListViewRemoteViewsFactory constructor called _____________________");
            mContext = applicationContext;
            mIntent = intent;
        }

        @Override
        public void onCreate() {
            //Log.d(TAG, "_________________________ onCreate called _____________________");
            records = mIntent.getStringArrayListExtra("text");
            count = records.size();
        }

        @Override
        public void onDataSetChanged() {
            //Log.d(TAG, "_________________________ onDataSetChanged called _____________________");
            Thread thread = new Thread() {
                public void run() {
                    query();
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }

        private void query() {
            try {
                Uri RECIPE_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
                //Log.d(TAG, "_________________________ querying database... _____________________");
                Cursor cursor = mContext.getContentResolver().query(
                        RECIPE_URI,
                        null,
                        null,
                        null,
                        null
                );
                ArrayList<String> concat = new ArrayList<>();
                if (cursor.getCount() < 1) {
                    Log.d(TAG, "_________________________ cursor was **NULL** _____________________");
                    cursor.close();
                } else {
                    cursor.moveToFirst();
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
                records = concat;
                count = records.size();
                /*if (records.isEmpty()) {
                    Log.d(TAG, "___________________ listview arraylist is empty__________");
                } else {
                    Log.d(TAG, "___________________ listview arraylist is NOT**** empty__________");
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            Log.d(TAG, "_________________________ getCount called, value is " + records.size() + " _____________________");
            return count;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.d(TAG, "_________________________ getView at position " + position + "  called _____________________");
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            String data = records.get(position);
            Log.d(TAG, "_________________________ setting text to " + data + "   _____________________");
            rv.setTextViewText(R.id.item, data);
            //Bundle extras = new Bundle();

            //extras.putInt(BakingAppWidget.EXTRA_ITEM, position);

            //Intent fillInIntent = new Intent();

           // fillInIntent.putExtra("recipe_ingredient",data);

            //fillInIntent.putExtras(extras);

            // Make it possible to distinguish the individual on-click

            // action of a given item

            //rv.setOnClickFillInIntent(R.id.appwidget_text, fillInIntent);

            // Return the RemoteViews object.

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
