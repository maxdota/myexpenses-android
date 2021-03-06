package br.com.jonathanzanella.myexpenses.source;

import android.content.Intent;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.DatabaseHelper;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static br.com.jonathanzanella.myexpenses.helpers.UIHelper.matchToolbarTitle;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class ShowSourceActivityTest {
	@Rule
	public ActivityTestRule<ShowSourceActivity> activityTestRule = new ActivityTestRule<>(ShowSourceActivity.class, true, false);

	private Source source;
	private SourceRepository repository = new SourceRepository(new RepositoryImpl<Source>(getTargetContext()));

	@Before
	public void setUp() throws Exception {
		source = new Source();
		source.setName("test");
		repository.save(source);
	}

	@After
	public void tearDown() throws Exception {
		new DatabaseHelper(getTargetContext()).recreateTables();
	}

	@Test
	public void shows_account_correctly() throws Exception {
		Intent i = new Intent();
		i.putExtra(ShowSourceActivity.KEY_SOURCE_UUID, source.getUuid());
		activityTestRule.launchActivity(i);

		final String editSourceTitle = getTargetContext().getString(R.string.source) + " " + source.getName();
		matchToolbarTitle(editSourceTitle);

		onView(withId(R.id.act_show_source_name)).check(matches(withText(source.getName())));
	}
}