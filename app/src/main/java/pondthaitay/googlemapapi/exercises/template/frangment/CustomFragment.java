package pondthaitay.googlemapapi.exercises.template.frangment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.internal.Utils;
import pondthaitay.googlemapapi.exercises.ApplicationComponent;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.ui.base.BaseFragment;
import timber.log.Timber;

public class CustomFragment extends BaseFragment<CustomFragmentPresenter>
        implements CustomFragmentInterface.View {

    public static CustomFragment newInstance() {
        CustomFragment fragment = new CustomFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutToInflate() {
        return 0;
    }

    @Override
    protected void doInjection(ApplicationComponent component) {

    }

    @Override
    protected void startView() {

    }

    @Override
    protected void stopView() {

    }

    @Override
    protected void bindView(View view) {

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
}