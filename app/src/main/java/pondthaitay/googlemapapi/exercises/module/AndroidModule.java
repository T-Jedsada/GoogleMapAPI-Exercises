package pondthaitay.googlemapapi.exercises.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pondthaitay.googlemapapi.exercises.MyApplication;
import pondthaitay.googlemapapi.exercises.configuration.BuildConfiguration;
import pondthaitay.googlemapapi.exercises.configuration.Config;

import static android.content.Context.LOCATION_SERVICE;

@Module
public class AndroidModule {
    private final MyApplication application;

    public AndroidModule(MyApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    LocationManager provideLocationManager() {
        return (LocationManager) application.getSystemService(LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    Resources provideResources() {
        return application.getResources();
    }

    @Provides
    @Singleton
    Config provideConfig() {
        return new BuildConfiguration();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}