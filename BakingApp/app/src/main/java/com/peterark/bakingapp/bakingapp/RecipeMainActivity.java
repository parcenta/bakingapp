package com.peterark.bakingapp.bakingapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.peterark.bakingapp.bakingapp.testing.BakingRecipeLoadIdlingResource;

public class RecipeMainActivity  extends AppCompatActivity implements MasterRecipeListFragment.BakingRecipeListHandler {


    // For Testing
    @Nullable
    private BakingRecipeLoadIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);
    }

    // ------------------------------------------------------------
    // FOR TESTING ONLY. Like the course shows te testing based on Acitivities,
    // I had to figure it out how to send Loading status from fragment to this Activity.
    // So im using a Handler (interface) to make the fragment communicate to this activity.
    // ------------------------------------------------------------
    @Override
    public void setIdlingResource(boolean isIdle) {
        if (mIdlingResource != null) mIdlingResource.setIdleState(isIdle);
    }


    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new BakingRecipeLoadIdlingResource();
        }
        return mIdlingResource;
    }
}
