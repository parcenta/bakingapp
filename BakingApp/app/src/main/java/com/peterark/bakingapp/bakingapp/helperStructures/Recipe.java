package com.peterark.bakingapp.bakingapp.helperStructures;

import java.util.List;

/**
 * Created by PETER on 30/10/2017.
 */

public class Recipe{
    public final int recipeId;
    public final String recipeName;
    public final int recipeServings;
    public final String recipeImageUrl;
    public final List<RecipeIngredient> recipeIngredients;
    public final List<RecipeStep> recipeSteps;

    public Recipe(int recipeId, String recipeName, int recipeServings, String recipeImageUrl, List<RecipeIngredient> recipeIngredients, List<RecipeStep> recipeSteps) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeServings = recipeServings;
        this.recipeImageUrl = recipeImageUrl != null ? recipeImageUrl : "";
        this.recipeIngredients = recipeIngredients;
        this.recipeSteps = recipeSteps;
    }
}
