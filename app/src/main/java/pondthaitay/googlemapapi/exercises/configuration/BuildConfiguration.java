package pondthaitay.googlemapapi.exercises.configuration;

import pondthaitay.googlemapapi.exercises.BuildConfig;

public class BuildConfiguration implements Config {
    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @Override
    public String version() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public String userToken() {
        return null;
    }
}