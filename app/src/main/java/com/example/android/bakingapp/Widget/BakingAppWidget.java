package com.example.android.bakingapp.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.bakingapp.R;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class BakingAppWidget extends AppWidgetProvider {

    private static String TAG = BakingAppWidget.class.getSimpleName();

    public static final String RECIPE_ITEM_FETCH = "com.example.android.bakingapp.RECIPE_ITEM";
    public static final String DATABASE_UPDATE = "com.example.android.bakingapp.DATABASE_UPDATE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, ArrayList<String> ingredientsList, String name) {

        //Log.d(TAG, "_________________________ updateAppWidget called _____________________");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_app_widget);

        Intent intent = new Intent(context, ListViewService.class);
        intent.putExtra("text", ingredientsList);

        views.setRemoteAdapter(R.id.appwidget_listView, intent);

        views.setTextViewText(R.id.appwidget_tv_name, name);

        views.setEmptyView(R.id.appwidget_listView, R.id.empty_view);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        //for (int appWidgetId : appWidgetIds) {
        // updateAppWidget(context, appWidgetManager, appWidgetId);
        //Log.d(TAG, "_________________________ onUpdate called _____________________");
        BakingAppService.startActionShowRecipe(context);
        //}
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(TAG, "_________________________ onReceive called _____________________");
        super.onReceive(context, intent);
        if (intent.getAction().equals(RECIPE_ITEM_FETCH)) {
            Log.d(TAG, "_______________ case is RECIPE_ITEM_FETCH _______________");
            ArrayList<String> list = intent.getStringArrayListExtra("text");
            String name = intent.getStringExtra("name");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = intent.getIntArrayExtra("appWidgetIds");
            updateAppRecipeWidget(context, appWidgetManager, appWidgetIds, list, name);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_listView);
        }
        else if(intent.getAction().equals(DATABASE_UPDATE)) {
            //Log.d(TAG, "_______________ case is DATABASE_UPDATE _______________");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_ID);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
    }

    public static void updateAppRecipeWidget(Context context,
                                             AppWidgetManager appWidgetManager, int[] appWidgetIds,
                                             ArrayList<String> ingredientsList, String name) {
        //Log.d(TAG, "_________________________ updateAppRecipeWidget called _____________________");
        for (int appWidgetId: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, ingredientsList, name);
        }
    }
}

