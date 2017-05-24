package pondthaitay.googlemapapi.exercises.ui.base;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pondthaitay.googlemapapi.exercises.ApplicationComponent;
import pondthaitay.googlemapapi.exercises.MyApplication;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.ui.base.exception.MvpNotSetLayoutException;
import pondthaitay.googlemapapi.exercises.ui.base.exception.MvpPresenterNotCreateException;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity
        implements BaseInterface.View {

    private static final int LOCATION_REQUEST_CODE = 2001;
    private static final int LOCATION_NOTIFICATION_ID = 2000;

    @Inject
    P presenter;

    @Nullable
    @BindView(android.R.id.content)
    android.view.View contentView;

    private ProgressDialog progressDialog;

    @LayoutRes
    protected abstract int layoutToInflate();

    protected abstract void doInjection(final ApplicationComponent component);

    protected abstract void startView();

    protected abstract void stopView();

    protected abstract void bindView();

    protected abstract void setupInstance();

    protected abstract void setupView();

    protected abstract void initialize();

    protected abstract void saveInstanceState(Bundle outState);

    public abstract void restoreView(Bundle savedInstanceState);

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layoutToInflate() == 0) throw new MvpNotSetLayoutException();
        setContentView(layoutToInflate());
        doInjection(((MyApplication) getApplication()).component());
        ButterKnife.bind(this);
        presenter.attachView(this);
        bindView();
        setupInstance();
        setupView();
        setupProgressDialog();
        getPresenter().onViewCreate();
        if (savedInstanceState == null) initialize();
    }

    @Override
    public P getPresenter() {
        if (presenter != null) return presenter;
        throw new MvpPresenterNotCreateException();
    }

    @Override
    public void showProgressDialog() {
        if (!progressDialog.isShowing()) progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    @Override
    public void showError(String errorMessage) {
        showSnackBar(errorMessage);
    }

    @Override
    public void showError(@StringRes int errorMessage) {
        showSnackBar(getString(errorMessage));
    }

    @Override
    public void showMessage(String message) {
        showSnackBar(message);
    }

    @Override
    public void showMessage(@StringRes int message) {
        showSnackBar(getString(message));
    }

    @Override
    public void unAuthorizedApi() {
        showSnackBar(getResources().getString(R.string.un_authorize_api));
    }

    @Override
    protected void onStart() {
        super.onStart();
        startView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreView(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) progressDialog.cancel();
        getPresenter().onViewDestroy();
        presenter.detachView();
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.loading));
    }

    private void showSnackBar(@NonNull String message) {
        if (contentView != null) Snackbar.make(contentView, message, Snackbar.LENGTH_SHORT).show();
    }

    protected boolean isLocationEnable() {
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    protected Location getLastKnownLocation() {
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

    protected void enableMyLocationMap(GoogleMap mGoogleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        if (mGoogleMap != null) mGoogleMap.setMyLocationEnabled(true);
    }

    protected void moveCameraToMyLocation(GoogleMap mGoogleMap) {
        Location location = getLastKnownLocation();
        if (isLocationEnable() && location != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }
    }

    protected void createLocationNotification(Context context) {
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