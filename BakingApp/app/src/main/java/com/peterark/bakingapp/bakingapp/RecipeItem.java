package com.peterark.bakingapp.bakingapp;

/**
 * Created by PETER on 29/10/2017.
 */

public class RecipeItem {
    public int recipeId;
    public String recipeName;
    public RecipeItem(int recipeId, String recipeName){
        this.recipeId   = recipeId;
        this.recipeName = recipeName;
    }
}
