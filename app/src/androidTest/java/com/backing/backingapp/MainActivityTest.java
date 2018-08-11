package com.backing.backingapp;

import android.content.pm.ActivityInfo;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.backing.backingapp.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.backing.backingapp.activities.MainActivity.TAG_DETAILS;
import static com.backing.backingapp.activities.MainActivity.TAG_INGREDIENTS;
import static com.backing.backingapp.activities.MainActivity.TAG_RECIPE;
import static com.backing.backingapp.activities.MainActivity.TAG_STEP;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);
    FragmentManager fragmentManager;

    @Before
    public void init() {
        if (fragmentManager == null) {
            fragmentManager = activityTestRule.getActivity().getSupportFragmentManager();
        }
    }

    @Test
    public void clickOnRecipeTest() {
        Espresso.onView(withId(R.id.recyclerView_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Fragment fragment = fragmentManager.findFragmentByTag(TAG_DETAILS);
        assertNotNull(fragment);
    }

    @Test
    public void clickOnIngredientsTest() {
        Espresso.onView(withId(R.id.recyclerView_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Espresso.onView(withId(R.id.recyclerView_details)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Fragment fragment = fragmentManager.findFragmentByTag(TAG_INGREDIENTS);
        assertNotNull(fragment);
    }

    @Test
    public void clickOnStepTest() {
        Espresso.onView(withId(R.id.recyclerView_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Espresso.onView(withId(R.id.recyclerView_details)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Fragment fragment = fragmentManager.findFragmentByTag(TAG_STEP);
        assertNotNull(fragment);
    }

    @Test
    public void rotateScreenTest() {
        activityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Espresso.onView(withId(R.id.recyclerView_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        fragmentManager = activityTestRule.getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG_DETAILS);
        assertNotNull(fragment);
    }

    @Test
    public void backBtnTest() {
        Espresso.onView(withId(R.id.recyclerView_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Espresso.onView(withId(R.id.recyclerView_details)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Espresso.onView(isRoot()).perform(pressBack());
        Espresso.onView(isRoot()).perform(pressBack());
        Fragment fragmentRoot =   fragmentManager.findFragmentByTag(TAG_RECIPE);
        Fragment fragmentStep =   fragmentManager.findFragmentByTag(TAG_STEP);
        Fragment fragmentDetail = fragmentManager.findFragmentByTag(TAG_DETAILS);
        Fragment fragmentIngredients = fragmentManager.findFragmentByTag(TAG_INGREDIENTS);
        assertNull(fragmentStep);
        assertNull(fragmentDetail);
        assertNull(fragmentIngredients);
        assertNotNull(fragmentRoot);
    }

}
