package br.com.jonathanzanella.myexpenses.source;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.views.BaseActivity;
import butterknife.Bind;

public class ShowSourceActivity extends BaseActivity implements SourceContract.View {
	public static final String KEY_SOURCE_UUID = "KeySourceUuid";

	@Bind(R.id.act_show_source_name)
	TextView sourceName;

	private SourcePresenter presenter = new SourcePresenter(new SourceRepository(new RepositoryImpl<Source>(this)));

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_source);
	}

	@Override
	protected void storeBundle(final Bundle extras) {
		super.storeBundle(extras);

		if(extras != null && extras.containsKey(KEY_SOURCE_UUID)) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... voids) {
					presenter.loadSource(extras.getString(KEY_SOURCE_UUID));
					return null;
				}
			}.execute();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(KEY_SOURCE_UUID, presenter.getUuid());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		presenter.viewUpdated();
	}

	@Override
	protected void onStart() {
		super.onStart();
		presenter.attachView(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		presenter.detachView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				Intent i = new Intent(this, EditSourceActivity.class);
				i.putExtra(EditSourceActivity.KEY_SOURCE_UUID, presenter.getUuid());
				startActivity(i);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void showSource(Source source) {
		sourceName.setText(source.getName());
	}
}
