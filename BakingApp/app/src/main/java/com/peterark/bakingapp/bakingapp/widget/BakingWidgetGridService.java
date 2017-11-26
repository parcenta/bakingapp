package com.peterark.bakingapp.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeIngredientContract;
import com.peterark.bakingapp.bakingapp.utils.BakingDataUtils;

import timber.log.Timber;

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

    private final Context mContext;
    private Cursor mCursor;

    BakingWidgetGridRemoteViewsFactory(Context context){
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        Timber.d("Widget Adapter refreshing.");

        int recipeId = BakingDataUtils.getWidgetSelectedRecipeId(mContext);
        if(mCursor !=null && !mCursor.isClosed()) mCursor.close();

        // Get Recipe Ingredients Cursor.
        mCursor = mContext.getContentResolver().query(RecipeIngredientContract.RecipeIngredientEntry.CONTENT_URI,
                null,
                RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null);
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
            int ingredienteQuantity         = mCursor.getInt(mCursor.getColumnIndex(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_QUANTITY));
            String ingredientName           = mCursor.getString(mCursor.getColumnIndex(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_NAME));
            String ingredientMeasure        = mCursor.getString(mCursor.getColumnIndex(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_MEASURE));

            String ingredientLabel = ingredienteQuantity + " " + ingredientMeasure + " of " + ingredientName;

            // Set Layout.
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_item_baking_widget);
            remoteViews.setTextViewText(R.id.baking_widget_grid_recipe_name_text_view,ingredientLabel);

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
