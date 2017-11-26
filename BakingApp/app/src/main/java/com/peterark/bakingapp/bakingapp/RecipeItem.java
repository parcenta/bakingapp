package com.peterark.bakingapp.bakingapp;

/**
 * Created by PETER on 29/10/2017.
 */

public class RecipeItem {
    public final int recipeId;
    public final String recipeName;
    public final int recipeServings;
    public RecipeItem(int recipeId, String recipeName, int recipeServings){
        this.recipeId       = recipeId;
        this.recipeName     = recipeName;
        this.recipeServings = recipeServings;
    }
}
