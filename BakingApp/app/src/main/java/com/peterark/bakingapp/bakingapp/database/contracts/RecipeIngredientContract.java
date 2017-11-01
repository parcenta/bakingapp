package com.peterark.bakingapp.bakingapp.database.contracts;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by PETER on 1/11/2017.
 */

public class RecipeIngredientContract {

    public static final String CONTENT_AUTHORITY  = "com.peterark.bakingapp";
    private static final Uri BASE_CONTENT_URI  = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPE_INGREDIENTS = "recipeingredients";

    public static final class RecipeIngredientEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE_INGREDIENTS).build();

        // TABLE NAME
        public static final String TABLE_NAME = "recipeingredient";

        // COLUMNS
        public static final String COLUMN_RECIPE_ID                     = "recipeid";
        public static final String COLUMN_RECIPE_INGREDIENT_NAME        = "ingredientname";
        public static final String COLUMN_RECIPE_INGREDIENT_QUANTITY    = "ingredientquantity";
        public static final String COLUMN_RECIPE_INGREDIENT_MEASURE     = "ingredientmeasure";
    }

}