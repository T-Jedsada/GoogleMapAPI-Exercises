package pondthaitay.googlemapapi.exercises.ui.main;

import java.util.List;

import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;
import pondthaitay.googlemapapi.exercises.ui.base.BaseInterface;

class MainInterface {

    interface View extends BaseInterface.View {

    }

    interface Presenter {
        List<ResultNearbySearchDao> sortListByName(List<ResultNearbySearchDao> list);
    }
}
