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

    private static final String RECIPE_ID = "RECIPE_ID";

    // Intent variables
    private int recipeId;

    // Responsive Phone/Tablet helper variables
    private boolean twoPane;

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
            Fragment recipeDetailFragment = new RecipeDetailFragment();
            Bundle args = new Bundle();
            args.putInt(RecipeDetailFragment.RECIPE_ID,recipeId);
            recipeDetailFragment.setArguments(args);

            // Setting the RecipeDetail fragment in the view.
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(R.id.recipe_detail_container,recipeDetailFragment).commit();
        }

    }

    @Override
    public void onRecipeStepClick(int recipeStepId) {
        if(twoPane){
            // Create RecipeDetail Fragment
            Fragment newFragment = new RecipeDetailStepFragment();
            Bundle args = new Bundle();
            args.putInt(RecipeDetailStepFragment.RECIPE_ID,recipeId);
            args.putInt(RecipeDetailStepFragment.RECIPE_STEP_ID,recipeStepId);
            newFragment.setArguments(args);

            // Setting the RecipeDetail fragment in the view.
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.recipe_step_detail_container,newFragment).commit();
        }else
            RecipeDetailStepActivity.launch(this,recipeId,recipeStepId);

    }
}
