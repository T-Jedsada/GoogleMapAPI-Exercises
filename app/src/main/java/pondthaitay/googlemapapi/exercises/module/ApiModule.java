package pondthaitay.googlemapapi.exercises.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import pondthaitay.googlemapapi.exercises.api.service.GitHubApi;
import retrofit2.Retrofit;

@Module
public class ApiModule {

    @Provides
    @Singleton
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    @Singleton
    Class<GitHubApi> providesGithubService() {
        NetworkModule.setBaseUrl("https://api.githu.com/");
        return GitHubApi.class;
    }

    @Provides
    @Singleton
    GitHubApi providesGithubService1(Class<GitHubApi> gitHubApi, Retrofit retrofit) {
        return retrofit.create(gitHubApi);
    }
}