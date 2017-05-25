package pondthaitay.googlemapapi.exercises.ui.main;

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
import pondthaitay.googlemapapi.exercises.utils.SortUtil;

class MainPresenter extends BasePresenter<MainInterface.View> implements
        MainInterface.Presenter, BaseSubscriber.NetworkCallback {

    private GoogleMapApi googleMapApi;
    private CompositeDisposable disposables;
    private NearbySearchDao nearbySearchDao;
    private boolean isEnableNextPage = true;
    private SortUtil sortUtil;

    @Inject
    MainPresenter(GoogleMapApi api, SortUtil sortUtil) {
        super();
        this.googleMapApi = api;
        this.sortUtil = sortUtil;
        this.disposables = new CompositeDisposable();
    }

    void setDisposables(CompositeDisposable mockDisposables) {
        this.disposables = mockDisposables;
    }

    void setNearbySearchDao(NearbySearchDao mockNearbyDao) {
        this.nearbySearchDao = mockNearbyDao;
    }

    void setSortUtil(SortUtil mockSortUtil) {
        this.sortUtil = mockSortUtil;
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void onViewDestroy() {
        if (disposables != null) disposables.clear();
        setNearbySearchDao(null);
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
                getView().loadMoreComplete();
            }
        }
    }

    @Override
    public List<ResultNearbySearchDao> sortListByName(List<ResultNearbySearchDao> list) {
        return sortUtil.sortListByName(list);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sortListByNameRx(List<ResultNearbySearchDao> list) {
        if (getView() != null && !list.isEmpty()) {
            getView().showProgressDialog();
            sortUtil.sortListByRx(list)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newList -> {
                        nearbySearchDao.setList(newList);
                        setNearbySearchDao(nearbySearchDao);
                        getView().hideProgressDialog();
                        getView().sortSuccess(getNearbySearchDao());
                    });
        }
    }

    @Override
    public <T> void onSuccess(T result) {
        if (getView() != null) {
            if (((NearbySearchDao) result).getList().isEmpty()) {
                setNearbySearchDaoLoadComplete(false);
            } else {
                setEnableNextPage(true);
                insertNearbySearchList((NearbySearchDao) result);
            }
        }
    }

    @Override
    public void onFailure(String message) {
        if (getView() != null) {
            setNearbySearchDaoLoadComplete(true);
        }
    }

    private void insertNearbySearchList(NearbySearchDao result) {
        setNearbySearchDao(result);
        getView().loadMoreSuccess(getNearbySearchDao());
    }

    private void setNearbySearchDaoLoadComplete(boolean error) {
        setEnableNextPage(false);
        nearbySearchDao.setNextPageToken(null);
        setNearbySearchDao(nearbySearchDao);
        if (error) {
            getView().loadMoreError();
        } else {
            getView().loadMoreComplete();
        }
    }

    String getTokenNextPage() {
        if (getNearbySearchDao() == null || getNearbySearchDao().getNextPageToken() == null) {
            return "";
        } else {
            return getNearbySearchDao().getNextPageToken();
        }
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