package com.chartianz.ipocheckkar.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {

    /**
     * Show the soft keyboard for a given view.
     *
     * @param context The context.
     * @param view    The view for which the keyboard should be shown.
     */
    public static void showKeyboard(Context context, View view) {
        if (context == null || view == null) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Hide the soft keyboard for a given view.
     *
     * @param context The context.
     * @param view    The view for which the keyboard should be hidden.
     */
    public static void hideKeyboard(Context context, View view) {
        if (context == null || view == null) {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}