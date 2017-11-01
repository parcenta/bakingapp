package com.peterark.bakingapp.bakingapp.helperStructures;

import java.util.List;

/**
 * Created by PETER on 30/10/2017.
 */

public class Recipe{
    public int recipeId;
    public String recipeName;
    public int recipeServings;
    public List<RecipeIngredient> recipeIngredients;
    public List<RecipeStep> recipeSteps;

    public Recipe(int recipeId, String recipeName, int recipeServings, List<RecipeIngredient> recipeIngredients, List<RecipeStep> recipeSteps) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.recipeServings = recipeServings;
        this.recipeIngredients = recipeIngredients;
        this.recipeSteps = recipeSteps;
    }
}
