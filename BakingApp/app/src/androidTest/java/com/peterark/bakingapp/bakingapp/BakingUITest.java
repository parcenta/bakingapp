package com.peterark.bakingapp.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.peterark.bakingapp.bakingapp.panels.RecipeDetailActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * Created by PETER on 20/11/2017.
 */

// Based in: https://github.com/googlesamples/android-testing/blob/master/ui/espresso/IdlingResourceSample/app/src/androidTest/java/com/example/android/testing/espresso/IdlingResourceSample/ChangeTextBehaviorTest.java

@RunWith(AndroidJUnit4.class)
public class BakingUITest {

    private static String RECIPE_NAME_TO_TESTED                     = "Nutella Pie";  // Must be an existant recipe from the WS.
    private static String RECIPE_DETAIL_INGREDIENT_NAME_TO_TESTED   = "Nutella or other chocolate-hazelnut spread"; // It was the only ingredient unique to this recipe.
    private static String RECIPE_DETAIL_STEP_NAME_TO_TESTED         = "Prep the cookie crust"; // Choosing a random step

    @Rule
    public ActivityTestRule<RecipeMainActivity> mRecipeListActivityRule = new ActivityTestRule<>(RecipeMainActivity.class);

    private IdlingResource recipeListActivityIdlingResource;

    @Before
    public void registerIdlingResource() {

        // Creating and registering the IdlingResourse in the RecipeMainActivity
        recipeListActivityIdlingResource = mRecipeListActivityRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(recipeListActivityIdlingResource);
    }

    @Test
    public void chooseRecipeFromList_And_CheckSelectedRecipeDetails() {
        // At start the RecipeMainActivity activity will be not idle because will loading data from network and DB.
        // So the Next action performed in the recylcer view will be executed as soon as the acitivty finished loading the data
        // Source: https://developer.android.com/training/testing/espresso/lists.html
        // Source 2(About hasDescendant solution): https://stackoverflow.com/questions/31394569/how-to-assert-inside-a-recyclerview-in-espresso
        onView(ViewMatchers.withId(R.id.recipe_list_recycler_view))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(RECIPE_NAME_TO_TESTED)),
                        click()));

        // Check that the RecipeName TextView and the Ingrediente contains the data that we expected.
        onView(withId(R.id.selected_recipe_name_textview)).check(matches(withText(RECIPE_NAME_TO_TESTED)));
        onView(withId(R.id.ingredients_textarea)).check(matches(withText(containsString(RECIPE_DETAIL_INGREDIENT_NAME_TO_TESTED))));

        // Press in the Introduction Step (normally has a video)
        onView(ViewMatchers.withId(R.id.recipe_detail_step_recyclerview))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText(containsString(RECIPE_DETAIL_STEP_NAME_TO_TESTED))),
                        click()));


        onView(withId(R.id.recipe_short_description_textview)).check(matches(withText(containsString(RECIPE_DETAIL_STEP_NAME_TO_TESTED))));
        onView(withId(R.id.no_media_available_textview)).check(matches(not(isDisplayed())));

        //pressBack();
    }

    // Unregistering Idling Resource
    @After
    public void unregisterIdlingResource() {
        if (recipeListActivityIdlingResource != null) {
            Espresso.unregisterIdlingResources(recipeListActivityIdlingResource);
        }
    }
}
