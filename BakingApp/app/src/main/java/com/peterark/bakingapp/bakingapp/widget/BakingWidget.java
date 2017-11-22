package com.peterark.bakingapp.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.RecipeMainActivity;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.panels.RecipeDetailActivity;
import com.peterark.bakingapp.bakingapp.utils.BakingDataUtils;


/**
 * Implementation of App Widget functionality.
 */
public class BakingWidget extends AppWidgetProvider {

    public static final String WIDGET_SELECTED_RECIPE_ID = "WIDGET_SELECTED_RECIPE_ID";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,int appWidgetId, int selectedRecipeId, String recipeName) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        // If selected id > 0, then there is a valid selected recipe.
        if(selectedRecipeId > 0){

            // Set the name in the textview
            views.setTextViewText(R.id.widget_recipe_name_text_view,recipeName.length()>0?recipeName:"Unknown");

            // Set visibility of some layout items.
            views.setViewVisibility(R.id.widget_no_recipe_selected_textview, View.GONE);
            views.setViewVisibility(R.id.baking_widget_grid_view, View.VISIBLE);


            // Set the "adapter" for the gridview inside the widget
            Intent intent = new Intent(context, BakingWidgetGridService.class);
            views.setRemoteAdapter(R.id.baking_widget_grid_view, intent);
        }else{

            views.setTextViewText(R.id.widget_recipe_name_text_view,context.getString(R.string.widget_no_selected_recipe_title));

            // Set visbility to some layout items
            views.setViewVisibility(R.id.baking_widget_grid_view, View.GONE);
            views.setViewVisibility(R.id.widget_no_recipe_selected_textview, View.VISIBLE);

            // Create and set the Pending to be launched to make the user choose any available recipe.
            Intent recipeListIntent = new Intent(context,RecipeMainActivity.class);
            PendingIntent recipeListPendingIntent = PendingIntent.getActivity(context,0,recipeListIntent,PendingIntent.FLAG_CANCEL_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_no_recipe_selected_textview,recipeListPendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateBakingWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds,int recipeId, String recipeName){
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeId, recipeName);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        BakingIntentService.startActionShowSelectedRecipeIngredientsInWidget(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

