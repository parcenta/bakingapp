package com.peterark.bakingapp.bakingapp.panels.recipeDetail;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeIngredientContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeStepContract;
import com.peterark.bakingapp.bakingapp.databinding.FragmentRecipeDetailBinding;
import com.peterark.bakingapp.bakingapp.helperStructures.RecipeStep;
import com.peterark.bakingapp.bakingapp.utils.BakingDataUtils;
import com.peterark.bakingapp.bakingapp.widget.BakingIntentService;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by PETER on 6/11/2017.
 */

public class RecipeDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<RecipeDetailFragment.RecipeDetailFragmentLoaderResponse>,
                                                                RecipeDetailFragmentStepAdapter.OnRecipeStepClickHandler{

    private static final int RECIPE_DETAIL_LOADER_ID = 1001;

    // Bundle variables
    private static final String RECIPE_ID = "RECIPE_ID";
    private int mRecipeId;


    private FragmentRecipeDetailBinding mBinding;

    private RecipeDetailFragmentStepAdapter mAdapter;

    private RecipeDetailFragmentStepAdapter.OnRecipeStepClickHandler mHandler;

    private RecipeDetailFragmentLoaderResponse mResponse;

    // Constructor
    public RecipeDetailFragment(){
        Timber.d("RecipeDetailFragment is created.");
    }

    // Fragment "Instance constructor".
    // As recomended in StackOverflow post: https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment
    public static RecipeDetailFragment newInstance(int recipeId){
        Timber.d("RecipeDetailFragment newInstance with RecipeId:" + recipeId);
        RecipeDetailFragment fragment = new RecipeDetailFragment();

        Bundle args = new Bundle();
        args.putInt(RECIPE_ID,recipeId);
        fragment.setArguments(args);

        return fragment;
    }

    private int getRecipeId(){
        Bundle bundle = getArguments();
        if (bundle!=null)
            return bundle.getInt(RECIPE_ID,0);
        else
            return 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_detail, container, false);

        // Get the RecipeId
        mRecipeId = getRecipeId();

        // Set the Adapter
        mAdapter = new RecipeDetailFragmentStepAdapter(null,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.recipeDetailStepRecyclerview.setLayoutManager(layoutManager);
        mBinding.recipeDetailStepRecyclerview.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Init Loader
        getLoaderManager().initLoader(RECIPE_DETAIL_LOADER_ID,null,this);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mHandler = (RecipeDetailFragmentStepAdapter.OnRecipeStepClickHandler) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnImageClickListener");
        }
    }

    @Override
    public Loader<RecipeDetailFragmentLoaderResponse> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<RecipeDetailFragmentLoaderResponse>(getActivity()) {

            private RecipeDetailFragmentLoaderResponse cachedResponse;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(cachedResponse != null)
                    deliverResult(cachedResponse);
                else
                    forceLoad();
            }

            @Override
            public RecipeDetailFragmentLoaderResponse loadInBackground() {
                String recipeName           = "";
                int recipeServings       = 0;
                String ingredientsText      = "";
                List<RecipeStep> stepList   = new ArrayList<>();

                Context context = getContext();
                if (context == null || mRecipeId == 0) {
                    Timber.d("An error ocurred. Context is null or mRecipeId is zero...");
                    return null;
                }

                // Make the respective queries to the database.
                Cursor recipeCursor             = null;
                Cursor recipeIngredientCursor   = null;
                Cursor recipeStepCursor         = null;
                try {
                    // -----------------------------------------------------------------------
                    // First query the selected recipeId to get the basic recipe info.
                    // -----------------------------------------------------------------------
                    recipeCursor = context.getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,
                            null,
                            RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " = ?",
                            new String[]{String.valueOf(mRecipeId)},
                            null);
                    if(recipeCursor !=null){
                        if(recipeCursor.moveToNext()) {
                            recipeName = recipeCursor.getString(recipeCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
                            recipeServings = recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS));
                        }
                        if (!recipeCursor.isClosed()) recipeCursor.close();
                    }



                    // ---------------------------------------------------------------------------------------
                    // Then query all the selected recipe ingredients and put all of them in a single string.
                    // ---------------------------------------------------------------------------------------
                    recipeIngredientCursor = context.getContentResolver().query(RecipeIngredientContract.RecipeIngredientEntry.CONTENT_URI,
                            null,
                            RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_ID + " = ?",
                            new String[]{String.valueOf(mRecipeId)},
                            null);
                    if(recipeIngredientCursor!= null) {
                        while (recipeIngredientCursor.moveToNext()){
                            double quantity = recipeIngredientCursor.getDouble(recipeIngredientCursor.getColumnIndex(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_QUANTITY));
                            String name     = recipeIngredientCursor.getString(recipeIngredientCursor.getColumnIndex(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_NAME));
                            String measure  = recipeIngredientCursor.getString(recipeIngredientCursor.getColumnIndex(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_MEASURE));

                            ingredientsText += " - " + String.valueOf(quantity) + " " + measure + " of " + name + "\n";
                        }
                        if (!recipeIngredientCursor.isClosed()) recipeIngredientCursor.close();
                    }

                    // -------------------------------------------------------------------------------------------------
                    // And finally query the selected recipe steps. And put them on the List<RecipeStep> list.
                    // -------------------------------------------------------------------------------------------------
                    recipeStepCursor = context.getContentResolver().query(RecipeStepContract.RecipeStepEntry.CONTENT_URI,
                            null,
                            RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_ID + " = ?",
                            new String[]{String.valueOf(mRecipeId)},
                            RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID);
                    if(recipeStepCursor!=null){
                        while (recipeStepCursor.moveToNext()) {
                            int stepId                  = recipeStepCursor.getInt(recipeStepCursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID));
                            String stepShortDescription = recipeStepCursor.getString(recipeStepCursor.getColumnIndex(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_SHORT_DESCRIPTION));
                            stepList.add(new RecipeStep(stepId, stepShortDescription)); // Adding it to the list.
                        }
                        if (!recipeStepCursor.isClosed()) recipeStepCursor.close();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }finally {
                    if (recipeCursor!= null && !recipeCursor.isClosed()) recipeCursor.close();
                    if (recipeIngredientCursor!= null && !recipeIngredientCursor.isClosed()) recipeIngredientCursor.close();
                    if (recipeStepCursor!= null && !recipeStepCursor.isClosed()) recipeStepCursor.close();
                }

                // Set the Selected Recipe for the widget
                BakingDataUtils.setWidgetSelectedRecipeId(context,mRecipeId);

                // Update the widget. When the recipe is selected.
                BakingIntentService.startActionShowSelectedRecipeIngredientsInWidget(context);

                // If nothing happens before, then i should have valid data at this point.
                return new RecipeDetailFragmentLoaderResponse(recipeName,recipeServings,ingredientsText,stepList);
            }

            @Override
            public void deliverResult(RecipeDetailFragmentLoaderResponse response) {
                cachedResponse = response;
                super.deliverResult(response);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<RecipeDetailFragmentLoaderResponse> loader, RecipeDetailFragmentLoaderResponse response) {
        mResponse = response;

        if (mResponse== null){
            Toast.makeText(getActivity(),"An error has ocurred",Toast.LENGTH_SHORT).show();
            return;
        }

        mBinding.recipeNameTextview.setText(response.recipeName);
        mBinding.ingredientsTextarea.setText(response.ingredientsText);
        mAdapter.setItemList(response.stepList);
    }

    @Override
    public void onLoaderReset(Loader<RecipeDetailFragmentLoaderResponse> loader) {

    }

    @Override
    public void onRecipeStepClick(int recipeStep) {
        if(mHandler!=null)
            mHandler.onRecipeStepClick(recipeStep);
    }

    public class RecipeDetailFragmentLoaderResponse{
        final String recipeName;
        final int recipeServings;
        final String ingredientsText;
        final List<RecipeStep> stepList;
        public RecipeDetailFragmentLoaderResponse(String recipeName,int recipeServings, String ingredientsText, List<RecipeStep> stepList){
            this.recipeName         = recipeName;
            this.recipeServings     = recipeServings;
            this.ingredientsText    = ingredientsText;
            this.stepList           = stepList;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RECIPE_ID,mRecipeId);
    }
}
