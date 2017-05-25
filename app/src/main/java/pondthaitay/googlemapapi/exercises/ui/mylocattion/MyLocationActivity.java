package pondthaitay.googlemapapi.exercises.ui.mylocattion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;

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
import pondthaitay.googlemapapi.exercises.ui.base.BaseActivity;
import pondthaitay.googlemapapi.exercises.ui.mylocattion.database.LocationDatabase;
import pondthaitay.googlemapapi.exercises.ui.mylocattion.database.LocationModel;
import pondthaitay.googlemapapi.exercises.utils.DialogUtil;
import timber.log.Timber;

@RuntimePermissions
public class MyLocationActivity extends BaseActivity<MyLocationPresenter> implements
        MyLocationInterface.View, OnMapReadyCallback {

    @Inject
    DialogUtil dialogUtil;
    @Inject
    LocationDatabase database;
    @Inject
    Gson gson;

    @BindView(R.id.progress_bar)
    View progressBar;
    @BindView(R.id.btn_search)
    AppCompatButton btnSearch;

    private static final String KEY_DATA = "nearby_search_data";
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

    @SuppressWarnings("unchecked")
    @Override
    public void loadNearbySearchSuccess(NearbySearchDao result) {
        result.setTargetLoc(getCenterLatLngPosition());
        LocationModel model = database.queryDataByLocation(result.getTargetLoc());

        if (model == null) {
            Timber.e("null");
            LocationModel locationModel = new LocationModel();
            locationModel.location = "18.774999,98.948900";
            locationModel.jsonData = gson.toJson(result);
            database.insertLocation(locationModel);
        }

        Timber.e(String.valueOf(database.queryIdByLocation(result.getTargetLoc()).jsonData));

//        startActivity(new Intent(this, MainActivity.class)
//                .putExtra(KEY_DATA, Parcels.wrap(result))
//                .putExtra("id", locationModel.getModelAdapter().getAutoIncrementingId(locationModel)));
    }

    @Override
    public void loadNearbySearchFromDB() {
//        LocationModel query = database.queryDataByLocation("18.774999,98.948900");
//        if (query != null) {
//            NearbySearchDao result = gson.fromJson(query.getJsonData(), NearbySearchDao.class);
//            startActivity(new Intent(this, MainActivity.class)
//                    .putExtra(KEY_DATA, Parcels.wrap(result)));
//        }
    }
}