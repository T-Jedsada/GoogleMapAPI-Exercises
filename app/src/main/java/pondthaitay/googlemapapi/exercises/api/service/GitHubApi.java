package pondthaitay.googlemapapi.exercises.api.service;

import io.reactivex.Observable;
import pondthaitay.googlemapapi.exercises.api.dao.UserInfoDao;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubApi {

    @GET("users/{userName}")
    Observable<Response<UserInfoDao>> getUserInfo(@Path("userName") String userName);
}