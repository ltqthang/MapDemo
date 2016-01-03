package alphadev.demo.fragment;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import alphadev.demo.MainActivity;
import alphadev.demo.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.matches;

@RunWith(AndroidJUnit4.class)
public class HomeMapFragmentTest {
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void testDecodeLatLng() {
        onView(withId(R.id.mapFragment)).perform(longClick());
    }

    @Test
    public void testRoute() {
        // Select start to input start location
        onView(withText(R.string.start)).perform(click());
        onView(withId(R.id.searchView)).perform(click());

        onView(withId(android.R.id.content)).perform(ViewActions.swipeLeft());
        onView(withText(matches("Cần Thơ"))).perform(click());

        // Select end to input start location
        onView(withText(R.string.start)).perform(click());
        onView(withId(R.id.searchView)).perform(click());

        onView(withId(android.R.id.content)).perform(ViewActions.swipeRight());
        onView(withText(matches("Cần Thơ"))).perform(click());

        // Route
        onView(withId(R.id.goButton)).perform(click());
    }
}