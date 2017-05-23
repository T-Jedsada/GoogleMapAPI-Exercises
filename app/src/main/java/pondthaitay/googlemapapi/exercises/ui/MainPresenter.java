package pondthaitay.googlemapapi.exercises.ui;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.BaseSubscriber;
import pondthaitay.googlemapapi.exercises.api.dao.UserInfoDao;
import pondthaitay.googlemapapi.exercises.api.service.GitHubApi;
import pondthaitay.googlemapapi.exercises.ui.base.BasePresenter;
import pondthaitay.googlemapapi.exercises.ui.event.TestBusEvent;

import static android.R.id.message;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainPresenter extends BasePresenter<MainInterface.View> implements MainInterface.Presenter {

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
}