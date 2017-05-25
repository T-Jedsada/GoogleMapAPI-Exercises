package pondthaitay.googlemapapi.exercises.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import javax.inject.Inject;

import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;

public class LocationDatabase {

    private static final String NAME = "location_db";
    private SharedPreferences spf;
    private Gson mGson;

    @Inject
    public LocationDatabase(Context context, Gson gson) {
        spf = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        mGson = gson;
    }

    public void setJsonData(String location, NearbySearchDao dao) {
        spf.edit().putString(location, mGson.toJson(dao)).apply();
    }

    public NearbySearchDao getJsonData(String location) {
        String json = spf.getString(location, null);
        if (json == null) return null;
        return mGson.fromJson(json, NearbySearchDao.class);
    }
}
