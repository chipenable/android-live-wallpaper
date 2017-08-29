package ru.chipenable.trenette.di;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Pavel.B on 29.08.2017.
 */

public class RxSchedulers {

    private final Scheduler ioScheduler;
    private final Scheduler uiScheduler;
    private final Scheduler compScheduler;

    public RxSchedulers(){
        ioScheduler = Schedulers.io();
        uiScheduler = AndroidSchedulers.mainThread();
        compScheduler = Schedulers.computation();
    }

    public Scheduler getIoScheduler() {
        return ioScheduler;
    }

    public Scheduler getUiScheduler() {
        return uiScheduler;
    }

    public Scheduler getCompScheduler() {
        return compScheduler;
    }
}
