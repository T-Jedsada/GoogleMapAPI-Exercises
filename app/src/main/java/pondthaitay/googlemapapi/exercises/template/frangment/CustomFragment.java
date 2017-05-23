package pondthaitay.googlemapapi.exercises.template.frangment;

import android.os.Bundle;
import android.view.View;

import pondthaitay.googlemapapi.exercises.ApplicationComponent;
import pondthaitay.googlemapapi.exercises.ui.base.BaseFragment;

public class CustomFragment extends BaseFragment<CustomPresenter>
        implements CustomInterface.View {

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