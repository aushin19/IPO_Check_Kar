package com.chartianz.ipocheckkar.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class SwipeDistance {
    public static int dpToPixels(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
