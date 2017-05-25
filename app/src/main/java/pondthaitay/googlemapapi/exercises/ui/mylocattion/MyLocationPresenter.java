package pondthaitay.googlemapapi.exercises.ui.mylocattion;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.BaseSubscriber;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.service.GoogleMapApi;
import pondthaitay.googlemapapi.exercises.ui.base.BasePresenter;

public class MyLocationPresenter extends BasePresenter<MyLocationInterface.View> implements
        MyLocationInterface.Presenter, BaseSubscriber.NetworkCallback {

    private GoogleMapApi googleMapApi;
    private CompositeDisposable disposables;
    private NearbySearchDao nearbySearchDao;

    @Inject
    MyLocationPresenter(GoogleMapApi googleMapApi) {
        super();
        this.googleMapApi = googleMapApi;
        this.disposables = new CompositeDisposable();
    }

    void setDisposables(CompositeDisposable mockDisposables) {
        this.disposables = mockDisposables;
    }

    void setNearbySearchDao(NearbySearchDao mockNearbyDao) {
        this.nearbySearchDao = mockNearbyDao;
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void onViewDestroy() {
        if (disposables != null) disposables.clear();
        nearbySearchDao = null;
    }

    @Override
    public void onViewStart() {

    }

    @Override
    public void onViewStop() {
        if (disposables != null) disposables.clear();
        nearbySearchDao = null;
    }

    @Override
    public void searchNearby(String location, int radius, String key) {
        if (getView() != null) {
            getView().showProgressDialog();
            disposables.add(googleMapApi.nearbySearch(location, radius, key, "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new BaseSubscriber<>(this)));
        }
    }

    @Override
    public <T> void onSuccess(T result) {
        if (getView() != null) {
            getView().hideProgressDialog();
            if (((NearbySearchDao) result).getList().isEmpty()) {
                getView().showError(R.string.please_try_again);
            } else {
                nearbySearchDao = (NearbySearchDao) result;
                getView().loadNearbySearchSuccess(getNearbySearchDao());
            }
        }
    }

    @Override
    public void onFailure(String message) {
        if (getView() != null) {
            getView().hideProgressDialog();
            getView().showError(message);
            getView().loadNearbySearchFromDB();
        }
    }

    NearbySearchDao getNearbySearchDao() {
        return nearbySearchDao;
    }
}