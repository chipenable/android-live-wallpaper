package ru.chipenable.trenette.loader;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.chipenable.trenette.Trenette;

import static com.evernote.android.job.JobRequest.MIN_FLEX;
import static com.evernote.android.job.JobRequest.MIN_INTERVAL;

/**
 * Created by Pavel.B on 28.08.2017.
 */

public class ImgLoaderJob extends Job {

    public static final String TAG = "ImgLoaderJob";
    private final ImgLoader loader;

    public ImgLoaderJob(ImgLoader loader){
        super();
        this.loader = loader;
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.d(TAG, "onRunJob");
        loader.downloadImages()
                .subscribe(
                        aBoolean -> {},
                        throwable -> Log.e(TAG, "exception: ", throwable),
                        () -> {}
                );

        return Result.SUCCESS;
    }

    public static void schedulePeriodic() {
        new JobRequest.Builder(ImgLoaderJob.TAG)
                .setPeriodic(MIN_INTERVAL, MIN_FLEX)
                .setUpdateCurrent(true)
                .setPersisted(true)
                .build()
                .schedule();
    }

}
