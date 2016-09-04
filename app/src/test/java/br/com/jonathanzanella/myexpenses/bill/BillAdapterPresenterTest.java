package br.com.jonathanzanella.myexpenses.bill;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by jzanella on 8/27/16.
 */
public class BillAdapterPresenterTest {
	@Mock
	private BillAdapter adapter;
	@Mock
	private BillRepository repository;

	BillAdapterPresenter presenter;

	@Before
	public void setUp() throws Exception {
		initMocks(this);

		presenter = new BillAdapterPresenter(adapter, repository);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void get_sources_return_unmodifiable_list() {
		when(repository.userBills()).thenReturn(new ArrayList<Bill>());

		List<Bill> sources = presenter.getBills(false);
		sources.add(new Bill());
	}
}