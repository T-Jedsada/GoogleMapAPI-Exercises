package pondthaitay.googlemapapi.exercises.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pondthaitay.googlemapapi.exercises.ui.mylocattion.database.LocationDatabase;
import pondthaitay.googlemapapi.exercises.utils.DialogUtil;
import pondthaitay.googlemapapi.exercises.utils.NetworkUtil;
import pondthaitay.googlemapapi.exercises.utils.SortUtil;

@Module
public class UtilModule {

    @Provides
    @Singleton
    DialogUtil provideDialogUtil() {
        return new DialogUtil();
    }

    @Provides
    @Singleton
    NetworkUtil provideNetworkUtil(Context context) {
        return new NetworkUtil(context);
    }

    @Provides
    @Singleton
    SortUtil provideSortUtil() {
        return new SortUtil();
    }

    @Provides
    @Singleton
    LocationDatabase provideLocationDatabase(){
        return new LocationDatabase();
    }
}