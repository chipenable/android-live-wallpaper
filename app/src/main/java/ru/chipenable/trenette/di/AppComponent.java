package ru.chipenable.trenette.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.chipenable.trenette.wallpaper.CustomWallpaperService;
import ru.chipenable.trenette.loader.ImgLoaderJob;

/**
 * Created by Pavel.B on 28.08.2017.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(CustomWallpaperService obj);
    void inject(ImgLoaderJob obj);

}
