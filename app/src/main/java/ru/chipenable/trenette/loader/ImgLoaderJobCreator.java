package ru.chipenable.trenette.loader;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import javax.inject.Inject;

import ru.chipenable.trenette.Trenette;

import static java.security.AccessController.getContext;

/**
 * Created by Pavel.B on 28.08.2017.
 */

public class ImgLoaderJobCreator implements JobCreator {

    private final ImgLoader loader;

    public ImgLoaderJobCreator(ImgLoader loader){
        this.loader = loader;
    }

    @Override
    public Job create(String tag) {
        switch (tag) {
            case ImgLoaderJob.TAG:
                return new ImgLoaderJob(loader);
            default:
                return null;
        }
    }

}
