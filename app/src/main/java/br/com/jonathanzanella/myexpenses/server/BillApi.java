package br.com.jonathanzanella.myexpenses.server;

import com.raizlabs.android.dbflow.StringUtils;

import java.util.List;

import br.com.jonathanzanella.myexpenses.models.Bill;
import br.com.jonathanzanella.myexpenses.models.Source;
import br.com.jonathanzanella.myexpenses.models.UnsyncModel;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jzanella on 6/12/16.
 */
public class BillApi implements UnsyncModelApi<Bill> {
    BillInterface BillInterface;

    private BillInterface getInterface() {
        if(BillInterface == null)
            BillInterface = new Server().billInterface();
        return BillInterface;
    }

    @Override
    public void index(Subscriber<List<Bill>> subscriber) {
        Observable<List<Bill>> observable = getInterface().index(Source.greaterUpdatedAt());
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }

    @Override
    public void save(UnsyncModel model, Subscriber<Bill> subscriber) {
        Bill bill = (Bill) model;
        Observable<Bill> observable;
        if(StringUtils.isNotNullOrEmpty(bill.getServerId()))
            observable = getInterface().update(bill.getServerId(), bill);
        else
            observable = getInterface().create(bill);

        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }
}