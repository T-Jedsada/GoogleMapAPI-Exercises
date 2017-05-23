package pondthaitay.googlemapapi.exercises.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;

import javax.inject.Inject;

import pondthaitay.googlemapapi.exercises.ApplicationComponent;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;
import pondthaitay.googlemapapi.exercises.ui.base.BaseActivity;
import pondthaitay.googlemapapi.exercises.ui.main.adapter.ListPlaceAdapter;

public class MainActivity extends BaseActivity<MainPresenter> implements
        MainInterface.View, OnMapReadyCallback, ListPlaceAdapter.LisPlaceListener {

    @Inject
    SharedPreferences spf;

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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void setupInstance() {
        listPlaceAdapter = new ListPlaceAdapter();
        listPlaceAdapter.setData(nearbySearchDao, getLastKnownLocation());
        listPlaceAdapter.setListener(this);
    }

    @Override
    protected void setupView() {

    }

    @Override
    protected void initialize() {
        nearbySearchDao = Parcels.unwrap(getIntent().getParcelableExtra("nearby_search_data"));
    }

    @Override
    protected void saveInstanceState(Bundle outState) {
        outState.putParcelable("nearby_search_data", Parcels.wrap(nearbySearchDao));
    }

    @Override
    public void restoreView(Bundle savedInstanceState) {
        nearbySearchDao = Parcels.unwrap(savedInstanceState.getParcelable("nearby_search_data"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setTrafficEnabled(true);
        enableMyLocationMap(mGoogleMap);
        moveCameraToMyLocation(mGoogleMap);
    }

    @Override
    public void onPlaceItemClick(ResultNearbySearchDao dao) {
        LatLng latLng = new LatLng(dao.getGeometryDao().getLocation().getLat(),
                dao.getGeometryDao().getLocation().getLng());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }
}