package pondthaitay.googlemapapi.exercises.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import pondthaitay.googlemapapi.exercises.ApplicationComponent;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;
import pondthaitay.googlemapapi.exercises.ui.base.BaseActivity;
import pondthaitay.googlemapapi.exercises.ui.main.adapter.ListPlaceAdapter;
import pondthaitay.googlemapapi.exercises.ui.view.SlidingUpPanelLayout;

public class MainActivity extends BaseActivity<MainPresenter> implements
        MainInterface.View, OnMapReadyCallback, ListPlaceAdapter.LisPlaceListener {

    @Inject
    SharedPreferences spf;

    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @BindView(R.id.tv_show_list)
    AppCompatTextView tvShowList;
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.iv_sort)
    AppCompatImageView ivSort;

    private static final String KEY_DATA = "nearby_search_data";
    private static final String KEY_STATE_SORT = "state_sort";
    private NearbySearchDao nearbySearchDao;
    private GoogleMap mGoogleMap;
    private ListPlaceAdapter listPlaceAdapter;

    @Override
    protected int layoutToInflate() {
        return R.layout.activity_main;
    }

    @Override
    protected void doInjection(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected void startView() {

    }

    @Override
    protected void stopView() {

    }

    @Override
    protected void bindView() {

    }

    @NonNull
    private SlidingUpPanelLayout.PanelSlideListener getSlideListener() {
        return new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                ivSort.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                setStateSort(newState == SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        };
    }

    @Override
    protected void setupInstance() {
        listPlaceAdapter = new ListPlaceAdapter();
        listPlaceAdapter.setListener(this);
    }

    @Override
    protected void setupView() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        slidingUpPanelLayout.addPanelSlideListener(getSlideListener());
        slidingUpPanelLayout.setAnchorPoint(0.6f);
        slidingUpPanelLayout.setFadeOnClickListener(v ->
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));
        ivSort.setAlpha(0f);
        ivSort.setEnabled(false);
        ivSort.setOnClickListener(v -> {
            nearbySearchDao.setList(getPresenter().sortListByName(nearbySearchDao.getList()));
            listPlaceAdapter.setData(nearbySearchDao, getLastKnownLocation(), !TextUtils.isEmpty(nearbySearchDao.getNextPageToken()));
        });
        list.setAdapter(listPlaceAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setHasFixedSize(false);
    }

    @Override
    protected void initialize() {
        nearbySearchDao = Parcels.unwrap(getIntent().getParcelableExtra(KEY_DATA));
        getPresenter().setNearbySearchDao(nearbySearchDao);
        listPlaceAdapter.setData(nearbySearchDao,
                getLastKnownLocation(),
                !TextUtils.isEmpty(nearbySearchDao.getNextPageToken()));
    }

    @Override
    protected void saveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_DATA, Parcels.wrap(listPlaceAdapter.getData()));
        outState.putBoolean(KEY_STATE_SORT, ivSort.isEnabled());
    }

    @Override
    public void restoreView(Bundle savedInstanceState) {
        setStateSort(!savedInstanceState.getBoolean(KEY_STATE_SORT, false));
        nearbySearchDao = Parcels.unwrap(savedInstanceState.getParcelable(KEY_DATA));
        listPlaceAdapter.setData(nearbySearchDao,
                getLastKnownLocation(), false);
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setTrafficEnabled(true);
        enableMyLocationMap(mGoogleMap);
        moveCameraToMyLocation(mGoogleMap);
        setupMarker();
    }

    @Override
    public void loadMore() {
        getPresenter().searchNearby(nearbySearchDao.getTargetLoc(),
                500, getString(R.string.google_maps_key));
    }

    @Override
    public void onPlaceItemClick(ResultNearbySearchDao dao) {
        LatLng latLng = new LatLng(dao.getGeometryDao().getLocation().getLat(),
                dao.getGeometryDao().getLocation().getLng());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void loadMoreError() {
        listPlaceAdapter.setNextItemAvailable(false);
    }

    @Override
    public void loadMoreSuccess(List<ResultNearbySearchDao> list) {
        listPlaceAdapter.addNewPlace(list, true);
    }

    @Override
    public void loadMoreComplete() {
        listPlaceAdapter.setNextItemAvailable(false);
    }

    private void setupMarker() {
        if (nearbySearchDao != null) {
            int index = 0;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ResultNearbySearchDao dao : nearbySearchDao.getList()) {
                LatLng latlng = new LatLng(dao.getGeometryDao().getLocation().getLat(),
                        dao.getGeometryDao().getLocation().getLng());
                MarkerOptions markerOptions;
                markerOptions = new MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory.defaultMarker());
                Marker marker = mGoogleMap.addMarker(markerOptions);
                marker.setTag(index);
                builder.include(markerOptions.getPosition());
                index++;
            }

            mGoogleMap.setOnMapLoadedCallback(() ->
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50)));
        }
    }

    private void setStateSort(boolean state) {
        tvShowList.setText(state ? R.string.show_list : R.string.sort_by_name);
        ivSort.setEnabled(!state);
        ivSort.setAlpha(state ? 0f : 1f);
    }
}