package pondthaitay.googlemapapi.exercises;

import javax.inject.Singleton;

import dagger.Component;
import pondthaitay.googlemapapi.exercises.module.AndroidModule;
import pondthaitay.googlemapapi.exercises.module.ApiModule;
import pondthaitay.googlemapapi.exercises.module.NetworkModule;
import pondthaitay.googlemapapi.exercises.module.UtilModule;
import pondthaitay.googlemapapi.exercises.ui.MainActivity;
import pondthaitay.googlemapapi.exercises.ui.mylocattion.MyLocationActivity;

@Singleton
@Component(modules = {UtilModule.class, AndroidModule.class, NetworkModule.class, ApiModule.class})
public interface ApplicationComponent {
    void inject(MyApplication application);

    void inject(MyLocationActivity mainActivity);

    void inject(MainActivity mainActivity);
}