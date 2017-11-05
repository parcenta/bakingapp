package com.peterark.bakingapp.bakingapp;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.databinding.FragmentMasterRecipeListBinding;
import com.peterark.bakingapp.bakingapp.utils.BakingDataUtils;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by PETER on 29/10/2017.
 */

public class MasterRecipeListFragment extends Fragment implements LoaderManager.LoaderCallbacks<MasterRecipeListFragment.MasterRecipeListResponse>,
                                                                        MasterRecipeListAdapter.OnRecipeClickHandler {


    FragmentMasterRecipeListBinding mBinding;
    MasterRecipeListAdapter mAdapter;

    List<RecipeItem> mItemList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_master_recipe_list, container, false);

        mAdapter = new MasterRecipeListAdapter(null,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.recipeListRecyclerView.setLayoutManager(layoutManager);
        mBinding.recipeListRecyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Init Loader
        getLoaderManager().initLoader(0,null,this);
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

                // First Hide the RecyclerView and the Error Message.
                mBinding.recipeListRecyclerView.setVisibility(View.INVISIBLE);
                mBinding.recipeListErrorOcurredTextview.setVisibility(View.INVISIBLE);

                // Show Progress Bar.
                mBinding.recipeListProgressBar.setVisibility(View.VISIBLE);

                // If there is a cachedResponse, we deliver the result.
                if(cachedResponse!=null) {
                    Timber.d("onStartLoading: Delivering result. Not force loading..");
                    deliverResult(cachedResponse);
                }
                else // If there is NOT a cachedResponse then forceLoad.
                {

                    Timber.d("onStartLoading: Forcing Load...");

                    // Set the MoviesList to null.
                    mItemList = null;
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
                    while(recipesCursor.moveToNext()){
                        int recipeId        = recipesCursor.getInt(recipesCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID));
                        String recipeName   = recipesCursor.getString(recipesCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
                        recipeList.add(new RecipeItem(recipeId,recipeName));
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

        if (response == null) {
            Toast.makeText(getActivity(), "An error has ocurred. Please try again.", Toast.LENGTH_SHORT).show();
            mBinding.recipeListErrorOcurredTextview.setVisibility(View.VISIBLE);
            return;
        }

        // Hide the Progress Bar.
        mBinding.recipeListProgressBar.setVisibility(View.INVISIBLE);

        // Set the list in the adapter.
        mItemList = response.recipeList;
        mAdapter.setItemList(mItemList);

        // Show any error messages as a Toast Message.
        if (response.errorMessage.length()>0){
            Context context = getActivity();
            if (context != null) Toast.makeText(context,response.errorMessage,Toast.LENGTH_SHORT).show();
        }

        // If there are no results found and also there is an error message. then show a message in the layout (To make the user to press it in order to retry the loading).
        if (mItemList !=null && mItemList.size() == 0 && response.errorMessage.length()>0)
            mBinding.recipeListErrorOcurredTextview.setVisibility(View.VISIBLE);
        else //
            mBinding.recipeListRecyclerView.setVisibility(View.VISIBLE);



    }

    @Override
    public void onLoaderReset(Loader<MasterRecipeListResponse> loader) {
    }

    @Override
    public void onRecipeClick(RecipeItem recipeId) {

    }

    public class MasterRecipeListResponse {
        public String errorMessage;
        public List<RecipeItem> recipeList;
        public MasterRecipeListResponse(String errorMessage, List<RecipeItem> recipeList){
            this.errorMessage   = errorMessage;
            this.recipeList     = recipeList;
        }
    }

}
