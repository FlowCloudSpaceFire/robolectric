package org.robolectric.integration_tests.atsl;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Simple tests to verify espresso APIs can be used on Robolectric.
 */
@RunWith(AndroidJUnit4.class)
public final class EspressoTest {

  /**
   * Test activity that contains a single TextView
   */ 
  public static class ActivityFixture extends Activity {

    private int textViewId;
    private int buttonViewId;
    private boolean buttonClicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      LinearLayout layout = new LinearLayout(this);
      TextView textView = new TextView(this);
      this.textViewId = generateViewId();
      textView.setId(textViewId);
      textView.setEnabled(true);
      layout.addView(textView);

      Button button = new Button(this);
      buttonViewId = generateViewId();
      button.setId(buttonViewId);
      button.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          buttonClicked = true;
        }
      });
      layout.addView(button);

      setContentView(layout);
    }

    private static int generateViewId() {
      if (VERSION.SDK_INT >= 17) {
        return View.generateViewId();
      }
      return 0xbcbc;
    }
  }

  @Rule
  public ActivityTestRule<ActivityFixture> activityRule =
      new ActivityTestRule<>(ActivityFixture.class, false, true);

  @Test
  public void onIdle_doesnt_block() throws Exception {
    Espresso.onIdle();
  }

  @Test
  public void launchActivityAndFindView_ById() throws Exception {
    ActivityFixture activity = activityRule.getActivity();

    TextView textView = (TextView) activity.findViewById(activity.textViewId);
    assertThat(textView).isNotNull();
    assertThat(textView.isEnabled()).isTrue();
  }

  /**
   * Perform the equivalent of launchActivityAndFindView_ById except using espresso APIs
   */
  @Test
  public void launchActivityAndFindView_espresso() throws Exception {
    ActivityFixture activity = activityRule.getActivity();
    onView(withId(activity.textViewId)).check(matches(isEnabled()));
  }

  @Test
  public void buttonClick() throws Exception {
    ActivityFixture activity = activityRule.getActivity();
    Button button = activity.findViewById(activity.buttonViewId);
    button.performClick();
    assertThat(activity.buttonClicked).isTrue();
  }

  /**
   * Perform the equivalent of click except using espresso APIs
   */
  @Test
  public void buttonClick_espresso() throws Exception {
    ActivityFixture activity = activityRule.getActivity();
    onView(withId(activity.buttonViewId)).perform(click());
  }
}