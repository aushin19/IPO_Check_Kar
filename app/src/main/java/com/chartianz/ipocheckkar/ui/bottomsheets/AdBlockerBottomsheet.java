package com.chartianz.ipocheckkar.ui.bottomsheets;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.chartianz.ipocheckkar.R;
import com.chartianz.ipocheckkar.databinding.BottomsheetAdBlockerBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AdBlockerBottomsheet {
    public BottomSheetDialog dialog;
    public BottomsheetAdBlockerBinding binding;

    public void Show(Activity activity) {

        dialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);

        dialog.setCancelable(false);
        dialog.setDismissWithAnimation(true);
        binding = BottomsheetAdBlockerBinding.inflate(LayoutInflater.from(activity));
        dialog.setContentView(binding.getRoot());

        binding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        if(!activity.isFinishing())
        {
            dialog.show();
        }
    }

    public void dismiss(Context context) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });

    }

}
