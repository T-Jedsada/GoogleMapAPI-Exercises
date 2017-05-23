package pondthaitay.googlemapapi.exercises.ui;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

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
import pondthaitay.googlemapapi.exercises.ui.base.BaseActivity;
import pondthaitay.googlemapapi.exercises.utils.DialogUtil;
import timber.log.Timber;

@RuntimePermissions
public class MainActivity extends BaseActivity<MainPresenter> implements MainInterface.View, OnMapReadyCallback {

    private static final int LOCATION_REQUEST_CODE = 2001;
    private static final int LOCATION_NOTIFICATION_ID = 2000;

    @Inject
    SharedPreferences sharedPreferences;
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
        return R.layout.activity_main;
    }

    @Override
    protected void doInjection(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected void startView() {
        getPresenter().onViewStart();
        MainActivityPermissionsDispatcher.requestPermissionWithCheck(this);
    }

    @Override
    protected void stopView() {
        getPresenter().onViewStop();
    }

    @Override
    protected void bindView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        btnSearch.setOnClickListener(v ->
                Timber.e("Location : %s", getCenterLatLngPosition().toString()));
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
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        configGoogleMap();
    }

    private boolean isLocationEnable() {
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private LatLng getCenterLatLngPosition() {
        return mGoogleMap.getCameraPosition().target;
    }

    private void configGoogleMap() {
        mGoogleMap.setTrafficEnabled(true);
        enableMyLocationMap();
        moveCameraToMyLocation();
        progressBar.setVisibility(View.INVISIBLE);
        btnSearch.setVisibility(View.VISIBLE);
    }

    private void enableMyLocationMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        if (mGoogleMap != null) mGoogleMap.setMyLocationEnabled(true);
    }

    private void moveCameraToMyLocation() {
        Location location = getLastKnownLocation();
        if (isLocationEnable() && location != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 16));
        }
    }

    private void createLocationNotification(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, LOCATION_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_settings)
                .setSound(alarmSound)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 400})
                .setColor(ContextCompat.getColor(context, R.color.notification_color))
                .setLights(ContextCompat.getColor(context, R.color.colorPrimaryDark), 1000, 1000)
                .setContentTitle(context.getString(R.string.enable_location_service))
                .setContentText(context.getString(R.string.open_location_settings))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(LOCATION_NOTIFICATION_ID, mBuilder.build());
    }
}