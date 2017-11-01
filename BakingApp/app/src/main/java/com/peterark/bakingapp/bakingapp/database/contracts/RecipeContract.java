package com.peterark.bakingapp.bakingapp.database.contracts;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by PETER on 1/11/2017.
 */

public class RecipeContract {

    public static final String CONTENT_AUTHORITY  = "com.peterark.bakingapp";
    private static final Uri BASE_CONTENT_URI  = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPES = "recipes";

    public static final class RecipeEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();


        // TABLE NAME
        public static final String TABLE_NAME = "recipe";

        // COLUMNS
        public static final String COLUMN_RECIPE_ID              = "recipeid";
        public static final String COLUMN_RECIPE_NAME            = "recipename";
        public static final String COLUMN_RECIPE_SERVINGS        = "recipeservings";

    }
}
