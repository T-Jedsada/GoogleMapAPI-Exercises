package pondthaitay.googlemapapi.exercises.template.activity;

import pondthaitay.googlemapapi.exercises.ui.base.BaseInterface;

class CustomActivityInterface {

    interface View extends BaseInterface.View {
        void testResult();
    }

    interface Presenter {
        void test();
    }
}
