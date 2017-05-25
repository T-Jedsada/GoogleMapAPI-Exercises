package pondthaitay.googlemapapi.exercises.ui.main;

import java.util.List;

import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;
import pondthaitay.googlemapapi.exercises.ui.base.BaseInterface;

class MainInterface {

    interface View extends BaseInterface.View {

        void loadMoreError();

        void loadMoreSuccess(NearbySearchDao nearbySearchDao);

        void loadMoreComplete();

        void sortSuccess(NearbySearchDao dao);
    }

    interface Presenter {
        void searchNearby(String location, int radius, String key);

        List<ResultNearbySearchDao> sortListByName(List<ResultNearbySearchDao> list);

        void sortListByNameRx(List<ResultNearbySearchDao> list);
    }
}
