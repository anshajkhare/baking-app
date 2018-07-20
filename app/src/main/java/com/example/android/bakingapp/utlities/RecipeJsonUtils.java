package com.example.android.bakingapp.utlities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeJsonUtils {

    private static final String TAG = RecipeJsonUtils.class.getSimpleName();
    private static String[] recipeName = null;
    private static int[] servingValue = null;
    private static int[] idValues = null;
    private static JSONArray[] ingredientsList = null;
    private static JSONArray[] stepsList = null;

    public static void getRecipeDetailsFromJson(Context context, String movieJsonStr)
            throws JSONException {

        JSONArray recipeArray = new JSONArray(movieJsonStr);

        final String NAME_PATH = "name";
        final String ID_PATH = "id";
        final String SERVING_PATH = "servings";
        final String INGREDIENTS_PATH = "ingredients";
        final String STEPS_PATH = "steps";

        recipeName = new String[recipeArray.length()];
        servingValue = new int[recipeArray.length()];
        ingredientsList = new JSONArray[recipeArray.length()];
        stepsList = new JSONArray[recipeArray.length()];
        idValues = new int[recipeArray.length()];

        for (int i = 0; i < recipeArray.length(); i++) {

            JSONObject selectedRecipe = recipeArray.getJSONObject(i);

            JSONArray ingredient = selectedRecipe.getJSONArray(INGREDIENTS_PATH);
            ingredientsList[i] = ingredient;

            JSONArray step = selectedRecipe.getJSONArray(STEPS_PATH);
            stepsList[i] = step;

            if (selectedRecipe.has(NAME_PATH)) {
                recipeName[i] = selectedRecipe.getString(NAME_PATH);
            }
            if(selectedRecipe.has(SERVING_PATH)) {
                servingValue[i] = selectedRecipe.getInt(SERVING_PATH);
            }
            if(selectedRecipe.has(ID_PATH)) {
                idValues[i] = selectedRecipe.getInt(ID_PATH);
            }
        }
    }

    public static int[] getServingValue() {
        return servingValue;
    }

    public static String[] getRecipeName() {
        return recipeName;
    }

    public static JSONArray[] getIngredientsList() {
        return ingredientsList;
    }

    public static JSONArray[] getStepsList() {
        return stepsList;
    }

    public static int[] getIdValues() {
        return idValues;
    }
}
