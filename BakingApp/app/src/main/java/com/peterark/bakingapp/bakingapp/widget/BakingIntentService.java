package com.peterark.bakingapp.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.helperStructures.Recipe;
import com.peterark.bakingapp.bakingapp.utils.BakingDataUtils;

import timber.log.Timber;

/**
 * Created by PETER on 14/11/2017.
 */

public class BakingIntentService extends IntentService {

    public static final String ACTION_WIDGET_SHOW_SELECTED_RECIPE_INGREDIENTS = "ACTION_WIDGET_SHOW_SELECTED_RECIPE_INGREDIENTS";

    public BakingIntentService(){
        super("BakingIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null){
            final String action = intent.getAction();
            if(action.equals(ACTION_WIDGET_SHOW_SELECTED_RECIPE_INGREDIENTS))
                handleShowSelectedRecipeIngredients();
        }
    }

    public static void startActionShowSelectedRecipeIngredientsInWidget(Context context){
        Intent intent = new Intent(context,BakingIntentService.class);
        intent.setAction(ACTION_WIDGET_SHOW_SELECTED_RECIPE_INGREDIENTS);
        context.startService(intent);
    }

    private void handleShowSelectedRecipeIngredients(){
        Timber.d("Widget is going to be refreshed from IntentService...");

        // Get Selected Recipe Id.
        int selectedRecipeId = BakingDataUtils.getWidgetSelectedRecipeId(this);

        // Get Name of selected recipe id.
        String selectedRecipeName = "";
        Cursor cursor = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,null,RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + "= ?",new String[]{String.valueOf(selectedRecipeId)},null);
        if (cursor!=null){
            if (cursor.moveToNext())
                selectedRecipeName = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
            if(!cursor.isClosed()) cursor.close();
        }

        // Refresh the Widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetsIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetsIds, R.id.baking_widget_grid_view);
        BakingWidget.updateBakingWidgets(this,appWidgetManager,appWidgetsIds,selectedRecipeId,selectedRecipeName);
    }
}
