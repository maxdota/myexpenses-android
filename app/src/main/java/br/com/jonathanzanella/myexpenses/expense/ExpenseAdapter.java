package br.com.jonathanzanella.myexpenses.expense;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.lang.ref.WeakReference;
import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.bill.Bill;
import br.com.jonathanzanella.myexpenses.chargeable.Chargeable;
import br.com.jonathanzanella.myexpenses.database.RepositoryImpl;
import br.com.jonathanzanella.myexpenses.helpers.CurrencyHelper;
import butterknife.Bind;
import butterknife.ButterKnife;

class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
	private List<Expense> expenses;
	private ExpenseAdapterPresenter presenter;
	private DateTime date;
	private ExpenseRepository expenseRepository;

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.row_expense_name)
		TextView name;
		@Bind(R.id.row_expense_date)
		TextView date;
		@Bind(R.id.row_expense_value)
		TextView value;
		@Bind(R.id.row_expense_chargeable)
		TextView chargeable;
		@Bind(R.id.row_expense_bill_layout)
		View billLayout;
		@Bind(R.id.row_expense_bill)
		TextView billView;
		@Bind(R.id.row_expense_charge_next_month)
		TableRow chargeNextMonth;

		WeakReference<ExpenseAdapter> adapterWeakReference;

		public ViewHolder(View itemView, ExpenseAdapter adapter) {
			super(itemView);
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);

			itemView.setOnClickListener(this);
		}

		@UiThread
		public void setData(final Expense expense) {
			name.setText(expense.getName());
			date.setText(Expense.SIMPLE_DATE_FORMAT.format(expense.getDate().toDate()));
			value.setText(CurrencyHelper.format(expense.getValue()));
			new AsyncTask<Void, Void, Chargeable>() {

				@Override
				protected Chargeable doInBackground(Void... voids) {
					return expense.getChargeable();
				}

				@Override
				protected void onPostExecute(Chargeable c) {
					super.onPostExecute(c);
					chargeable.setText(c.getName());
				}
			}.execute();

			chargeNextMonth.setVisibility(expense.isChargedNextMonth() ? View.VISIBLE : View.GONE);
			new AsyncTask<Void, Void, Bill>() {

				@Override
				protected Bill doInBackground(Void... voids) {
					return expense.getBill();
				}

				@Override
				protected void onPostExecute(Bill bill) {
					super.onPostExecute(bill);
					if(bill == null) {
						billLayout.setVisibility(View.GONE);
					} else {
						billLayout.setVisibility(View.VISIBLE);
						billView.setText(bill.getName());
					}
				}
			}.execute();
		}

		@Override
		public void onClick(View v) {
			Expense expense = adapterWeakReference.get().getExpense(getAdapterPosition());
			if(expense != null) {
                Intent i = new Intent(itemView.getContext(), ShowExpenseActivity.class);
                i.putExtra(ShowExpenseActivity.KEY_EXPENSE_UUID, expense.getUuid());
                itemView.getContext().startActivity(i);
			}
		}
	}

	ExpenseAdapter() {
		expenseRepository = new ExpenseRepository(new RepositoryImpl<Expense>(MyApplication.getContext()));
		presenter = new ExpenseAdapterPresenter(this, expenseRepository);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_expense, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(getExpense(position));
	}

	@Override
	public int getItemCount() {
		return expenses != null ? expenses.size() : 0;
	}

	@WorkerThread
	public void loadData(DateTime date) {
		expenses = expenseRepository.monthly(date);
		expenses = presenter.getExpenses(true, date);
		this.date = date;
	}

	void addExpense(@NonNull Expense expense) {
		presenter.addExpense(expense);
		expenses = presenter.getExpenses(false, date);
	}

	@Nullable
	private Expense getExpense(int position) {
		return expenses != null ? expenses.get(position) : null;
	}

	public void filter(String filter) {
		presenter.filter(filter);
		expenses = presenter.getExpenses(false, date);
	}
}