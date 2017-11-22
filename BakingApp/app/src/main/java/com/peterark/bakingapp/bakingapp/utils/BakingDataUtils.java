package com.peterark.bakingapp.bakingapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.peterark.bakingapp.bakingapp.BuildConfig;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeIngredientContract;
import com.peterark.bakingapp.bakingapp.database.contracts.RecipeStepContract;
import com.peterark.bakingapp.bakingapp.helperStructures.Recipe;
import com.peterark.bakingapp.bakingapp.helperStructures.RecipeIngredient;
import com.peterark.bakingapp.bakingapp.helperStructures.RecipeStep;
import com.peterark.bakingapp.bakingapp.widget.BakingIntentService;
import com.peterark.bakingapp.bakingapp.widget.BakingWidget;

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

            if(response==null)
                return "No connection available.";

            Timber.d("JsonString Response: " + response);

            // Convert the JSON to a Recipe class object.
            List<Recipe> recipeList = BakingDataUtils.getBakingRecipeDataFromJson(response);

            // First delete previous data saved in DB tables.
            context.getContentResolver().delete(RecipeIngredientContract.RecipeIngredientEntry.CONTENT_URI,null,null);
            context.getContentResolver().delete(RecipeStepContract.RecipeStepEntry.CONTENT_URI,null,null);
            context.getContentResolver().delete(RecipeContract.RecipeEntry.CONTENT_URI,null,null);

            // Now insert the data from the Recipe class object.
            for (Recipe oneRecipe : recipeList){

                // Insert Recipe Header
                ContentValues cv = new ContentValues();
                cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID,oneRecipe.recipeId);
                cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME,oneRecipe.recipeName);
                cv.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS,oneRecipe.recipeServings);
                context.getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI,cv);

                // Insert the Ingredients
                for(RecipeIngredient oneIngredient : oneRecipe.recipeIngredients){
                    cv = new ContentValues();
                    cv.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_ID,oneRecipe.recipeId);
                    cv.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_NAME,oneIngredient.ingredientName);
                    cv.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_MEASURE,oneIngredient.ingredientMeasure);
                    cv.put(RecipeIngredientContract.RecipeIngredientEntry.COLUMN_RECIPE_INGREDIENT_QUANTITY,oneIngredient.ingredientQuantity);
                    context.getContentResolver().insert(RecipeIngredientContract.RecipeIngredientEntry.CONTENT_URI,cv);
                }

                // Insert the Steps
                for(RecipeStep oneStep : oneRecipe.recipeSteps){
                    cv = new ContentValues();
                    cv.put(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_ID,oneRecipe.recipeId);
                    cv.put(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_ID,oneStep.stepId);
                    cv.put(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_SHORT_DESCRIPTION,oneStep.stepShortDescription);
                    cv.put(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_DESCRIPTION,oneStep.stepDescription);
                    cv.put(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_THUMBNAIL_URL,oneStep.stepThumbnailUrl);
                    cv.put(RecipeStepContract.RecipeStepEntry.COLUMN_RECIPE_STEP_VIDEO_URL,oneStep.stepVideoUrl);
                    context.getContentResolver().insert(RecipeStepContract.RecipeStepEntry.CONTENT_URI,cv);
                }
            }

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

        Timber.d("Received JSON: %s",dataJson);

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
                JSONObject oneStep = stepsArray.getJSONObject(j);

                int stepId              = oneStep.getInt("id");
                String shortDescription = oneStep.getString("shortDescription");
                String description      = oneStep.getString("description");
                String videoUrl         = oneStep.getString("videoURL");
                String thumbnailUrl     = oneStep.getString("thumbnailURL");

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

    public static int getWidgetSelectedRecipeId(Context context){
        SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        if (prefs!=null)
            return prefs.getInt(BakingWidget.WIDGET_SELECTED_RECIPE_ID,0);
        else
            return 0;
    }

    public static void setWidgetSelectedRecipeId(Context context,int recipeId){
        SharedPreferences prefs = context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BakingWidget.WIDGET_SELECTED_RECIPE_ID, recipeId);
        editor.commit();
    }
}
