package com.chartianz.ipocheckkar.ui.snackbar;

import android.view.View;

import com.chartianz.ipocheckkar.R;
import com.google.android.material.snackbar.Snackbar;

public class CustomSnackbar {
    public void showSnackbar(String message, View view) {
        View rootView = view;
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
        snackbar.setTextColor(view.getResources().getColor(R.color.background));
        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.setBackgroundTint(view.getResources().getColor(R.color.primary));
        snackbar.setActionTextColor(view.getResources().getColor(R.color.background));
        snackbar.show();
    }
}
