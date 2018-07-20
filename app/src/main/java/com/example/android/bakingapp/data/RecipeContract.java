package com.example.android.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class RecipeContract {

    public static final String AUTHORITY = "com.example.android.bakingapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class RecipeEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERVES = "serves";
        public static final String COLUMN_INGREDIENTS_JSON = "json_ingredients";
        public static final String COLUMN_STEPS_JSON = "json_steps";
    }
}