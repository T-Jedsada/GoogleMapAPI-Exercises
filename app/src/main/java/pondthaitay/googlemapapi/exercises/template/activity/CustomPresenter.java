package pondthaitay.googlemapapi.exercises.template.activity;

import javax.inject.Inject;

import pondthaitay.googlemapapi.exercises.ui.base.BasePresenter;

class CustomPresenter extends BasePresenter<CustomInterface.View> implements CustomInterface.Presenter {

    @Inject
    CustomPresenter() {
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
    public void test() {
        getView().testResult();
    }
}
