package pondthaitay.googlemapapi.exercises.ui.main;

import java.util.List;

import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;
import pondthaitay.googlemapapi.exercises.ui.base.BaseInterface;

class MainInterface {

    interface View extends BaseInterface.View {

        void loadMoreError();

        void loadMoreSuccess(List<ResultNearbySearchDao> list);

        void loadMoreComplete();
    }

    interface Presenter {
        void searchNearby(String location, int radius, String key);

        List<ResultNearbySearchDao> sortListByName(List<ResultNearbySearchDao> list);
    }
}
