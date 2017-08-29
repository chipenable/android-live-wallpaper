package ru.chipenable.trenette;

import android.app.Application;

import ru.chipenable.trenette.di.AppComponent;
import ru.chipenable.trenette.di.AppModule;
import ru.chipenable.trenette.di.DaggerAppComponent;

/**
 * Created by Pavel.B on 28.08.2017.
 */

public class Trenette extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = createAppComponent();
    }

    protected AppComponent createAppComponent(){
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent(){
        return component;
    }

}
