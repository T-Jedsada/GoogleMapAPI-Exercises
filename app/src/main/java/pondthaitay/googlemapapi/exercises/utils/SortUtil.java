package pondthaitay.googlemapapi.exercises.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;

public class SortUtil {

    public List<ResultNearbySearchDao> sortListByName(List<ResultNearbySearchDao> list) {
        if (!list.isEmpty()) {
            Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));
            return list;
        } else {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unchecked")
    public Observable<List<ResultNearbySearchDao>> sortListByRx(List<ResultNearbySearchDao> list) {
        return Observable.just(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(list1 -> {
                    Collections.sort(list1, (o1, o2) -> o1.getName().compareTo(o2.getName()));
                    return list1;
                });
    }
}