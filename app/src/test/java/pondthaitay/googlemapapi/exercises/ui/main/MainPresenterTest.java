package pondthaitay.googlemapapi.exercises.ui.main;

import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.service.GoogleMapApi;
import pondthaitay.googlemapapi.exercises.utils.JsonMockUtility;
import pondthaitay.googlemapapi.exercises.utils.RxSchedulersOverrideRule;
import retrofit2.Response;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, CompositeDisposable.class})
public class MainPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule schedulers = new RxSchedulersOverrideRule();

    @Mock
    MainInterface.View mockView;
    @Mock
    GoogleMapApi mockGoogleMapApi;
    @Mock
    private CompositeDisposable mockDisposable;

    private MainPresenter presenter;
    private JsonMockUtility jsonUtil;
    private ResponseBody responseBody;

    @Before
    public void setUp() throws Exception {
        jsonUtil = new JsonMockUtility();
        MockitoAnnotations.initMocks(this);
        presenter = new MainPresenter(mockGoogleMapApi);
        presenter.attachView(mockView);
        presenter.setDisposables(mockDisposable);
        presenter.setNearbySearchDao(new NearbySearchDao());

        MainPresenter spyPresenter = spy(presenter);
        spyPresenter.setDisposables(mockDisposable);
        spyPresenter.attachView(mockView);

        responseBody = ResponseBody.create(MediaType.parse("application/json"), "");
    }

    @Test
    public void onViewCreate() throws Exception {
        presenter.onViewCreate();
    }

    @Test
    public void onViewDestroy() throws Exception {
        presenter.onViewDestroy();
        verify(mockDisposable, times(1)).clear();
        Assert.assertNull(presenter.getNearbySearchDao());
    }

    @Test
    public void onViewStart() throws Exception {
        presenter.onViewStart();
    }

    @Test
    public void onViewStop() throws Exception {
        presenter.onViewStop();
    }

    @Test
    public void testSortListByNameEn() throws Exception {
        List<ResultNearbySearchDao> list = presenter.sortListByName(getListNameEn());
        String[] az = new String[6];
        String[] azActuals = {"A", "B", "C", "D", "F", "a"};
        for (int i = 0; i < list.size(); i++) {
            az[i] = list.get(i).getName();
        }

        Assert.assertArrayEquals(az, azActuals);
    }

    @Test
    public void testSortListByNameTh() throws Exception {
        List<ResultNearbySearchDao> list = presenter.sortListByName(getListNameTh());
        String[] az = new String[6];
        String[] azActuals = {"y", "z", "ก", "ข", "ค", "ง"};
        for (int i = 0; i < list.size(); i++) {
            az[i] = list.get(i).getName();
        }

        Assert.assertArrayEquals(az, azActuals);
    }

    @Test
    public void testSortListEmpty() throws Exception {
        List<ResultNearbySearchDao> list = presenter.sortListByName(new ArrayList<>());
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void searchNearbySuccess() throws Exception {
        NearbySearchDao mockResult = jsonUtil.getJsonToMock(
                "nearby_search_success.json",
                NearbySearchDao.class);

        Response<NearbySearchDao> mockResponse = Response.success(mockResult);
        Observable<Response<NearbySearchDao>> mockObservable = Observable.just(mockResponse);
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockDisposable, times(1)).add(anyObject());

        TestObserver<Response<NearbySearchDao>> testObserver = mockObservable.test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).loadMoreSuccess(eq(mockResult.getList()));
            assertThat(response, is(mockResponse));
            assertEquals(true, presenter.isEnableNextPage());
            return true;
        });
    }

    @Test
    public void searchNearbyMoreSuccess() throws Exception {
        NearbySearchDao mockResult = jsonUtil.getJsonToMock(
                "nearby_search_success.json",
                NearbySearchDao.class);

        Response<NearbySearchDao> mockResponse = Response.success(mockResult);
        Observable<Response<NearbySearchDao>> mockObservable = Observable.just(mockResponse);
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockDisposable, times(1)).add(anyObject());

        TestObserver<Response<NearbySearchDao>> testObserver = mockObservable.test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).loadMoreSuccess(eq(mockResult.getList()));
            assertThat(response, is(mockResponse));
            assertEquals(true, presenter.isEnableNextPage());
            return true;
        });
    }

    @Test
    public void searchNearbyEmpty() throws Exception {
        NearbySearchDao mockResult = jsonUtil.getJsonToMock(
                "nearby_search_empty_result.json",
                NearbySearchDao.class);

        Response<NearbySearchDao> mockResponse = Response.success(mockResult);
        Observable<Response<NearbySearchDao>> mockObservable = Observable.just(mockResponse);
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockDisposable, times(1)).add(anyObject());

        TestObserver<Response<NearbySearchDao>> testObserver = mockObservable.test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).loadMoreComplete();
            verify(mockView, times(1)).showError(eq(R.string.please_try_again));
            assertThat(response, is(mockResponse));
            assertEquals(false, presenter.isEnableNextPage());
            return true;
        });
    }

    @Test
    public void searchNearbyAPIKeyInvalid() throws Exception {
        NearbySearchDao mockResult = jsonUtil.getJsonToMock(
                "nearby_search_empty_result.json",
                NearbySearchDao.class);
        Response<NearbySearchDao> mockResponse = Response.success(mockResult);
        Observable<Response<NearbySearchDao>> mockObservable = Observable.just(mockResponse);
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockDisposable, times(1)).add(anyObject());

        TestObserver<Response<NearbySearchDao>> testObserver = mockObservable.test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).loadMoreComplete();
            verify(mockView, times(1)).showError(eq(R.string.please_try_again));
            assertThat(response, is(mockResponse));
            assertEquals(false, presenter.isEnableNextPage());
            return true;
        });
    }

    @Test
    public void searchNearbyError() throws Exception {
        Response<NearbySearchDao> mockResponse = Response.error(500, responseBody);
        Observable<Response<NearbySearchDao>> mockObservable = Observable.just(mockResponse);
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockDisposable, times(1)).add(anyObject());

        TestObserver<Response<NearbySearchDao>> testObserver = mockObservable.test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).loadMoreError();
            verify(mockView, times(1)).showError(eq(response.message()));
            assertThat(response, is(mockResponse));
            assertEquals(false, presenter.isEnableNextPage());
            return true;
        });
    }

    private List<ResultNearbySearchDao> getListNameEn() {
        List<ResultNearbySearchDao> list = new ArrayList<>();

        ResultNearbySearchDao dao = new ResultNearbySearchDao();
        dao.setName("A");
        list.add(dao);

        ResultNearbySearchDao dao1 = new ResultNearbySearchDao();
        dao1.setName("F");
        list.add(dao1);

        ResultNearbySearchDao dao2 = new ResultNearbySearchDao();
        dao2.setName("B");
        list.add(dao2);

        ResultNearbySearchDao dao3 = new ResultNearbySearchDao();
        dao3.setName("D");
        list.add(dao3);

        ResultNearbySearchDao dao4 = new ResultNearbySearchDao();
        dao4.setName("C");
        list.add(dao4);

        ResultNearbySearchDao dao5 = new ResultNearbySearchDao();
        dao5.setName("a");
        list.add(dao5);

        return list;
    }

    private List<ResultNearbySearchDao> getListNameTh() {
        List<ResultNearbySearchDao> list = new ArrayList<>();

        ResultNearbySearchDao dao = new ResultNearbySearchDao();
        dao.setName("y");
        list.add(dao);

        ResultNearbySearchDao dao1 = new ResultNearbySearchDao();
        dao1.setName("ก");
        list.add(dao1);

        ResultNearbySearchDao dao2 = new ResultNearbySearchDao();
        dao2.setName("ค");
        list.add(dao2);

        ResultNearbySearchDao dao3 = new ResultNearbySearchDao();
        dao3.setName("ข");
        list.add(dao3);

        ResultNearbySearchDao dao4 = new ResultNearbySearchDao();
        dao4.setName("z");
        list.add(dao4);

        ResultNearbySearchDao dao5 = new ResultNearbySearchDao();
        dao5.setName("ง");
        list.add(dao5);

        return list;
    }
}