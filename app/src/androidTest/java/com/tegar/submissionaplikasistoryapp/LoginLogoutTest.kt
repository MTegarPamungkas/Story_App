package com.tegar.submissionaplikasistoryapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tegar.submissionaplikasistoryapp.ui.StartActivity
import com.tegar.submissionaplikasistoryapp.util.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.tegar.submissionaplikasistoryapp.ui.MainActivity
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation

@RunWith(AndroidJUnit4::class)
class LoginLogoutTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(StartActivity::class.java)


    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginLogout() {
        Intents.init()
        val context = getInstrumentation().targetContext
        onView(withId(R.id.btn_login_start)).perform(click())
        onView(withId(R.id.ed_email_login)).perform(typeText(EMAIL_USER))
        onView(withId(R.id.ed_password_login)).perform(typeText(PASS_USER))
        onView(withId(R.id.btn_login)).perform(click())
        EspressoIdlingResource.increment()
        Intents.intended(hasComponent(MainActivity::class.java.name))
        EspressoIdlingResource.decrement()

        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))

        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        val logout = context.getString(R.string.logout)
        onView(withText(logout)).perform(click())

        val yes = context.getString(R.string.yes)
        onView(withText(yes)).perform(click())

        onView(withId(R.id.img_start)).check(matches(isDisplayed()))
    }

    companion object {
        private const val EMAIL_USER = "tegar@gmail.com"
        private const val PASS_USER = "tegar123"
    }

}
