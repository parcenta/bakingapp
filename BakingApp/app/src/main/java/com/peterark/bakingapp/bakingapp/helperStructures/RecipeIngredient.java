package com.peterark.bakingapp.bakingapp.helperStructures;

/**
 * Created by PETER on 30/10/2017.
 */

public class RecipeIngredient {

    public final String ingredientName;
    public final double ingredientQuantity;
    public final String ingredientMeasure;

    public RecipeIngredient(String ingredientName, double ingredientQuantity, String ingredientMeasure){
        this.ingredientName     = ingredientName;
        this.ingredientQuantity = ingredientQuantity;
        this.ingredientMeasure  = ingredientMeasure;
    }
}
