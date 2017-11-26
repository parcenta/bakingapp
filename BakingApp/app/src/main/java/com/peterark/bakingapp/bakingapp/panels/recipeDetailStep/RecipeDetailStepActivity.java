package com.peterark.bakingapp.bakingapp.panels.recipeDetailStep;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.peterark.bakingapp.bakingapp.R;

/**
 * Created by PETER on 7/11/2017.
 */

public class RecipeDetailStepActivity extends AppCompatActivity implements RecipeDetailStepFragment.PaginationHandler{

    int mRecipeId;
    int mRecipeStepId;

    /* -----------------------------------------------------------------
     * Launch Helper
     * -----------------------------------------------------------------*/
    public static void launch(Context context, int recipeId, int recipeStepId) {
        context.startActivity(launchIntent(context, recipeId,recipeStepId));
    }

    private static Intent launchIntent(Context context, int recipeId, int recipeStepId) {
        Class destinationActivity = RecipeDetailStepActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(RecipeDetailStepFragment.RECIPE_ID, recipeId);
        intent.putExtra(RecipeDetailStepFragment.RECIPE_STEP_ID, recipeStepId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_step_activity);

        // Get the RecipeId from the Bundle.
        Intent receivedIntent = getIntent();
        mRecipeId = 0;
        mRecipeStepId = 0;
        if (receivedIntent.hasExtra(RecipeDetailStepFragment.RECIPE_ID))
            mRecipeId = receivedIntent.getIntExtra(RecipeDetailStepFragment.RECIPE_ID,0);
        if (receivedIntent.hasExtra(RecipeDetailStepFragment.RECIPE_STEP_ID))
            mRecipeStepId = receivedIntent.getIntExtra(RecipeDetailStepFragment.RECIPE_STEP_ID,0);

        // Like this acitivty is called only on non-tablet devices. Then we just check if we are in landsacpe mode...
        // to show the video in full screen.
        boolean isInLandscapeMode  = getResources().getBoolean(R.bool.isInLandscapeMode);
        if(isInLandscapeMode){
            ActionBar ab = getSupportActionBar();
            if (ab != null) ab.hide();
        }

        // Setting the fragments.
        if (savedInstanceState == null)
            createRecipeDetailStepFragment(mRecipeStepId);

    }

    @Override
    public void goToPreviousRecipe(int previousStepId) {
        if(previousStepId<0) return;
        createRecipeDetailStepFragment(previousStepId);
    }

    @Override
    public void goToNextRecipe(int nextRecipeId) {
        if(nextRecipeId<0) return;
        createRecipeDetailStepFragment(nextRecipeId);
    }

    private void createRecipeDetailStepFragment(int recipeStepId){
        // Create RecipeDetail Fragment
        Fragment newFragment = RecipeDetailStepFragment.newInstance(mRecipeId,recipeStepId);

        // Setting the RecipeDetail fragment in the view.
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.recipe_detail_fragment_holder,newFragment).commit();
    }
}
