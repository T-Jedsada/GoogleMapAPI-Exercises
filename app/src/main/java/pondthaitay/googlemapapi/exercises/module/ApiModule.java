package pondthaitay.googlemapapi.exercises.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pondthaitay.googlemapapi.exercises.api.service.GoogleMapApi;
import retrofit2.Retrofit;

@Module
public class ApiModule {

    @Provides
    @Singleton
    GoogleMapApi providesGoogleMapApi(Retrofit retrofit) {
        return retrofit.create(GoogleMapApi.class);
    }
}