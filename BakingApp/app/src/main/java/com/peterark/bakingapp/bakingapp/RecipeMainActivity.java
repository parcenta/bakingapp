package com.peterark.bakingapp.bakingapp;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeMainActivity  extends AppCompatActivity {

    private MasterRecipeListFragment mRecipeListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);
    }
}
