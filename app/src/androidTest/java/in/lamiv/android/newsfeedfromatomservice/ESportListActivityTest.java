package in.lamiv.android.newsfeedfromatomservice;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.rules.*;

import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.*;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
/**
 * Created by vimal on 10/13/2016.
 */
@RunWith(AndroidJUnit4.class)
public class ESportListActivityTest {

    @Test
    public void onCreate() throws Exception {

    }

    @Test
    public void refreshOnClick() throws Exception {
//        onView(withId(R.id.refreshButton)).perform(ViewActions.click()).check(ViewAssertions.matches(isDisplayed()));
    }

}