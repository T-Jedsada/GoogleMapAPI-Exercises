package pondthaitay.googlemapapi.exercises.ui.base.exception;

public class MvpNotSetLayoutException extends RuntimeException {
    public MvpNotSetLayoutException() {
        super("getLayoutView() not return 0");
    }
}

