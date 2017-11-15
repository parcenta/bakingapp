package com.peterark.bakingapp.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.RecipeMainActivity;
import com.peterark.bakingapp.bakingapp.panels.RecipeDetailActivity;


/**
 * Implementation of App Widget functionality.
 */
public class BakingWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        // Set the "adapter" for the gridview inside the widget
        Intent intent = new Intent(context, BakingWidgetGridService.class);
        views.setRemoteAdapter(R.id.baking_widget_grid_view, intent);

        // Set the Pending Intent template to call the RecipeDetailActivity.
        Intent recipeDetailIntent = new Intent(context, RecipeDetailActivity.class);
        PendingIntent recipeDetailPendingIntent = PendingIntent.getActivity(context,0,recipeDetailIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.baking_widget_grid_view,recipeDetailPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
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

