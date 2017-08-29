package ru.chipenable.trenette.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.evernote.android.job.JobManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.chipenable.trenette.di.RxSchedulers;

/**
 * Created by Pavel.B on 28.08.2017.
 */

public class ImgLoader {

    public enum ImgLoaderEvent {
        SUBSCRIBED,
        UPDATED
    }

    public static final String TAG = ImgLoader.class.getName();
    public static final String IMGES_KEY = "images";

    private final String baseUrl;
    private final OkHttpClient client;
    private final Context context;
    private final RxSchedulers schedulers;
    private final PublishSubject<ImgLoaderEvent> publishSubject;

    public ImgLoader(String baseUrl, OkHttpClient client,
                     Context context, RxSchedulers schedulers){
        this.baseUrl = baseUrl;
        this.client = client;
        this.context = context;
        this.schedulers = schedulers;
        this.publishSubject = PublishSubject.create();
    }

    public Observable<Boolean> startLoaderPeriodically(){
        //periodic task
        JobManager.create(context)
                .addJobCreator(new ImgLoaderJobCreator(this));
        ImgLoaderJob.schedulePeriodic();

        //download images for the first time
        return downloadImages();
    }

    public Observable<Boolean> downloadImages() {
        return Observable.fromCallable(() -> {
            Log.d(TAG, "download images");

            List<String> imgList = syncDownloadImgList(baseUrl);

            int count = 0;
            for(String imgUrl: imgList){
                if (!Util.isUrlValid(imgUrl)) {
                    continue;
                }

                String imgName = Util.parseImageName(imgUrl);
                if (TextUtils.isEmpty(imgName)){
                    continue;
                }

                Bitmap bitmap = syncDownloadImg(imgUrl);
                //TODO scale img before saving

                if (Util.saveBitmapAsFile(context, bitmap, imgName)){
                    count++;
                }
            }

            if (count > 0) {
                publishSubject.onNext(ImgLoaderEvent.UPDATED);
            }

            return count > 0;
        }).subscribeOn(schedulers.getIoScheduler())
                .observeOn(schedulers.getUiScheduler());
    }

    private List<String> syncDownloadImgList(String url) throws IOException, JSONException{
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();

        List<String> list = new ArrayList<>();
        if (response.isSuccessful()) {
            JSONObject object = new JSONObject(response.body().string());
            JSONArray array = object.getJSONArray(IMGES_KEY);

            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        }
        return list;
    }

    private Bitmap syncDownloadImg(String photoUrl) throws IOException {
        Request request = new Request.Builder()
                .url(photoUrl)
                .build();
        Response response = client.newCall(request).execute();
        InputStream input = response.body().byteStream();
        return BitmapFactory.decodeStream(input);
    }

    public Observable<ImgLoaderEvent> subscribeToEvents(){
        return Observable.concat(Observable.just(ImgLoaderEvent.SUBSCRIBED), publishSubject)
                .subscribeOn(schedulers.getIoScheduler())
                .observeOn(schedulers.getUiScheduler());
    }

}

