package pondthaitay.googlemapapi.exercises.ui;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.parceler.Parcels;

import pondthaitay.googlemapapi.exercises.ApplicationComponent;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.ui.base.BaseActivity;

public class MainActivity extends BaseActivity<MainPresenter> implements MainInterface.View, OnMapReadyCallback {

    private NearbySearchDao nearbySearchDao;
    private GoogleMap mGoogleMap;

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
}