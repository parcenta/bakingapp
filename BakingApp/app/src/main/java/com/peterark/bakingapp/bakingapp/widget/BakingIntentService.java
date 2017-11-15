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

/**
 * Created by PETER on 14/11/2017.
 */

public class BakingIntentService extends IntentService {

    public static final String ACTION_WIDGET_SHOW_TOP_RECIPES = "ACTION_WIDGET_SHOW_TOP_RECIPES";

    public BakingIntentService(){
        super("BakingIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null){
            final String action = intent.getAction();
            if(action.equals(ACTION_WIDGET_SHOW_TOP_RECIPES))
                handleShowTopRecipesInWidget();
        }
    }

    public static void startActionShowTopRecipesInWidget(Context context){
        Intent intent = new Intent(context,BakingIntentService.class);
        intent.setAction(ACTION_WIDGET_SHOW_TOP_RECIPES);
        context.startService(intent);
    }

    private void handleShowTopRecipesInWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.baking_widget_grid_view);
    }
}
