package com.peterark.bakingapp.bakingapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.peterark.bakingapp.bakingapp.database.RecipeDBHelper;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeIngredientContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeStepContract;
import com.peterark.bakingapp.bakingapp.helperStructures.RecipeStep;

/**
 * Created by PETER on 1/11/2017.
 */

public class RecipeProvider extends ContentProvider {

    private static final String RECIPES_TABLE_NAME = RecipeContract.RecipeEntry.TABLE_NAME;
    private static final String RECIPES_INGREDIENTS_TABLE_NAME = RecipeIngredientContract.RecipeIngredientEntry.TABLE_NAME;
    private static final String RECIPES_STEPS_TABLE_NAME = RecipeStepContract.RecipeStepEntry.TABLE_NAME;

    private static final int CODE_RECIPES           = 1000;
    private static final int CODE_RECIPE_INGREDIENTS = 2000;
    private static final int CODE_RECIPE_STEPS       = 3000;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private RecipeDBHelper mDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Addking Recipes to uri matcher
        matcher.addURI(RecipeContract.CONTENT_AUTHORITY, RecipeContract.PATH_RECIPES, CODE_RECIPES);

        // Adding Recipe Ingredients to uri matcher.
        matcher.addURI(RecipeIngredientContract.CONTENT_AUTHORITY, RecipeIngredientContract.PATH_RECIPE_INGREDIENTS, CODE_RECIPE_INGREDIENTS);

        // Adding Recipe Steps to uri matcher.
        matcher.addURI(RecipeStepContract.CONTENT_AUTHORITY, RecipeStepContract.PATH_RECIPE_STEPS, CODE_RECIPE_STEPS);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new RecipeDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            case CODE_RECIPES: {

                cursor = mDbHelper.getReadableDatabase().query(
                        RECIPES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_RECIPE_INGREDIENTS: {

                cursor = mDbHelper.getReadableDatabase().query(
                        RECIPES_INGREDIENTS_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case CODE_RECIPE_STEPS: {

                cursor = mDbHelper.getReadableDatabase().query(
                        RECIPES_STEPS_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase mDb = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;
        long id;

        switch (match){
            case CODE_RECIPES:
                id = mDb.insert(RECIPES_TABLE_NAME,null,values);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(RecipeContract.RecipeEntry.CONTENT_URI,id);
                else
                    throw new SQLException("Failed to insert row " + uri);
                break;
            case CODE_RECIPE_INGREDIENTS:
                id = mDb.insert(RECIPES_INGREDIENTS_TABLE_NAME,null,values);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(RecipeIngredientContract.RecipeIngredientEntry.CONTENT_URI,id);
                else
                    throw new SQLException("Failed to insert row " + uri);
                break;
            case CODE_RECIPE_STEPS:
                id = mDb.insert(RECIPES_STEPS_TABLE_NAME,null,values);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(RecipeStepContract.RecipeStepEntry.CONTENT_URI,id);
                else
                    throw new SQLException("Failed to insert row " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unkown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

            int rowsDeleted;

            switch (sUriMatcher.match(uri)) {

                case CODE_RECIPES:

                    rowsDeleted = mDbHelper.getWritableDatabase().delete(
                            RecipeContract.RecipeEntry.TABLE_NAME,
                            selection,
                            selectionArgs);
                    break;

                case CODE_RECIPE_INGREDIENTS:

                    rowsDeleted = mDbHelper.getWritableDatabase().delete(
                            RecipeIngredientContract.RecipeIngredientEntry.TABLE_NAME,
                            selection,
                            selectionArgs);
                    break;

                case CODE_RECIPE_STEPS:

                    rowsDeleted = mDbHelper.getWritableDatabase().delete(
                            RecipeStepContract.RecipeStepEntry.TABLE_NAME,
                            selection,
                            selectionArgs);
                    break;

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            if (rowsDeleted != 0)
                getContext().getContentResolver().notifyChange(uri, null);

            return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


}
