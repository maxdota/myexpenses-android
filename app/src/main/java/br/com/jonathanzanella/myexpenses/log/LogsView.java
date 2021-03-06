package br.com.jonathanzanella.myexpenses.log;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.views.BaseView;
import br.com.jonathanzanella.myexpenses.views.DateTimeView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogsView extends BaseView implements DateTimeView.Listener {
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
	private static final String LOG_TAG = LogsView.class.getSimpleName();

	@Bind(R.id.view_logs_list)
	RecyclerView logs;
	@Bind(R.id.view_log_init_time)
	DateTimeView initTime;
	@Bind(R.id.view_log_end_time)
	DateTimeView endTime;
	@Bind(R.id.view_logs_log_level)
	RadioGroup logLevel;

	private LogAdapter adapter;
	private String filter;

	public LogsView(Context context) {
		super(context);
	}

	public LogsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LogsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void init() {
		inflate(getContext(), R.layout.view_logs, this);
		ButterKnife.bind(this);

		adapter = new LogAdapter(new LogRepository(new RepositoryImpl<Log>(getContext())));
		logs.setAdapter(adapter);
		logs.setHasFixedSize(true);
		logs.setLayoutManager(new LinearLayoutManager(getContext()));
		logs.setNestedScrollingEnabled(false);

		initTime.setDate(DateTime.now().minusHours(1));
		initTime.setListener(this);
		endTime.setDate(DateTime.now());
		endTime.setListener(this);

		refreshAdapter();
	}

	@Override
	public void onDateTimeChanged(DateTime currentTime) {
		refreshAdapter();
	}

	@OnClick({R.id.view_logs_log_level_error, R.id.view_logs_log_level_warning,
			R.id.view_logs_log_level_info, R.id.view_logs_log_level_debug})
	public void onLogLevel() {
		refreshAdapter();
	}

	private void refreshAdapter() {
		adapter.loadData(initTime.getCurrentTime(), endTime.getCurrentTime(), getLogLevel(), filter);
		adapter.notifyDataSetChanged();
	}

	private Log.LogLevel getLogLevel() {
		switch (logLevel.getCheckedRadioButtonId()) {
			case R.id.view_logs_log_level_error:
				return Log.LogLevel.ERROR;
			case R.id.view_logs_log_level_warning:
				return Log.LogLevel.WARNING;
			case R.id.view_logs_log_level_info:
				return Log.LogLevel.INFO;
			case R.id.view_logs_log_level_debug:
				return Log.LogLevel.DEBUG;
		}

		Log.error(LOG_TAG, "new log level?");
		return Log.LogLevel.DEBUG;
	}

	@Override
	public void filter(String s) {
		super.filter(s);
		filter = s;
		refreshAdapter();
	}
}