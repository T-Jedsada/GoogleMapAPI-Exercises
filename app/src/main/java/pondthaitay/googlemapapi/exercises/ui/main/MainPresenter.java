package pondthaitay.googlemapapi.exercises.ui.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pondthaitay.googlemapapi.exercises.api.BaseSubscriber;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.service.GoogleMapApi;
import pondthaitay.googlemapapi.exercises.ui.base.BasePresenter;
import timber.log.Timber;

class MainPresenter extends BasePresenter<MainInterface.View> implements
        MainInterface.Presenter, BaseSubscriber.NetworkCallback {

    private GoogleMapApi googleMapApi;
    private CompositeDisposable disposables;
    private NearbySearchDao nearbySearchDao;
    private boolean isEnableNextPage = true;

    @Inject
    MainPresenter(GoogleMapApi api) {
        super();
        this.googleMapApi = api;
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
    }

    @Override
    public void searchNearby(String location, int radius, String key) {
        if (getView() != null && isEnableNextPage()) {
            if (getTokenNextPage().length() > 0) {
                disposables.add(googleMapApi.nearbySearch(location, radius, key, getTokenNextPage())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new BaseSubscriber<>(this)));
            } else {
                Timber.e("searchNearby");
                getView().loadMoreComplete();
            }
        }
    }

    @Override
    public List<ResultNearbySearchDao> sortListByName(List<ResultNearbySearchDao> list) {
        if (!list.isEmpty() && getView() != null) {
            Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public <T> void onSuccess(T result) {
        if (getView() != null) {
            if (((NearbySearchDao) result).getList().isEmpty()) {
                setEnableNextPage(false);
                getView().loadMoreComplete();
            } else {
                nearbySearchDao = null;
                setEnableNextPage(true);
                insertNearbySearchList((NearbySearchDao) result);
            }
        }
    }

    @Override
    public void onFailure(String message) {
        if (getView() != null) {
            nearbySearchDao = null;
            getView().loadMoreError();
            setEnableNextPage(false);
        }
    }

    private void insertNearbySearchList(NearbySearchDao result) {
        nearbySearchDao = result;
        getView().loadMoreSuccess(getNearbySearchDao().getList());
    }

    private String getTokenNextPage() {
        if (nearbySearchDao == null || nearbySearchDao.getNextPageToken() == null) return "";
        else return nearbySearchDao.getNextPageToken();
    }

    NearbySearchDao getNearbySearchDao() {
        return nearbySearchDao;
    }

    boolean isEnableNextPage() {
        return isEnableNextPage;
    }

    private void setEnableNextPage(boolean enableNextPage) {
        isEnableNextPage = enableNextPage;
    }
}
