package com.peterark.bakingapp.bakingapp.panels;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.peterark.bakingapp.bakingapp.R;
import com.peterark.bakingapp.bakingapp.panels.recipeDetail.RecipeDetailFragment;
import com.peterark.bakingapp.bakingapp.panels.recipeDetail.RecipeDetailFragmentStepAdapter;
import com.peterark.bakingapp.bakingapp.panels.recipeDetailStep.RecipeDetailStepActivity;
import com.peterark.bakingapp.bakingapp.panels.recipeDetailStep.RecipeDetailStepFragment;

import timber.log.Timber;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.RecipeDetailHandler,RecipeDetailFragmentStepAdapter.OnRecipeStepClickHandler {

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

        Bundle bundle = new Bundle();
        bundle.putInt(RECIPE_ID,recipeId);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Timber.d("RecipeDetailActivity ONCREATE...");

        // Showing the Back Button in the AppBar.
        ActionBar ab = getSupportActionBar();
        if (ab!=null) ab.setDisplayHomeAsUpEnabled(true);

        // Check if the Panel is in two panel mode or not.
        twoPane = findViewById(R.id.recipe_step_detail_container) != null;

        // Get the RecipeId from the Bundle.
        if(savedInstanceState==null) {
            Timber.d("RecipeDetailActivity has NULL savedInstance:");


            // --------------------------------------
            // Getting the Recipe from Intent.
            // --------------------------------------
            Timber.d("... then we get the recipeId from getIntent().getExtras()");
            Intent receivedIntent = getIntent();
            Bundle bundle = receivedIntent.getExtras();
            if (bundle!=null && bundle.containsKey(RECIPE_ID))
                recipeId = bundle.getInt(RECIPE_ID);
            else
                recipeId = 0;

            Timber.d("... and we recreate the RecipeDetailFragment.");

            // --------------------------------------
            // Create RecipeDetail Fragment
            // --------------------------------------
            Fragment recipeDetailFragment = RecipeDetailFragment.newInstance(recipeId);

            // Setting the RecipeDetail fragment in the view.
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(R.id.recipe_detail_container,recipeDetailFragment).commit();

        }else{
            recipeId = savedInstanceState.getInt(RECIPE_ID);
            Timber.d("RecipeDetailActivity has VALID savedInstance, so we get the recipeId from the savedInstance...");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method that will display
    @Override
    public void displayFirstRecipeStepInTwoPane(int firstRecipeStepId) {
        if(twoPane){
            // Create RecipeDetail Fragment
            Fragment recipeDetailStepFragment = RecipeDetailStepFragment.newInstance(recipeId,firstRecipeStepId);

            // Setting the RecipeDetail fragment in the view.
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.recipe_step_detail_container,recipeDetailStepFragment).commit();
        }
    }

    @Override
    public void onRecipeStepClick(int recipeStepId) {
        if(twoPane){
            // Create RecipeDetail Fragment
            Fragment recipeDetailStepFragment = RecipeDetailStepFragment.newInstance(recipeId,recipeStepId);

            // Setting the RecipeDetail fragment in the view.
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.recipe_step_detail_container,recipeDetailStepFragment).commit();
        }else
            RecipeDetailStepActivity.launch(this,recipeId,recipeStepId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RECIPE_ID,recipeId);
        super.onSaveInstanceState(outState);
    }

}
