package pondthaitay.googlemapapi.exercises.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pondthaitay.googlemapapi.exercises.utils.DialogUtil;
import pondthaitay.googlemapapi.exercises.utils.NetworkUtil;

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
}