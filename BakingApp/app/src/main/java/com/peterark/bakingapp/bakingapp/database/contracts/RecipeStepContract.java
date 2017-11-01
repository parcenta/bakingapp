package com.peterark.bakingapp.bakingapp.database.contracts;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by PETER on 1/11/2017.
 */

public class RecipeStepContract {

    public static final String CONTENT_AUTHORITY  = "com.peterark.bakingapp";
    private static final Uri BASE_CONTENT_URI  = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPE_STEPS = "recipesteps";

    public static final class RecipeStepEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE_STEPS).build();


        // TABLE NAME
        public static final String TABLE_NAME = "recipestep";

        // COLUMNS
        public static final String COLUMN_RECIPE_ID                     = "recipeid";
        public static final String COLUMN_RECIPE_STEP_ID                = "stepid";
        public static final String COLUMN_RECIPE_STEP_SHORT_DESCRIPTION = "stepshortdescription";
        public static final String COLUMN_RECIPE_STEP_DESCRIPTION       = "stepdescription";
        public static final String COLUMN_RECIPE_STEP_VIDEO_URL         = "stepvideourl";
        public static final String COLUMN_RECIPE_STEP_THUMBNAIL_URL     = "stepthumbnailurl";
    }

}
