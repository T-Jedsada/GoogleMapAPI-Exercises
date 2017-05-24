package pondthaitay.googlemapapi.exercises.ui.mylocattion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import pondthaitay.googlemapapi.exercises.ApplicationComponent;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.ui.main.MainActivity;
import pondthaitay.googlemapapi.exercises.ui.base.BaseActivity;
import pondthaitay.googlemapapi.exercises.utils.DialogUtil;

@RuntimePermissions
public class MyLocationActivity extends BaseActivity<MyLocationPresenter> implements
        MyLocationInterface.View, OnMapReadyCallback {

    @Inject
    DialogUtil dialogUtil;

    @BindView(R.id.progress_bar)
    View progressBar;
    @BindView(R.id.btn_search)
    AppCompatButton btnSearch;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;

    @Override
    protected int layoutToInflate() {
        return R.layout.activity_my_location;
    }

    @Override
    protected void doInjection(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected void startView() {
        getPresenter().onViewStart();
        MyLocationActivityPermissionsDispatcher.requestPermissionWithCheck(this);
    }

    @Override
    protected void stopView() {
        getPresenter().onViewStop();
    }

    @Override
    protected void bindView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        btnSearch.setOnClickListener(v -> getPresenter().searchNearby(getCenterLatLngPosition(),
                500, getString(R.string.google_maps_key)));
    }

    @Override
    protected void setupInstance() {

    }

    @Override
    protected void setupView() {

    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void saveInstanceState(Bundle outState) {

    }

    @Override
    public void restoreView(Bundle savedInstanceState) {

    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void requestPermission() {
        if (isLocationEnable()) {
            mapFragment.getMapAsync(this);
        } else {
            createLocationNotification(this);
        }
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void onPermissionDenied() {
        dialogUtil.showDialogWarning(this, R.string.permission_denied,
                R.string.title_permission_rationale);
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void onPermissionNeverAskAgain() {
        // nothing
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void showRationale(final PermissionRequest request) {
        dialogUtil.showDialog(this, R.string.title_permission_rationale, R.string.permission_rationale,
                (dialog, which) -> request.proceed());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MyLocationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setTrafficEnabled(true);
        enableMyLocationMap(mGoogleMap);
        moveCameraToMyLocation(mGoogleMap);
        progressBar.setVisibility(View.INVISIBLE);
        btnSearch.setVisibility(View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    private String getCenterLatLngPosition() {
        return String.format("%f,%f", mGoogleMap.getCameraPosition().target.latitude,
                mGoogleMap.getCameraPosition().target.longitude);
    }

    @Override
    public void loadNearbySearchSuccess(NearbySearchDao result) {
        result.setTargetLoc(getCenterLatLngPosition());
        startActivity(new Intent(this, MainActivity.class)
                .putExtra("nearby_search_data", Parcels.wrap(result)));
    }
}