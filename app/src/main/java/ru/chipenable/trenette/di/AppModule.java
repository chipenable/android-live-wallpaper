package ru.chipenable.trenette.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import ru.chipenable.trenette.BuildConfig;
import ru.chipenable.trenette.wallpaper.WallpaperController;
import ru.chipenable.trenette.loader.ImgLoader;
import ru.chipenable.trenette.repo.IImageRepo;
import ru.chipenable.trenette.repo.ImageRepo;

/**
 * Created by Pavel.B on 28.08.2017.
 */

@Module
public class AppModule {

    private Context context;

    public AppModule(Context context){
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideContext(){
        return context;
    }

    @Provides
    @Singleton
    public RxSchedulers provideSchedulers(){
        return new RxSchedulers();
    }

    @Provides
    @Singleton
    public ImgLoader provideImgLoader(Context context, RxSchedulers schedulers){
        return new ImgLoader(BuildConfig.BASE_URL, new OkHttpClient(), context, schedulers);
    }

    @Provides
    @Singleton
    public IImageRepo provideImageRepo(ImgLoader loader, Context context){
        return new ImageRepo(loader, context);
    }

    @Provides
    @Singleton
    public WallpaperController provideWallpaperController(IImageRepo repo, RxSchedulers schedulers){
        return new WallpaperController(repo, schedulers);
    }

}
