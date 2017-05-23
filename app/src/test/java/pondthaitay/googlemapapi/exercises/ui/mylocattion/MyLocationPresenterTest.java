package pondthaitay.googlemapapi.exercises.ui.mylocattion;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.service.GoogleMapApi;
import pondthaitay.googlemapapi.exercises.utils.JsonMockUtility;
import pondthaitay.googlemapapi.exercises.utils.RxSchedulersOverrideRule;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, CompositeDisposable.class})
public class MyLocationPresenterTest {
    @Rule
    public final RxSchedulersOverrideRule schedulers = new RxSchedulersOverrideRule();
    @Mock
    private MyLocationInterface.View mockView;
    @Mock
    private CompositeDisposable mockDisposable;
    @Mock
    private GoogleMapApi mockGoogleMapApi;

    private MyLocationPresenter presenter;
    private JsonMockUtility jsonUtil;
    private ResponseBody responseBody;

    @Before
    public void setUp() throws Exception {
        jsonUtil = new JsonMockUtility();
        MockitoAnnotations.initMocks(this);
        presenter = new MyLocationPresenter(mockGoogleMapApi);
        presenter.attachView(mockView);
        presenter.setDisposables(mockDisposable);

        MyLocationPresenter spyPresenter = spy(presenter);
        spyPresenter.setDisposables(mockDisposable);
        spyPresenter.attachView(mockView);

        responseBody = ResponseBody.create(MediaType.parse("application/json"), "");
    }

    @After
    public void destroy() {
        presenter.detachView();
    }

    @Test
    public void onViewCreate() throws Exception {
        presenter.onViewCreate();
    }

    @Test
    public void onViewDestroy() throws Exception {
        presenter.onViewDestroy();
        verify(mockDisposable, times(1)).clear();
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
    public void searchNearbySuccess() throws Exception {
        NearbySearchDao mockResult = jsonUtil.getJsonToMock(
                "nearby_search_success.json",
                NearbySearchDao.class);

        Response<NearbySearchDao> mockResponse = Response.success(mockResult);
        Observable<Response<NearbySearchDao>> mockObservable = Observable.just(mockResponse);
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString())).thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockView, times(1)).showProgressDialog();
        verify(mockDisposable, times(1)).add(anyObject());

        TestObserver<Response<NearbySearchDao>> testObserver = mockObservable.test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).hideProgressDialog();
            verify(mockView, times(1)).loadNearbySearchSuccess(eq(mockResult));
            assertThat(response, is(mockResponse));
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
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString())).thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockView, times(1)).showProgressDialog();
        verify(mockDisposable, times(1)).add(anyObject());

        TestObserver<Response<NearbySearchDao>> testObserver = mockObservable.test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).hideProgressDialog();
            verify(mockView, times(1)).showError(eq(R.string.please_try_again));
            assertThat(response, is(mockResponse));
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
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString())).thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockView, times(1)).showProgressDialog();
        verify(mockDisposable, times(1)).add(anyObject());

        TestObserver<Response<NearbySearchDao>> testObserver = mockObservable.test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).hideProgressDialog();
            verify(mockView, times(1)).showError(eq(R.string.please_try_again));
            assertThat(response, is(mockResponse));
            return true;
        });
    }

    @Test
    public void searchNearbyError() throws Exception {
        Response<NearbySearchDao> mockResponse = Response.error(500, responseBody);
        Observable<Response<NearbySearchDao>> mockObservable = Observable.just(mockResponse);
        when(mockGoogleMapApi.nearbySearch(anyString(), anyInt(), anyString())).thenReturn(mockObservable);
        presenter.searchNearby("location", 500, "key");
        verify(mockView, times(1)).showProgressDialog();
        verify(mockDisposable, times(1)).add(anyObject());

        TestObserver<Response<NearbySearchDao>> testObserver = mockObservable.test();
        testObserver.awaitTerminalEvent();
        testObserver.assertValue(response -> {
            verify(mockView, times(1)).hideProgressDialog();
            verify(mockView, times(1)).showError(eq(response.message()));
            assertThat(response, is(mockResponse));
            return true;
        });
    }
}