package pondthaitay.googlemapapi.exercises.ui.mylocattion;

import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.ui.base.BaseInterface;

class MyLocationInterface {

    interface View extends BaseInterface.View {

        void loadNearbySearchSuccess(NearbySearchDao result);
    }

    interface Presenter {

        void searchNearby(String location, int radius, String key);
    }
}