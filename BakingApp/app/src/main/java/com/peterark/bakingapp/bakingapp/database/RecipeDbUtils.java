package com.peterark.bakingapp.bakingapp.database;

import android.database.sqlite.SQLiteDatabase;

import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeIngredientContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeStepContract;

/**
 * Created by PETER on 1/11/2017.
 */

public class RecipeDbUtils {

    static void createTable(SQLiteDatabase db, String tableName){

        String CREATE_TABLE_SQL = null;

        switch (tableName){
            case RecipeContract.RecipeEntry.TABLE_NAME:
                CREATE_TABLE_SQL = "CREATE TABLE "  + RecipeContract.RecipeEntry.TABLE_NAME + " (" +
                        RecipeContract.RecipeEntry._ID                      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RecipeContract.RecipeEntry.COLUMN_RECIPE_ID         + " INTEGER NOT NULL, " +
                        RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME       + " TEXT NOT NULL," +
                        RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS   + " INTEGER NOT NULL," +
                        "CONSTRAINT constraint_"+RecipeContract.RecipeEntry.COLUMN_RECIPE_ID+" UNIQUE ("+RecipeContract.RecipeEntry.COLUMN_RECIPE_ID+"));";
                break;
            case RecipeIngredientContract.RecipeIngredientEntry.TABLE_NAME:
                CREATE_TABLE_SQL = "CREATE TABLE "  + RecipeIngredientContract.RecipeIngredientEntry.TABLE_NAME + " (" +
                        RecipeIngredientContract.RecipeIngredientEntry._ID                                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_ID                     + " INTEGER NOT NULL, " +
                        RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_NAME        + " TEXT NOT NULL," +
                        RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_QUANTITY    + " REAL NOT NULL," +
                        RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_MEASURE     + " TEXT NOT NULL);";
                break;
            case RecipeStepContract.RecipeStepEntry.TABLE_NAME:
                CREATE_TABLE_SQL = "CREATE TABLE "  + RecipeStepContract.RecipeStepEntry.TABLE_NAME + " (" +
                        RecipeStepContract.RecipeStepEntry._ID                                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_ID                     + " INTEGER NOT NULL, " +
                        RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID                + " INTEGER NOT NULL," +
                        RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_SHORT_DESCRIPTION + " TEXT NOT NULL," +
                        RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_DESCRIPTION       + " TEXT NOT NULL," +
                        RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_VIDEO_URL         + " TEXT NOT NULL," +
                        RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_THUMBNAIL_URL     + " TEXT NOT NULL);";
                break;
        }

        if (CREATE_TABLE_SQL != null)
            db.execSQL(CREATE_TABLE_SQL);
    }
}
