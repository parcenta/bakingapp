package com.peterark.bakingapp.bakingapp.helperStructures;

/**
 * Created by PETER on 30/10/2017.
 */

public class RecipeIngredient {

    public String ingredientName;
    public double ingredientQuantity;
    public String ingredientMeasure;

    public RecipeIngredient(String ingredientName, double ingredientQuantity, String ingredientMeasure){
        this.ingredientName     = ingredientName;
        this.ingredientQuantity = ingredientQuantity;
        this.ingredientMeasure  = ingredientMeasure;
    }
}
