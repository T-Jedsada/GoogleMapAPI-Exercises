package pondthaitay.googlemapapi.exercises.ui.base.exception;

public class MvpPresenterNotCreateException extends RuntimeException {
    public MvpPresenterNotCreateException() {
        super("Please call createPresenter() before" + " requesting data to the Presenter");
    }
}

