package com.peterark.bakingapp.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.panels.RecipeDetailActivity;

/**
 * Created by PETER on 14/11/2017.
 */

public class BakingWidgetGridService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingWidgetGridRemoteViewsFactory(this.getApplicationContext());
    }
}


class BakingWidgetGridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    BakingWidgetGridRemoteViewsFactory(Context context){
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        // GET TOP 5 recipes (ordered by index)
        mCursor = mContext.getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                RecipeContract.RecipeEntry.COLUMN_RECIPE_ID);
    }

    @Override
    public void onDestroy() {
        if(mCursor!=null && !mCursor.isClosed()) mCursor.close();
    }

    @Override
    public int getCount() {
        return mCursor!=null ? mCursor.getCount() : 0;
    }

    // Acts like the OnBindViewHolder
    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0)
            return null;
        if(mCursor.moveToPosition(position)){
            // Get Cursor values
            int recipeId      = mCursor.getInt(mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));
            String recipeName = mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));

            // Set Layout.
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_item_baking_widget);
            remoteViews.setTextViewText(R.id.baking_widget_grid_recipe_name_text_view,recipeName);

            // Set the Parameters for the onClick action which lauch the Pending Intent with the next specified values.
            Bundle extras = new Bundle();
            extras.putInt(RecipeDetailActivity.RECIPE_ID,recipeId);
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            remoteViews.setOnClickFillInIntent(R.id.baking_grid_view_item,fillInIntent);

            return remoteViews;
        }
        else
            return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
