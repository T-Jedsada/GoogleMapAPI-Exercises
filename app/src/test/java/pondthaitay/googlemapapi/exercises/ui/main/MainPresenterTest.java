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
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.service.GoogleMapApi;
import pondthaitay.googlemapapi.exercises.utils.JsonMockUtility;
import pondthaitay.googlemapapi.exercises.utils.RxSchedulersOverrideRule;
import pondthaitay.googlemapapi.exercises.utils.SortUtil;
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
    CompositeDisposable mockDisposable;

    private MainPresenter presenter;
    private JsonMockUtility jsonUtil;
    private ResponseBody responseBody;
    private SortUtil sortUtil;

    @Before
    public void setUp() throws Exception {
        jsonUtil = new JsonMockUtility();
        MockitoAnnotations.initMocks(this);
        sortUtil = new SortUtil();
        presenter = new MainPresenter(mockGoogleMapApi, sortUtil);
        presenter.attachView(mockView);
        presenter.setDisposables(mockDisposable);
        presenter.setSortUtil(sortUtil);

        MainPresenter spyPresenter = spy(presenter);
        spyPresenter.setDisposables(mockDisposable);
        spyPresenter.attachView(mockView);
        spyPresenter.setSortUtil(sortUtil);

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
    public void testSortListRxByNameEn() throws Exception {
        NearbySearchDao mockResult = jsonUtil.getJsonToMock(
                "nearby_search_success.json",
                NearbySearchDao.class);
        presenter.setNearbySearchDao(mockResult);
        presenter.sortListByNameRx(getListNameTh());
        verify(mockView, times(1)).showProgressDialog();
        TestObserver<List<ResultNearbySearchDao>> testObserver = sortUtil.sortListByRx(getListNameEn()).test();
        testObserver.assertComplete();
        testObserver.assertOf(list -> {
            String[] az = new String[6];
            String[] azActuals = {"A", "B", "C", "D", "F", "a"};
            for (int i = 0; i < list.values().get(0).size(); i++) {
                az[i] = list.values().get(0).get(i).getName();
            }
            verify(mockView, times(1)).hideProgressDialog();
            verify(mockView, times(1)).sortSuccess(eq(presenter.getNearbySearchDao()));
            Assert.assertArrayEquals(az, azActuals);
        });
    }

    @Test
    public void testSortListRxByNameTh() throws Exception {
        NearbySearchDao mockResult = jsonUtil.getJsonToMock(
                "nearby_search_success.json",
                NearbySearchDao.class);
        presenter.setNearbySearchDao(mockResult);
        presenter.sortListByNameRx(getListNameTh());
        verify(mockView, times(1)).showProgressDialog();
        TestObserver<List<ResultNearbySearchDao>> testObserver = sortUtil.sortListByRx(getListNameTh()).test();
        testObserver.assertComplete();
        testObserver.assertOf(list -> {
            String[] az = new String[6];
            String[] azActuals = {"y", "z", "ก", "ข", "ค", "ง"};
            for (int i = 0; i < list.values().get(0).size(); i++) {
                az[i] = list.values().get(0).get(i).getName();
            }
            verify(mockView, times(1)).hideProgressDialog();
            verify(mockView, times(1)).sortSuccess(eq(presenter.getNearbySearchDao()));
            Assert.assertArrayEquals(az, azActuals);
        });
    }

    @Test
    public void testSortListEmpty() throws Exception {
        List<ResultNearbySearchDao> list = presenter.sortListByName(new ArrayList<>());
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void searchNearbySuccess() throws Exception {
        setupNextPageToken();
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
            verify(mockView, times(1)).loadMoreSuccess(eq(mockResult));
            assertThat(response, is(mockResponse));
            assertEquals(true, presenter.isEnableNextPage());
            return true;
        });
    }

    @Test
    public void searchNearbyMoreSuccess() throws Exception {
        setupNextPageToken();
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
            verify(mockView, times(1)).loadMoreSuccess(eq(mockResult));
            assertThat(response, is(mockResponse));
            assertEquals(true, presenter.isEnableNextPage());
            return true;
        });
    }

    @Test
    public void searchNearbyEmpty() throws Exception {
        setupNextPageToken();
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
            assertThat(response, is(mockResponse));
            assertEquals(false, presenter.isEnableNextPage());
            return true;
        });
    }

    @Test
    public void searchNearbyAPIKeyInvalid() throws Exception {
        setupNextPageToken();
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
            assertThat(response, is(mockResponse));
            assertEquals(false, presenter.isEnableNextPage());
            return true;
        });
    }

    @Test
    public void searchNearbyError() throws Exception {
        setupNextPageToken();
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
            assertThat(response, is(mockResponse));
            assertEquals(false, presenter.isEnableNextPage());
            return true;
        });
    }

    @Test
    public void searchNearbyComplete() throws Exception {
        presenter.setNearbySearchDao(null);
        NearbySearchDao mockResult = jsonUtil.getJsonToMock(
                "nearby_search_empty_result.json",
                NearbySearchDao.class);
        Response<NearbySearchDao> mockResponse = Response.success(mockResult);
        Observable<Response<NearbySearchDao>> mockObservable = Observable.just(mockResponse);
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString(), anyString()))
                .thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockView, times(1)).loadMoreComplete();
    }

    private void setupNextPageToken() {
        NearbySearchDao nearbySearchDao = new NearbySearchDao();
        nearbySearchDao.setNextPageToken("jedsada");
        presenter.setNearbySearchDao(nearbySearchDao);
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