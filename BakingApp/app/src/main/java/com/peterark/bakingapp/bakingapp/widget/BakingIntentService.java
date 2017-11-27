package com.peterark.bakingapp.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.utils.BakingDataUtils;

import timber.log.Timber;

public class BakingIntentService extends IntentService {

    private static final String ACTION_WIDGET_SHOW_SELECTED_RECIPE_INGREDIENTS  = "ACTION_WIDGET_SHOW_SELECTED_RECIPE_INGREDIENTS";
    public static final String ACTION_WIDGET_SHOW_NEXT_RECIPE_INGREDIENTS       = "ACTION_WIDGET_SHOW_NEXT_RECIPE_INGREDIENTS";
    public static final String ACTION_WIDGET_SHOW_PREVIOUS_RECIPE_INGREDIENTS   = "ACTION_WIDGET_SHOW_PREVIOUS_RECIPE_INGREDIENTS";

    public BakingIntentService(){
        super("BakingIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null){
            final String action = intent.getAction();
            if(action.equals(ACTION_WIDGET_SHOW_SELECTED_RECIPE_INGREDIENTS))
                handleShowSelectedRecipeIngredients();
            else if(action.equals(ACTION_WIDGET_SHOW_NEXT_RECIPE_INGREDIENTS))
                handleShowNextRecipeIngredients();
            else if(action.equals(ACTION_WIDGET_SHOW_PREVIOUS_RECIPE_INGREDIENTS))
                handleShowPreviousRecipeIngredients();
        }
    }

    public static void startActionShowSelectedRecipeIngredientsInWidget(Context context){
        Intent intent = new Intent(context,BakingIntentService.class);
        intent.setAction(ACTION_WIDGET_SHOW_SELECTED_RECIPE_INGREDIENTS);
        context.startService(intent);
    }

    private void handleShowSelectedRecipeIngredients(){
        Timber.d("Widget is going to be refreshed from IntentService...");

        // Get Current Selected Recipe Id.
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
        int[] appWidgetsIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetsIds, R.id.baking_widget_grid_view);
        BakingWidgetProvider.updateBakingWidgets(this,appWidgetManager,appWidgetsIds,selectedRecipeId,selectedRecipeName);
    }

    private void handleShowNextRecipeIngredients(){
        Timber.d("Selecting Next Recipe...");

        // Get Current Selected Recipe Id.
        int selectedRecipeId = BakingDataUtils.getWidgetSelectedRecipeId(this);

        // Get all Recipes (ordered by RecipeId)
        Cursor cursor = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,null,null,null,RecipeContract.RecipeEntry.COLUMN_RECIPE_ID);

        // Search for the Next Recipe Id.
        int newRecipeId = 0;
        if (cursor!=null){

            //
            int count=0;
            boolean currentRecipeIdWasFound = false;
            while(cursor.moveToNext()){
                int cursorRecipeId = cursor.getInt(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));

                // Set the next selectedRecipe with the first one as default. Just in case the next recipe id is not available (for example if the current selected recipe is the last one)
                if(count ==0)
                    newRecipeId = cursorRecipeId;

                // If the current selected recipe id (saved in the SharedPreferences) was found in the prevous loop, then assign the current loop cursor recipe id and EXIT loop.
                if(currentRecipeIdWasFound){
                    newRecipeId = cursorRecipeId;
                    break;
                }

                // If the cursor recipe id is the current selected one (saved in the SharedPreferencee. Then mark the flag as TRUE.
                if (cursorRecipeId == selectedRecipeId)
                    currentRecipeIdWasFound = true;

                count++;
            }
            if(!cursor.isClosed()) cursor.close();
        }

        // Assuming that a RecipeId greather than zero is valid, we we will save it in the SharedPreferences. And update widget afterwards.
        if(newRecipeId > 0 ){
            BakingDataUtils.setWidgetSelectedRecipeId(this,newRecipeId);
            handleShowSelectedRecipeIngredients();
        }

    }

    private void handleShowPreviousRecipeIngredients(){
        Timber.d("Selecting Previous Recipe...");

        // Get Current Selected Recipe Id.
        int selectedRecipeId = BakingDataUtils.getWidgetSelectedRecipeId(this);

        // Get all Recipes (ordered by RecipeId)
        Cursor cursor = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,null,null,null,RecipeContract.RecipeEntry.COLUMN_RECIPE_ID);

        // Search for the Next Recipe Id.
        int newRecipeId = 0;
        if (cursor!=null){

            int countIndex=0;
            int currentRecipeIndex = 0;
            for(int i = 0; i < cursor.getCount(); i++){
                if(cursor.moveToPosition(i)){
                    Timber.d("Widget: Checking position cursor " + i);


                    // Get the current RecipeId value in cursor
                    int cursorRecipeId = cursor.getInt(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));

                    // If the cursor recipe id is the current selected one (saved in the SharedPreferencee. Then mark the flag as TRUE.
                    if (cursorRecipeId == selectedRecipeId) {
                        Timber.d("Widget: Cursor in position " + i + " have the Current Recipe... =)");
                        currentRecipeIndex = i;
                        break;
                    }
                }
            }

            Timber.d("Widget: Current Recipe Index: " + currentRecipeIndex );

            // If the current recipe is the first one, then set the last recipe index of the cursor.
            if(currentRecipeIndex <= 0){
                Timber.d("Widget: Current Recipe Index is zero, then ill try to go to last recipe.");
                if (cursor.moveToLast()) {
                    newRecipeId = cursor.getInt(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));
                    Timber.d("Widget: Getting the last item in cursor. RecipeId = " + newRecipeId);
                }
            }else if (cursor.moveToPosition(currentRecipeIndex - 1)) // If the Cursor is greater than zero, then i Can decrease by one the index safely.
                newRecipeId = cursor.getInt(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));

            if(!cursor.isClosed()) cursor.close();
        }

        // Assuming that a RecipeId greather than zero is valid, we we will save it in the SharedPreferences. And update widget afterwards.
        if(newRecipeId > 0 ){
            Timber.d("Widget: Widget Previous Recipe Id is " + newRecipeId);
            BakingDataUtils.setWidgetSelectedRecipeId(this,newRecipeId);
            handleShowSelectedRecipeIngredients();
        }

    }
}
