package pondthaitay.googlemapapi.exercises.api.service;

import io.reactivex.Observable;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapApi {

    @GET("nearbysearch/json?")
    Observable<Response<NearbySearchDao>> nearbySearch(@Query("location") String location,
                                                       @Query("radius") int radius,
                                                       @Query("key") String key,
                                                       @Query("pagetoken") String token);
}
