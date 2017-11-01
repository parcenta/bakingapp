package com.peterark.bakingapp.bakingapp.utils;

import android.content.Context;
import android.util.Log;

import com.peterark.bakingapp.bakingapp.BuildConfig;
import com.peterark.bakingapp.bakingapp.helperStructures.Recipe;
import com.peterark.bakingapp.bakingapp.helperStructures.RecipeIngredient;
import com.peterark.bakingapp.bakingapp.helperStructures.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

/**
 * Created by PETER on 30/10/2017.
 */

public class BakingDataUtils {


    public static String syncRecipes(Context context) throws Exception{

        try {
            URL requestUrl = NetworkUtils.buildUrl(NetworkUtils.LOAD_RECIPE_LIST);

            String response = NetworkUtils
                    .getResponseFromHttpUrl(context, requestUrl);

            if(response==null) {
                return "No connection available.";
            }

            Timber.d("JsonString Response: " + response);

            List<Recipe> recipeList = BakingDataUtils.getBakingRecipeDataFromJson(response);



            return "";

        } catch (Exception e) {
            Timber.d("Exception was thrown when syncing baking recipes...");
            e.printStackTrace();
            if (BuildConfig.DEBUG)
                return "An error has ocurred when syncing recipes. " + e.getMessage();
            else
                return "An issue happened when syncing the recipes.";
        }
    }

    public static List<Recipe> getBakingRecipeDataFromJson(String dataJson) throws Exception{

        // Init recipe list.
        List<Recipe> recipeList = new ArrayList<>();

        // Convert the JsonString to JsonArray (Because it already is a list).
        JSONArray bakingJsonArray = new JSONArray(dataJson);

        // Loop MoviesJsonArray...
        for (int i = 0; i < bakingJsonArray.length(); i++) {

            // Get JsonObject.
            JSONObject oneRecipeJsonObject = bakingJsonArray.getJSONObject(i);

            // Extract Values from JsonObject.
            int recipeId            = oneRecipeJsonObject.getInt("id");
            String recipeName       = oneRecipeJsonObject.getString("name");
            int recipeServings      = oneRecipeJsonObject.getInt("servings");

            // Get the Ingredients.
            List<RecipeIngredient> ingredientsList = new ArrayList<>();
            JSONArray ingredientsArray = oneRecipeJsonObject.getJSONArray("ingredients");
            for (int j = 0; j < ingredientsArray.length(); j++) {
                JSONObject oneIngredient = ingredientsArray.getJSONObject(j);

                String name     = oneIngredient.getString("ingredient");
                double quantity = oneIngredient.getDouble("quantity");
                String measure  = oneIngredient.getString("measure");

                ingredientsList.add(new RecipeIngredient(name, quantity, measure));
            }

            // Get the Steps list.
            List<RecipeStep> stepsList = new ArrayList<>();
            JSONArray stepsArray = oneRecipeJsonObject.getJSONArray("steps");
            for (int j = 0; j < stepsArray.length(); j++) {
                JSONObject oneIngredient = ingredientsArray.getJSONObject(j);

                int stepId              = oneIngredient.getInt("id");
                String shortDescription = oneIngredient.getString("shortDescription");
                String description      = oneIngredient.getString("description");
                String videoUrl         = oneIngredient.getString("videoURL");
                String thumbnailUrl     = oneIngredient.getString("thumbnailURL");

                stepsList.add(new RecipeStep(stepId,shortDescription,description,videoUrl,thumbnailUrl));
            }

            // Add the recipe to the recipe list.
            recipeList.add(new Recipe(recipeId,
                                        recipeName,
                                        recipeServings,
                                        ingredientsList,
                                        stepsList));

        }

        return recipeList;

    }
}
