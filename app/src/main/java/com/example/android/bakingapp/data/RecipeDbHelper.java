package com.example.android.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bakingapp.data.RecipeContract.RecipeEntry;

/**
 * Created by Khare on 08-Apr-18.
 */

public class RecipeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + RecipeEntry.TABLE_NAME + " (" +
                RecipeEntry._ID                     +   " INTEGER PRIMARY KEY, " +
                RecipeEntry.COLUMN_NAME             +   " TEXT NOT NULL, " +
                RecipeEntry.COLUMN_SERVES           +   " TEXT NOT NULL, " +
                RecipeEntry.COLUMN_INGREDIENTS_JSON +   " TEXT NOT NULL, " +
                RecipeEntry.COLUMN_STEPS_JSON +      " TEXT NOT NULL);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        onCreate(db);
    }
}
