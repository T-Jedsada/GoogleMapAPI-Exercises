package pondthaitay.googlemapapi.exercises.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import pondthaitay.googlemapapi.exercises.R;

public class DialogUtil {

    public void showDialog(@NonNull Context context, @StringRes int title, @StringRes int message
            , DialogInterface.OnClickListener callback) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.btn_ok), callback)
                .setNegativeButton(context.getString(R.string.btn_cancel), null)
                .create()
                .show();
    }

    public void showDialogWarning(@NonNull Context context, @StringRes int title, @StringRes int message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.btn_ok), null)
                .create()
                .show();
    }
}
