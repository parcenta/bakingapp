package com.peterark.bakingapp.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.databinding.FragmentMasterRecipeListBinding;
import com.peterark.bakingapp.bakingapp.panels.RecipeDetailActivity;
import com.peterark.bakingapp.bakingapp.utils.BakingDataUtils;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by PETER on 29/10/2017.
 */

public class MasterRecipeListFragment extends Fragment implements LoaderManager.LoaderCallbacks<MasterRecipeListFragment.MasterRecipeListResponse>,
                                                                        MasterRecipeListAdapter.OnRecipeClickHandler
                                                                        {


    private FragmentMasterRecipeListBinding mBinding;
    private MasterRecipeListAdapter mAdapter;

    private MasterRecipeListResponse mTaskResponse;

    private BakingRecipeListHandler mHandler;

    public interface BakingRecipeListHandler{
        void setIdlingResource(boolean isIdle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_recipe_list, container, false);

        // Check if its a tablet or not. (sw600dp)
        Context context = getActivity();
        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);

        // Set the Adapter
        mAdapter = new MasterRecipeListAdapter(null,this);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),isTablet ? 3 : 1);
        mBinding.recipeListRecyclerView.setLayoutManager(layoutManager);
        mBinding.recipeListRecyclerView.setAdapter(mAdapter);
        mBinding.recipeListErrorOcurredTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoaderManager().restartLoader(0,null,MasterRecipeListFragment.this);
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Init Loader

        getLoaderManager().initLoader(0,null,this);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mHandler = (BakingRecipeListHandler) context;
        } catch (ClassCastException e) {
            Timber.d("BakingRecipeListHandler is not set.");
            e.printStackTrace();
        }
    }

    /* -----------------------------------------------------------------------------------------------------
     * LOADER CALLBACKS METHODS
     -------------------------------------------------------------------------------------------------------*/
    @Override
    public Loader<MasterRecipeListResponse> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<MasterRecipeListResponse>(getActivity()) {

            MasterRecipeListResponse cachedResponse;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                // TESTING ONLY: Communicate to Activitty that is NOT in Idle Mode.
                if(mHandler!=null) mHandler.setIdlingResource(false);

                // If there is a cachedResponse, we deliver the result.
                if(cachedResponse!=null) {
                    Timber.d("onStartLoading: Delivering result. Not force loading..");
                    deliverResult(cachedResponse);
                }
                else // If there is NOT a cachedResponse then forceLoad.
                {
                    Timber.d("onStartLoading: Forcing Load...");

                    // First Hide the RecyclerView and the Error Message.
                    mBinding.recipeListRecyclerView.setVisibility(View.INVISIBLE);
                    mBinding.recipeListErrorOcurredTextview.setVisibility(View.INVISIBLE);

                    // Show Progress Bar.
                    mBinding.recipeListProgressBar.setVisibility(View.VISIBLE);

                    // Set the MoviesList to null.
                    mTaskResponse = null;
                    mAdapter.setItemList(null);

                    forceLoad();
                }
            }

            @Override
            public MasterRecipeListResponse loadInBackground() {

                String errorMessage = "";
                List<RecipeItem> recipeList = new ArrayList<>();

                Context context = getContext();
                if (context == null)
                    return null;

                try {
                    // First try to sync the recipes with the list provided on the WS.
                    String syncErrorMessage = BakingDataUtils.syncRecipes(context);
                    if (syncErrorMessage.length() > 0)
                        errorMessage = syncErrorMessage;

                    // Now we load the saved baking recipes (if there is any).If syncing in the previous step failed, we load anyway if there are previously saved recipes in DB.
                    Cursor recipesCursor = context.getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,null,null,null,null);
                    if (recipesCursor!=null) {
                        while (recipesCursor.moveToNext()) {
                            int recipeId = recipesCursor.getInt(recipesCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));
                            String recipeName = recipesCursor.getString(recipesCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
                            int servings = recipesCursor.getInt(recipesCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS));
                            recipeList.add(new RecipeItem(recipeId, recipeName, servings));
                        }
                        if(!recipesCursor.isClosed()) recipesCursor.close();
                    }

                    return new MasterRecipeListResponse(errorMessage,recipeList);

                }catch (Exception e){
                    return null;
                }
            }

            @Override
            public void deliverResult(MasterRecipeListResponse response) {
                cachedResponse = response;
                super.deliverResult(cachedResponse);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MasterRecipeListResponse> loader, MasterRecipeListResponse response) {

        mTaskResponse = response;

        // Hide the Progress Bar.
        mBinding.recipeListProgressBar.setVisibility(View.INVISIBLE);

        if (mTaskResponse == null) {
            Toast.makeText(getActivity(), "An error has ocurred. Please try again.", Toast.LENGTH_SHORT).show();
            mBinding.recipeListErrorOcurredTextview.setVisibility(View.VISIBLE);
            return;
        }

        // Set the list in the adapter.
        mAdapter.setItemList(mTaskResponse.recipeList);

        // Show any error messages as a Toast Message.
        if (mTaskResponse.errorMessage.length()>0){
            Context context = getActivity();
            if (context != null) Toast.makeText(context,mTaskResponse.errorMessage,Toast.LENGTH_SHORT).show();
        }

        // If there are no results found and also there is an error message. then show a message in the layout (To make the user to press it in order to retry the loading).
        if (mTaskResponse.recipeList !=null && mTaskResponse.recipeList.size() == 0 && mTaskResponse.errorMessage.length()>0)
            mBinding.recipeListErrorOcurredTextview.setVisibility(View.VISIBLE);
        else //
            mBinding.recipeListRecyclerView.setVisibility(View.VISIBLE);

        // TESTING: Communicate to activity that now its on Idle Mode.
        if(mHandler!=null) mHandler.setIdlingResource(true);
    }

    @Override
    public void onLoaderReset(Loader<MasterRecipeListResponse> loader) {
    }

    @Override
    public void onRecipeClick(RecipeItem recipeItem) {
        Context context = getActivity();

        if (recipeItem == null || context == null) {
            if (context != null) Toast.makeText(context, "An error has ocurred. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        RecipeDetailActivity.launch(context,recipeItem.recipeId);

    }

    class MasterRecipeListResponse {
        final String errorMessage;
        final List<RecipeItem> recipeList;
        MasterRecipeListResponse(String errorMessage, List<RecipeItem> recipeList){
            this.errorMessage   = errorMessage;
            this.recipeList     = recipeList;
        }
    }

}
