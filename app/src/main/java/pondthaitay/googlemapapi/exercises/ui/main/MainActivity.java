package pondthaitay.googlemapapi.exercises.ui.main;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

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
import pondthaitay.googlemapapi.exercises.utils.LocationDatabase;

public class MainActivity extends BaseActivity<MainPresenter> implements
        MainInterface.View, OnMapReadyCallback, ListPlaceAdapter.LisPlaceListener {

    @Inject
    LocationDatabase database;
    @Inject
    Gson gson;

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
    private static final String KEY_NEXT_PAGE_TOKEN = "key_next_page_token";
    private NearbySearchDao nearbySearchDao;
    private GoogleMap mGoogleMap;
    private ListPlaceAdapter adapter;
    private int id;

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
        adapter = new ListPlaceAdapter();
        adapter.setListener(this);
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
        ivSort.setOnClickListener(v -> getPresenter().sortListByNameRx(adapter.getData().getList()));
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setHasFixedSize(false);
    }

    @Override
    protected void initialize() {
        id = getIntent().getIntExtra("id", 0);
        nearbySearchDao = Parcels.unwrap(getIntent().getParcelableExtra(KEY_DATA));
        getPresenter().setNearbySearchDao(nearbySearchDao);
        adapter.setData(nearbySearchDao,
                getLastKnownLocation(),
                !TextUtils.isEmpty(nearbySearchDao.getNextPageToken()));
    }

    @Override
    protected void saveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_DATA, Parcels.wrap(adapter.getData()));
        outState.putString(KEY_NEXT_PAGE_TOKEN,
                getPresenter().getNearbySearchDao().getNextPageToken());
        outState.putBoolean(KEY_STATE_SORT, ivSort.isEnabled());
        outState.putInt("id", id);
    }

    @Override
    public void restoreView(Bundle savedInstanceState) {
        id = savedInstanceState.getInt("id", 0);
        String token = savedInstanceState.getString(KEY_NEXT_PAGE_TOKEN, "");
        setStateSort(!savedInstanceState.getBoolean(KEY_STATE_SORT, false));
        nearbySearchDao = Parcels.unwrap(savedInstanceState.getParcelable(KEY_DATA));
        nearbySearchDao.setNextPageToken(token);
        getPresenter().setNearbySearchDao(nearbySearchDao);
        adapter.setData(nearbySearchDao, getLastKnownLocation(),
                !TextUtils.isEmpty(nearbySearchDao.getNextPageToken()));
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
        setupMarker(nearbySearchDao);
    }

    @Override
    public void loadMore() {
        getPresenter().searchNearby(nearbySearchDao.getTargetLoc(),
                500, getString(R.string.google_maps_key));
    }

    @Override
    public void onPlaceItemClick(ResultNearbySearchDao dao) {
        Toast.makeText(this, dao.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadMoreError() {
        adapter.setNextItemAvailable(false);
        saveToLocalDatabase();
    }

    @Override
    public void loadMoreSuccess(NearbySearchDao nearbySearchDao) {
        adapter.addNewPlace(nearbySearchDao.getList(), true);
        setupNewMarker(nearbySearchDao.getList());
        saveToLocalDatabase();
    }

    @Override
    public void loadMoreComplete() {
        adapter.setNextItemAvailable(false);
        saveToLocalDatabase();
    }

    @Override
    public void sortSuccess(NearbySearchDao dao) {
        adapter.setData(dao, getLastKnownLocation(),
                !TextUtils.isEmpty(getPresenter().getTokenNextPage()));
    }

    private void setupMarker(NearbySearchDao nearbySearchDao) {
        if (nearbySearchDao != null) {
            int index = 0;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ResultNearbySearchDao dao : nearbySearchDao.getList()) {
                LatLng latlng = new LatLng(dao.getGeometryDao().getLocation().getLat(),
                        dao.getGeometryDao().getLocation().getLng());
                MarkerOptions markerOptions;
                markerOptions = new MarkerOptions().position(latlng)
                        .icon(index <= 20 ? BitmapDescriptorFactory.defaultMarker() :
                                BitmapDescriptorFactory.fromBitmap(createDot()));
                mGoogleMap.addMarker(markerOptions);
                builder.include(markerOptions.getPosition());
                index++;
            }
            mGoogleMap.setOnMapLoadedCallback(() ->
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50)));
        }
    }

    private void setupNewMarker(List<ResultNearbySearchDao> list) {
        if (mGoogleMap != null) {
            for (ResultNearbySearchDao dao : list) {
                LatLng latlng = new LatLng(dao.getGeometryDao().getLocation().getLat(),
                        dao.getGeometryDao().getLocation().getLng());
                MarkerOptions markerOptions;
                markerOptions = new MarkerOptions().position(latlng)
                        .icon(createDot() == null ? BitmapDescriptorFactory.defaultMarker() :
                                BitmapDescriptorFactory.fromBitmap(createDot()));
                mGoogleMap.addMarker(markerOptions);
            }
        }
    }

    private void setStateSort(boolean state) {
        tvShowList.setText(state ? R.string.show_list : R.string.sort_by_name);
        ivSort.setEnabled(!state);
        ivSort.setAlpha(state ? 0f : 1f);
    }

    @Nullable
    private Bitmap createDot() {
        int px = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size);
        Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mDotMarkerBitmap);
        Drawable shape = ContextCompat.getDrawable(this, R.drawable.oval_pin);
        if (shape != null) {
            shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
            shape.draw(canvas);
            return mDotMarkerBitmap;
        }
        return null;
    }

    private void saveToLocalDatabase() {
        NearbySearchDao dao = new NearbySearchDao();
        dao.setList(adapter.getData().getList());
        dao.setNextPageToken(getPresenter().getTokenNextPage());
        database.setJsonData(nearbySearchDao.getTargetLoc(), dao);
    }
}