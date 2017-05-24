package pondthaitay.googlemapapi.exercises.ui.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;
import pondthaitay.googlemapapi.exercises.ui.base.BasePresenter;

class MainPresenter extends BasePresenter<MainInterface.View> implements MainInterface.Presenter {

    @Inject
    MainPresenter() {
        super();
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void onViewDestroy() {

    }

    @Override
    public void onViewStart() {

    }

    @Override
    public void onViewStop() {

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
}
