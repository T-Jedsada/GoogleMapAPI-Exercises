package pondthaitay.googlemapapi.exercises.ui.main;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;

import static org.powermock.api.mockito.PowerMockito.spy;

public class MainPresenterTest {

    @Mock
    MainInterface.View mockView;

    private MainPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new MainPresenter();
        presenter.attachView(mockView);

        MainPresenter spyPresenter = spy(presenter);
        spyPresenter.attachView(mockView);
    }

    @Test
    public void onViewCreate() throws Exception {
        presenter.onViewCreate();
    }

    @Test
    public void onViewDestroy() throws Exception {
        presenter.onViewDestroy();
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