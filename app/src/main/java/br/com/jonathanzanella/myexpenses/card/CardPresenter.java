package br.com.jonathanzanella.myexpenses.card;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import org.joda.time.DateTime;

import java.util.List;

import br.com.jonathanzanella.myexpenses.MyApplication;
import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.account.Account;
import br.com.jonathanzanella.myexpenses.account.AccountRepository;
import br.com.jonathanzanella.myexpenses.account.ListAccountActivity;
import br.com.jonathanzanella.myexpenses.exceptions.InvalidMethodCallException;
import br.com.jonathanzanella.myexpenses.expense.Expense;
import br.com.jonathanzanella.myexpenses.expense.ExpenseRepository;
import br.com.jonathanzanella.myexpenses.validations.OperationResult;
import br.com.jonathanzanella.myexpenses.validations.ValidationError;

import static android.app.Activity.RESULT_OK;

class CardPresenter {
	private static final int REQUEST_SELECT_ACCOUNT = 1006;

	private CardContract.View view;
	@Nullable
	private CardContract.EditView editView;
	private CardRepository repository;
	private AccountRepository accountRepository;
	private ExpenseRepository expenseRepository;
	private Card card;
	private Account account;

	CardPresenter(CardRepository repository, AccountRepository accountRepository, ExpenseRepository expenseRepository) {
		this.repository = repository;
		this.accountRepository = accountRepository;
		this.expenseRepository = expenseRepository;
	}

	void attachView(CardContract.View view) {
		this.view = view;
	}

	void attachView(CardContract.EditView view) {
		this.view = view;
		this.editView = view;
	}

	void detachView() {
		this.view = null;
		this.editView = null;
	}

	@UiThread
	void viewUpdated(boolean invalidateCache) {
		if (card != null) {
			if(invalidateCache) {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... voids) {
						card = repository.find(card.getUuid());
						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						super.onPostExecute(aVoid);
						updateView();
					}
				}.execute();
			} else {
				updateView();
			}
		} else {
			updateView();
		}
	}

	void updateView() {
		if (card != null) {
			if (editView != null) {
				editView.setTitle(R.string.edit_card_title);
			} else {
				String title = view.getContext().getString(R.string.card);
				view.setTitle(title.concat(" ").concat(card.getName()));
			}
			view.showCard(card);
			if (account != null)
				editView.onAccountSelected(account);
		} else {
			if(editView != null)
				editView.setTitle(R.string.new_card_title);
		}
	}

	@WorkerThread
	void loadCard(String uuid) {
		card = repository.find(uuid);
		if(card == null)
			throw new CardNotFoundException(uuid);
	}

	@UiThread
	void save() {
		if(editView == null)
			throw new InvalidMethodCallException("save", getClass().toString(), "View should be a Edit View");
		if(card == null)
			card = new Card(accountRepository);
		card = editView.fillCard(card);
		if(account != null)
			card.setAccount(account);

		new AsyncTask<Void, Void, OperationResult>() {

			@Override
			protected OperationResult doInBackground(Void... voids) {
				return repository.save(card);
			}

			@Override
			protected void onPostExecute(OperationResult result) {
				super.onPostExecute(result);

				if(result.isValid()) {
					editView.finishView();
				} else {
					for (ValidationError validationError : result.getErrors())
						editView.showError(validationError);
				}
			}
		}.execute();
	}

	String getUuid() {
		return card != null ? card.getUuid() : null;
	}

	void showSelectAccountActivity(Activity act) {
		if(card == null)
			act.startActivityForResult(new Intent(act, ListAccountActivity.class), REQUEST_SELECT_ACCOUNT);
	}

	void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_SELECT_ACCOUNT: {
				if(resultCode == RESULT_OK) {
					new AsyncTask<String, Void, Account>() {

						@Override
						protected Account doInBackground(String... accountUuid) {
							if(accountUuid.length != 1) {
								throw new UnsupportedOperationException("Called with " + accountUuid.length +
										" uuids, it should be called with only one");
							}
							return accountRepository.find(accountUuid[0]);
						}

						@Override
						protected void onPostExecute(Account account) {
							super.onPostExecute(account);
							CardPresenter.this.account = account;
							if(account != null && editView != null) {
								editView.onAccountSelected(account);
							}
						}
					}.execute(data.getStringExtra(ListAccountActivity.Companion.getKEY_ACCOUNT_SELECTED_UUID()));
				}
				break;
			}
		}
	}

	Expense generateCreditCardBill() {
		List<Expense> expenses = repository.creditCardBills(card, DateTime.now().minusMonths(1));
		int totalExpense = 0;
		for (Expense expense : expenses) {
			totalExpense += expense.getValue();
			expense.setCharged(true);
			expenseRepository.save(expense);
		}

		if(totalExpense == 0)
			return null;

		Expense e = new Expense();
		e.setName(MyApplication.getContext().getString(R.string.invoice) + " " + card.getName());
		e.setValue(totalExpense);
		e.setChargeable(card.getAccount());
		expenseRepository.save(e);

		return e;
	}
}
