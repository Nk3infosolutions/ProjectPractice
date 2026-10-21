package com.example.book_curd;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class BookFormValidationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addBook_showsErrorsForEachMissingRequiredField() {
        onView(withId(R.id.add_book_button)).perform(click());
        onView(withId(R.id.book_name_input))
                .check(matches(hasErrorText("Enter a book name")));

        onView(withId(R.id.book_name_input)).perform(replaceText("Dune"), closeSoftKeyboard());
        onView(withId(R.id.add_book_button)).perform(click());
        onView(withId(R.id.book_author_input))
                .check(matches(hasErrorText("Enter an author")));

        onView(withId(R.id.book_author_input))
                .perform(replaceText("Frank Herbert"), closeSoftKeyboard());
        onView(withId(R.id.add_book_button)).perform(click());
        onView(withId(R.id.book_description_input))
                .check(matches(hasErrorText("Enter a description")));
    }
}
