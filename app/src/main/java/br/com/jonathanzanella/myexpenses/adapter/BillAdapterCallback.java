package br.com.jonathanzanella.myexpenses.adapter;

import br.com.jonathanzanella.myexpenses.model.Bill;

/**
 * Created by jzanella on 2/1/16.
 */
public interface BillAdapterCallback {
	void onBillSelected(Bill bill);
}