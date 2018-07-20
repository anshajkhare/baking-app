package com.example.android.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class IdlingResourceMainActivityTest {

    private final String INGREDIENT_TEXT = "2 CUP of Graham Cracker crumbs";
    private final String STEP_TEXT = "Recipe Introduction";
    private final String DESCRIPTION_TEXT = "1. Preheat the oven to 350\u00b0F. Butter a 9\" deep dish pie pan.";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getmSimpleIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void BakingAppIdlingResourceTest_AllTests() {
        onView(withId(R.id.rv_recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.rv_ingredients)).check(matches(hasDescendant(withText(INGREDIENT_TEXT))));
        onView(withId(R.id.rv_steps)).check(matches(hasDescendant(withText(STEP_TEXT))));

        onView(withId(R.id.rv_steps)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.tv_current_step_description)).check(matches(withText(DESCRIPTION_TEXT)));
    }

    @After
    public void unregisterIdlingResource() {
        if(mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
