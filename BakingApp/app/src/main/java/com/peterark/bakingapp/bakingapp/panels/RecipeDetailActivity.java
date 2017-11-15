package com.peterark.bakingapp.bakingapp.panels;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.panels.recipeDetail.RecipeDetailFragment;
import com.peterark.bakingapp.bakingapp.panels.recipeDetail.RecipeDetailFragmentStepAdapter;
import com.peterark.bakingapp.bakingapp.panels.recipeDetailStep.RecipeDetailStepActivity;
import com.peterark.bakingapp.bakingapp.panels.recipeDetailStep.RecipeDetailStepFragment;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragmentStepAdapter.OnRecipeStepClickHandler{

    public static final String RECIPE_ID = "RECIPE_ID";

    // Intent variables
    private int recipeId;

    // Responsive Phone/Tablet helper variables
    private boolean twoPane;


    private Fragment mRecipeDetailFragment;
    private Fragment mRecipeDetailStepFragment;

    /* -----------------------------------------------------------------
     * Launch Helper
     * -----------------------------------------------------------------*/
    public static void launch(Context context, int recipeId) {
        context.startActivity(launchIntent(context, recipeId));
    }

    private static Intent launchIntent(Context context, int recipeId) {
        Class destinationActivity = RecipeDetailActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra(RECIPE_ID, recipeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Get the RecipeId from the Bundle.
        Intent receivedIntent = getIntent();
        if (receivedIntent.hasExtra(RECIPE_ID))
            recipeId = receivedIntent.getIntExtra(RECIPE_ID,0);
        else
            recipeId = 0;

        // Check if the Panel is in two panel mode or not.
        if( findViewById(R.id.recipe_step_detail_container) != null)
            twoPane = true;
        else
            twoPane = false;


        // Setting the fragments.
        if (savedInstanceState == null){

            // Create RecipeDetail Fragment
            mRecipeDetailFragment = new RecipeDetailFragment();
            Bundle args = new Bundle();
            args.putInt(RecipeDetailFragment.RECIPE_ID,recipeId);
            mRecipeDetailFragment.setArguments(args);

            // Setting the RecipeDetail fragment in the view.
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(R.id.recipe_detail_container,mRecipeDetailFragment,"recipedetail").commit();
        }else{
            mRecipeDetailFragment = getSupportFragmentManager().findFragmentByTag("recipedetail");

            Fragment recipeDetailStepFragment = getSupportFragmentManager().findFragmentByTag("recipedetailstep");
            if(recipeDetailStepFragment != null) mRecipeDetailStepFragment = recipeDetailStepFragment;
        }

    }

    @Override
    public void onRecipeStepClick(int recipeStepId) {
        if(twoPane){
            // Create RecipeDetail Fragment
            mRecipeDetailStepFragment = new RecipeDetailStepFragment();
            Bundle args = new Bundle();
            args.putInt(RecipeDetailStepFragment.RECIPE_ID,recipeId);
            args.putInt(RecipeDetailStepFragment.RECIPE_STEP_ID,recipeStepId);
            mRecipeDetailStepFragment.setArguments(args);

            // Setting the RecipeDetail fragment in the view.
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.recipe_step_detail_container,mRecipeDetailStepFragment,"recipedetailstep").commit();
        }else
            RecipeDetailStepActivity.launch(this,recipeId,recipeStepId);
    }
}
