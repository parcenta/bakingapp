package com.peterark.bakingapp.bakingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeIngredientContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeStepContract;
import com.peterark.bakingapp.bakingapp.helperStructures.RecipeIngredient;

/**
 * Created by PETER on 1/11/2017.
 */

public class RecipeDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipesdb.db";
    private static final int VERSION = 1;

    public RecipeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * Called when the tasks database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        RecipeDbUtils.createTable(db, RecipeContract.RecipeEntry.TABLE_NAME);
        RecipeDbUtils.createTable(db, RecipeIngredientContract.RecipeIngredientEntry.TABLE_NAME);
        RecipeDbUtils.createTable(db, RecipeStepContract.RecipeStepEntry.TABLE_NAME);
    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
